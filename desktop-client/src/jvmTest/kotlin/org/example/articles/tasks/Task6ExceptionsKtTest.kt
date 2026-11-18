package org.example.articles.tasks

import app.cash.turbine.test
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import org.example.articles.data.ArticlesFakeDataResults
import org.example.articles.data.UnstableMockBlogService
import org.example.articles.network.NetworkException
import org.example.data.ArticlesFakeData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.measureTime

class Task6ExceptionsKtTest {
    private val failingArticleIds = setOf(2, 5)

    @Test
    fun `test observeArticlesUnstable results`() = runTest {
        val results = mutableListOf<ArticleResult>()
        observeArticlesUnstable(UnstableMockBlogService(failingArticleIds)).test {
            repeat(ArticlesFakeData.getArticles().size) {
                results += awaitItem()
            }
            awaitComplete()
        }

        val expected = ArticlesFakeDataResults.expectedUnstableResults(failingArticleIds)
        assertEquals(
            expected.filterIsInstance<ArticleResult.Success>().map { it.article }.toSet(),
            results.filterIsInstance<ArticleResult.Success>().map { it.article }.toSet(),
        )
        val failures = results.filterIsInstance<ArticleResult.Failure>()
        assertEquals(
            expected.filterIsInstance<ArticleResult.Failure>().map { it.articleInfo.id }.toSet(),
            failures.map { it.articleInfo.id }.toSet(),
        )
        assertTrue(failures.all { it.exception is NetworkException })
        assertEquals(ArticlesFakeData.getArticles().size, results.size)
    }

    @Test
    fun `test observeArticlesUnstable duration`() = runTest {
        val totalTime = testScheduler.timeSource.measureTime {
            observeArticlesUnstable(UnstableMockBlogService(failingArticleIds)).test {
                repeat(ArticlesFakeData.getArticles().size) { awaitItem() }
                awaitComplete()
            }
        }
        val upperBound = ArticlesFakeDataResults.expectedUnstableDuration(failingArticleIds)
        assertTrue(
            totalTime <= upperBound,
            "Unstable loading took $totalTime of virtual time, expected at most $upperBound"
        )
    }

    @Test
    fun `test observeArticlesUnstable propagates article list failure`() = runTest {
        val exception = runCatching {
            observeArticlesUnstable(UnstableMockBlogService(failArticleList = true)).collect {}
        }.exceptionOrNull()
        assertIs<NetworkException>(exception)
    }
}