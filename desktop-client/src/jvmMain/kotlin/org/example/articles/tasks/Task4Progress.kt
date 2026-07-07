package org.example.articles.tasks

import org.example.articles.model.Article
import org.example.articles.network.BlogService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Task: Implement loading of articles and comments using flows (with progress!)
fun observeArticlesLoading(service: BlogService): Flow<Article> = flow {
    val list = service.getArticleInfoList()
    for (articleInfo in list) {
        emit(Article(articleInfo, service.getComments(articleInfo)))
    }
}