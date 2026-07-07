package org.example.articles.exercises

import org.example.articles.tasks.observeArticlesLoading
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SequentialTest {
    // Task: Write a test that collects the articles emitted by observeArticlesLoading and
    // asserts they match the expected articles, in order.
    @Test
    fun `test observeArticlesLoading Result`() = runTest {
        TODO()
    }

    // Task: Write a test that asserts the virtual time observeArticlesLoading takes to complete
    // matches the expected sequential duration.
    @Test
    fun `test observeArticlesLoading Duration`() = runTest {
        TODO()
    }
}
