package org.example.articles.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.articles.model.Article
import org.example.articles.network.BlogService
import org.example.articles.network.BlogServiceBlocking
import org.example.articles.tasks.*
import org.example.articles.ui.ArticlesViewModel.LoadingStatus.*
import org.example.articles.ui.LoadingMode.*
import org.example.articles.util.loadStoredMode
import org.example.articles.util.removeStoredMode
import org.example.articles.util.saveParams
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class ArticlesViewModel(
    private val blockingService: BlogServiceBlocking,
    private val service: BlogService,
) : ViewModel() {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        markLoadingCompletion(exception)
    }

    // Task: Use SupervisorJob for the dedicated loadingScope.
    // Replace Job(...) with SupervisorJob(...) so a failed unstable-network load, such as loading the
    // article list, doesn't permanently cancel the loadingScope and break later loading attempts.
    // initial code: Job(viewModelScope.coroutineContext.job)
    private val loadingScope =
        CoroutineScope(Job(viewModelScope.coroutineContext.job) + Dispatchers.Main)

    var loadingMode by mutableStateOf(BLOCKING)
        private set

    fun changeLoadingMode(loadingMode: LoadingMode) {
        this.loadingMode = loadingMode
    }

    enum class LoadingStatus { NOT_STARTED, IN_PROGRESS, COMPLETED, COMPLETED_WITH_ERRORS, CANCELED, FAILED }

    var loadingStatus by mutableStateOf(NOT_STARTED)
        private set

    var currentLoadingTime by mutableStateOf(Duration.ZERO)
        private set

    var newLoadingEnabled by mutableStateOf(true)
        private set

    var cancellationEnabled by mutableStateOf(false)
        private set

    private var loadingJob by mutableStateOf<Job?>(null)

    val articlesFlow: StateFlow<List<Article>>
        field = MutableStateFlow(listOf())

    val failuresFlow: StateFlow<List<String>>
        field = MutableStateFlow(listOf())

    init {
        try {
            changeLoadingMode(loadStoredMode())
        } catch (e: Exception) {
            e.printStackTrace()
            removeStoredMode()
        }
    }

    fun loadComments() {
        saveParams(loadingMode)
        clearResults()
        cancellationEnabled = true
        loadingJob = loadingScope.launch(coroutineExceptionHandler) {
            val startTime = TimeSource.Monotonic.markNow()
            when (loadingMode) {
                BLOCKING -> {
                    val articleList = loadArticles(blockingService)
                    updateResults(articleList, startTime)
                }

                SUSPENDING -> {
                    val articleList = loadArticles(service)
                    updateResults(articleList, startTime)
                }

                CONCURRENT -> {
                    val articleList = loadArticlesConcurrently(service)
                    updateResults(articleList, startTime)
                }

                NON_CANCELABLE -> {
                    val articleList = loadArticlesNonCancelable(service)
                    updateResults(articleList, startTime)
                }

                WITH_PROGRESS -> {
                    val articleFlow = observeArticlesLoading(service)
                    updateResultsWithProgress(articleFlow, startTime)
                }

                CONCURRENT_WITH_PROGRESS -> {
                    val articleFlow = observeArticlesConcurrently(service)
                    updateResultsWithProgress(articleFlow, startTime)
                }

                UNSTABLE_NETWORK -> {
                    val articleResults = observeArticlesUnstable(service)
                    updateResultsWithFailures(articleResults, startTime)
                }
            }
        }
    }

    private fun updateResults(
        articles: List<Article>,
        startTime: TimeMark,
        completed: Boolean = true,
    ) {
        loadingStatus = if (completed) COMPLETED else IN_PROGRESS
        currentLoadingTime = startTime.elapsedNow()
        articlesFlow.value = articles
        if (completed) {
            markLoadingCompletion()
        }
    }

    private suspend fun updateResultsWithProgress(
        articleFlow: Flow<Article>,
        startTime: TimeMark,
        completion: (Throwable?) -> Unit = ::markLoadingCompletion,
    ) {
        articleFlow
            .runningFold(listOf<Article>()) { list, article -> list + article }
            .onEach { updateResults(it, startTime, completed = false) }
            .onCompletion { completion(it) }
            .collect()
    }

    private suspend fun updateResultsWithFailures(
        articleResultFlow: Flow<ArticleResult>,
        startTime: TimeMark,
    ) {
        val failures = mutableListOf<String>()
        val articleFlow = articleResultFlow
            .onEach { result ->
                if (result is ArticleResult.Failure) {
                    failures += "Getting comments for ${result.articleInfo.id} failed: ${result.exception.abbreviatedReason()}"
                    failuresFlow.value = failures.toList()
                }
            }
            .filterIsInstance<ArticleResult.Success>()
            .map { it.article }

        updateResultsWithProgress(articleFlow, startTime) { throwable ->
            currentLoadingTime = startTime.elapsedNow()
            if (throwable != null && throwable !is CancellationException) {
                failures += "Loading failed: ${throwable.abbreviatedReason()}"
                failuresFlow.value = failures.toList()
            }
            markLoadingCompletion(throwable)
        }
    }

    private fun Throwable.abbreviatedReason(): String =
        message?.lineSequence()?.firstOrNull()?.take(80) ?: this::class.simpleName.orEmpty()

    private fun markLoadingCompletion(throwable: Throwable? = null) {
        loadingStatus = when {
            throwable is CancellationException -> CANCELED
            throwable != null -> FAILED
            loadingMode == UNSTABLE_NETWORK && failuresFlow.value.isNotEmpty() -> COMPLETED_WITH_ERRORS
            else -> COMPLETED
        }
        newLoadingEnabled = true
        cancellationEnabled = false
    }

    fun cancelLoading() {
        loadingJob?.cancel()
        loadingStatus = CANCELED
        newLoadingEnabled = true
        cancellationEnabled = false
        currentLoadingTime = Duration.ZERO
    }

    private fun clearResults() {
        articlesFlow.value = listOf()
        failuresFlow.value = listOf()
        loadingStatus = IN_PROGRESS
        newLoadingEnabled = false
        cancellationEnabled = false
        currentLoadingTime = Duration.ZERO
    }
}
