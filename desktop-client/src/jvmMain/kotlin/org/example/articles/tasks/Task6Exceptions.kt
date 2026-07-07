package org.example.articles.tasks

import org.example.articles.model.Article
import org.example.articles.network.BlogService
import org.example.articles.network.NetworkException
import org.example.blog.ArticleInfo
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*

sealed interface ArticleResult {
    data class Success(val article: Article) : ArticleResult
    data class Failure(val articleInfo: ArticleInfo, val exception: Throwable) : ArticleResult
}

// Task: Catch NetworkException for each article so one failed article does not stop loading the others.
// Let failures while loading the article list propagate from the flow uncaught.
fun observeArticlesUnstable(service: BlogService): Flow<ArticleResult> = channelFlow {
    val articleInfoList = service.getArticleInfoListUnstable()
    articleInfoList.forEach { articleInfo ->
        launch {
            val result = try {
                ArticleResult.Success(Article(articleInfo, service.getCommentsUnstable(articleInfo)))
            } catch (exception: NetworkException) {
                ArticleResult.Failure(articleInfo, exception)
            }
            send(result)
        }
    }
}