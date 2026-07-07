package org.example.articles.tasks

import org.example.articles.model.Article
import org.example.articles.network.BlogService
import org.example.blog.ArticleInfo
import org.example.blog.Comment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.toList

// observeArticlesUnstableWithRetry is already implemented for you.
fun observeArticlesUnstableWithRetry(service: BlogService): Flow<Article> = flow {
    val list = service.getArticleInfoList()
    for (articleInfo in list) {
        emit(Article(articleInfo, getCommentsWithRetry(service, articleInfo)))
    }
}

suspend fun getCommentsWithRetry(service: BlogService, articleInfo: ArticleInfo): List<Comment> {
    return service.getCommentsUnstable(articleInfo) // TODO: Implement me correctly!
}
