package org.example.articles.tasks

import org.example.articles.model.Article
import org.example.articles.network.BlogService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

// Task: Implement concurrent loading of articles
suspend fun loadArticlesConcurrently(service: BlogService): List<Article> = coroutineScope {
    TODO()
}