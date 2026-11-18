package org.example.articles.data

import org.example.articles.model.Article
import org.example.articles.network.NetworkException
import org.example.articles.tasks.ArticleResult
import org.example.data.ArticlesFakeData
import kotlin.time.Duration

data class TestResults(val duration: Duration, val list: List<Article>)
object ArticlesFakeDataResults {
    private fun sequentialDuration() =
        ArticlesFakeData.getArticles()
            .fold(Duration.ZERO) { total, articleInfo -> total + ArticlesFakeData.getDelay(articleInfo.id) }

    private fun concurrentDuration() =
        ArticlesFakeData.getArticles()
            .maxOf { articleInfo -> ArticlesFakeData.getDelay(articleInfo.id) }

    private fun getArticles() =
        ArticlesFakeData.getArticles()
            .map { articleInfo -> Article(articleInfo, ArticlesFakeData.getComments(articleInfo.id)) }

    fun expectedUnstableResults(failingArticleIds: Set<Int>): List<ArticleResult> =
        ArticlesFakeData.getArticles().map { articleInfo ->
            if (articleInfo.id in failingArticleIds) {
                ArticleResult.Failure(articleInfo, NetworkException("Network error"))
            } else {
                ArticleResult.Success(Article(articleInfo, ArticlesFakeData.getComments(articleInfo.id)))
            }
        }

    fun expectedUnstableDuration(failingArticleIds: Set<Int>): Duration =
        ArticlesFakeData.getArticles()
            .filterNot { it.id in failingArticleIds }
            .maxOfOrNull { ArticlesFakeData.getDelay(it.id) } ?: Duration.ZERO

    val expectedSequentialList by lazy {
        TestResults(
            duration = sequentialDuration(),
            list = getArticles()
        )
    }

    val expectedConcurrentList by lazy {
        TestResults(
            duration = concurrentDuration(),
            list = getArticles(),
        )
    }
}