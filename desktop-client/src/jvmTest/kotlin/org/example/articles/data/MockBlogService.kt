package org.example.articles.data

import org.example.articles.network.BlogService
import org.example.articles.network.NetworkException
import org.example.blog.ArticleInfo
import org.example.data.ArticlesFakeData
import org.example.blog.Comment
import kotlinx.coroutines.delay

object MockBlogService: BlogService {
    override suspend fun getArticleInfoList(): List<ArticleInfo> {
        return ArticlesFakeData.getArticles()
    }

    override suspend fun getComments(articleInfo: ArticleInfo): List<Comment> {
        val id = articleInfo.id
        delay(ArticlesFakeData.getDelay(id))
        return ArticlesFakeData.getComments(id)
    }

    override suspend fun getArticleInfoListUnstable(): List<ArticleInfo> = getArticleInfoList()

    override suspend fun getCommentsUnstable(articleInfo: ArticleInfo): List<Comment> {
        return getComments(articleInfo)
    }
}

class UnstableMockBlogService(
    private val failingArticleIds: Set<Int> = emptySet(),
    private val failArticleList: Boolean = false,
) : BlogService by MockBlogService {
    override suspend fun getArticleInfoListUnstable(): List<ArticleInfo> {
        if (failArticleList) throw NetworkException("Network error")
        return MockBlogService.getArticleInfoListUnstable()
    }

    override suspend fun getCommentsUnstable(articleInfo: ArticleInfo): List<Comment> {
        if (articleInfo.id in failingArticleIds) throw NetworkException("Network error")
        return MockBlogService.getCommentsUnstable(articleInfo)
    }
}