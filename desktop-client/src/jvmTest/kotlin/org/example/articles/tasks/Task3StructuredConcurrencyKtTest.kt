package org.example.articles.tasks

import org.example.articles.data.ArticlesFakeDataResults
import org.example.articles.data.MockBlogService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class Task3StructuredConcurrencyKtTest {
    @Test
    fun `test loadArticlesNonCancellable Result`() = runTest {
        val result = loadArticlesNonCancellable(MockBlogService)
        assertEquals(
            expected = ArticlesFakeDataResults.expectedConcurrentList.list,
            actual = result,
            message = "Wrong result for 'loadArticlesNonCancellable'"
        )
    }

    @Test
    fun `test loadArticlesConcurrently Cancellation`() = runTest {
        val startTime = currentTime
        val job = launch {
            loadArticlesConcurrently(MockBlogService)
        }
        delay(1.seconds)
        job.cancel()
        job.join()

        assertTrue(job.isCancelled, "Job should be cancelled")

        val totalTime = currentTime - startTime
        val uncancelledDuration = ArticlesFakeDataResults.expectedConcurrentList.duration.inWholeMilliseconds
        assertTrue(
            totalTime < uncancelledDuration,
            "Cancelling after 1s of virtual time should stop the remaining requests instead of " +
                    "letting loading run for the full $uncancelledDuration ms, but took $totalTime ms"
        )
    }
}
