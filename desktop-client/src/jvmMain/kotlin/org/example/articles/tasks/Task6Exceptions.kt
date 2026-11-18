package org.example.articles.tasks

import org.example.articles.model.Article
import org.example.articles.network.BlogService
import org.example.articles.network.NetworkException
import org.example.blog.ArticleInfo
import kotlinx.coroutines.flow.*

sealed interface ArticleResult {
    data class Success(val article: Article) : ArticleResult
    data class Failure(val articleInfo: ArticleInfo, val exception: Throwable) : ArticleResult
}

// Task: Catch NetworkException for each article so one failed article does not stop loading the others.
// Let failures while loading the article list propagate from the flow uncaught.
fun observeArticlesUnstable(service: BlogService): Flow<ArticleResult> = flow {
    val articleInfoList = service.getArticleInfoListUnstable()
    articleInfoList.forEach { articleInfo ->
        val comments = service.getCommentsUnstable(articleInfo)
        emit(ArticleResult.Success(Article(articleInfo, comments)))
    }
}