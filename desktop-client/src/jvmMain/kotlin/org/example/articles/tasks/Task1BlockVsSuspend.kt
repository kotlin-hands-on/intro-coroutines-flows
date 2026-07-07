package org.example.articles.tasks

import org.example.articles.model.Article
import org.example.articles.network.BlogService
import org.example.articles.network.BlogServiceBlocking
import org.example.articles.network.createBlogService
import org.example.blog.ArticleInfo

// Task: Implement blocking and non-blocking versions for articles loading

// This function is invoked when you select the "BLOCKING" option in the UI.
fun loadArticles(serviceBlocking: BlogServiceBlocking): List<Article> {
    val articleInfoList = serviceBlocking.getArticleInfoList()
    return articleInfoList.map { articleInfo: ArticleInfo -> Article(articleInfo, serviceBlocking.getComments(articleInfo)) }
}

// This function is invoked when you select the "SUSPENDING" option in the UI.
suspend fun loadArticles(service: BlogService): List<Article> {
    val articleInfoList = service.getArticleInfoList()
    return articleInfoList.map { articleInfo: ArticleInfo -> Article(articleInfo, service.getComments(articleInfo)) }
}

fun main1() {
    val serviceBlocking = BlogServiceBlocking()
    val articles = loadArticles(serviceBlocking)
    println(articles)
}

suspend fun main() {
    val service = createBlogService()
    println(loadArticles(service))
}