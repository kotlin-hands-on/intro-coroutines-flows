package org.example.articles.data

import org.example.articles.network.BlogService
import org.example.articles.network.NetworkException
import org.example.blog.ArticleInfo
import org.example.data.ArticlesFakeData
import org.example.blog.Comment
import kotlinx.coroutines.delay
import kotlin.random.Random

object MockBlogService: BlogService {
    override suspend fun getArticleInfoList(): List<ArticleInfo> {
        return ArticlesFakeData.getArticles()
    }

    override suspend fun getComments(articleInfo: ArticleInfo): List<Comment> {
        val id = articleInfo.id
        delay(ArticlesFakeData.getDelay(id))
        return ArticlesFakeData.getComments(id)
    }

    override suspend fun getCommentsUnstable(articleInfo: ArticleInfo): List<Comment> {
        if (Random.nextDouble() < 0.5) {
            throw NetworkException("Network error")
        }
        return getComments(articleInfo)
    }
}