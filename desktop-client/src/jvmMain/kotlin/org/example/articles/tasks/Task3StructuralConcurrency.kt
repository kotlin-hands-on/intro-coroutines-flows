package org.example.articles.tasks

import org.example.articles.model.Article
import org.example.articles.network.BlogService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

// Run both versions and try to cancel
suspend fun loadArticlesNonCancellable(service: BlogService): List<Article> {
    TODO()
}