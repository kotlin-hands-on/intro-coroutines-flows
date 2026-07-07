package org.example.articles.tasks

import org.example.articles.model.Article
import org.example.articles.network.BlogService
import kotlinx.coroutines.flow.*

fun observeArticlesUnstable(service: BlogService): Flow<Article> = flow {
    val list = service.getArticleInfoList()
    for (articleInfo in list) {
        emit(Article(articleInfo, service.getCommentsUnstable(articleInfo)))
    }
}