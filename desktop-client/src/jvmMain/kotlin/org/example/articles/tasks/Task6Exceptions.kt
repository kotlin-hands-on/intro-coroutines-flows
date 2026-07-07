package org.example.articles.tasks

import org.example.articles.model.Article
import org.example.articles.network.BlogService
import org.example.articles.network.NetworkException
import kotlinx.coroutines.flow.*

fun observeArticlesUnstable(service: BlogService): Flow<Article> = flow {
    throw NetworkException("Loading articles was unsuccessful.")
}