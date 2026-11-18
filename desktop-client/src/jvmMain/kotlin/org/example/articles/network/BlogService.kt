package org.example.articles.network

import org.example.WorkshopServerConfig
import org.example.blog.ArticleInfo
import org.example.blog.Comment
import org.example.network.WorkshopKtorService
import org.example.util.log
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

private const val FAILURE_PROBABILITY = 0.3

interface BlogService {
    suspend fun getArticleInfoList(): List<ArticleInfo>

    suspend fun getArticleInfoListUnstable(): List<ArticleInfo>

    suspend fun getComments(articleInfo: ArticleInfo): List<Comment>

    suspend fun getCommentsUnstable(articleInfo: ArticleInfo): List<Comment>
}

fun createBlogService(): BlogService = BlogServiceImpl()

private class BlogServiceImpl: BlogService, WorkshopKtorService() {
    val articlesEndpoint = "${WorkshopServerConfig.WORKSHOP_SERVER_URL}/articles"
    val articlesUnstableEndpoint = "$articlesEndpoint?failure=$FAILURE_PROBABILITY"
    fun commentsEndpoint(id: Int) = "${WorkshopServerConfig.WORKSHOP_SERVER_URL}/articles/$id/comments"
    fun commentsUnstableEndpoint(id: Int) = commentsEndpoint(id) + "?failure=$FAILURE_PROBABILITY"

    override suspend fun getArticleInfoList(): List<ArticleInfo> {
        log("Started loading articles")
        return client.get(articlesEndpoint)
            .body<List<ArticleInfo>>()
            .also { log("Loaded articles") }
    }

    override suspend fun getArticleInfoListUnstable(): List<ArticleInfo> {
        log("Started loading articles")
        val response = client.get(articlesUnstableEndpoint)
        response.throwIfUnsuccessful("Loaded articles failure: ${response.status}")
        return response.body<List<ArticleInfo>>()
            .also { log("Loaded articles unstable") }
    }

    override suspend fun getComments(articleInfo: ArticleInfo): List<Comment> {
        log("Started loading comments for article ${articleInfo.title}")
        return client.get(commentsEndpoint(articleInfo.id))
            .body<List<Comment>>()
            .also {
                log("Loaded comments for article ${articleInfo.title}")
            }
    }

    override suspend fun getCommentsUnstable(articleInfo: ArticleInfo): List<Comment> {
        log("Started loading comments for article ${articleInfo.title}")
        val response = client.get(commentsUnstableEndpoint(articleInfo.id))
        response.throwIfUnsuccessful("Loaded comments failure for article ${articleInfo.title}: ${response.status}")
        return response.body<List<Comment>>()
            .also {
                log("Loaded comments unstable for article ${articleInfo.title}")
            }
    }
}

private fun HttpResponse.throwIfUnsuccessful(failureLog: String) {
    if (!status.isSuccess()) {
        log(failureLog)
        throw NetworkException("HTTP ${status.value}")
    }
}

class NetworkException(message: String) : Exception(message)