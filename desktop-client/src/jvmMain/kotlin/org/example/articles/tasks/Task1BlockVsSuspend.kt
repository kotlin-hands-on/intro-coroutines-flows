package org.example.articles.tasks

import org.example.articles.model.Article
import org.example.articles.network.BlogService
import org.example.articles.network.BlogServiceBlocking
import org.example.articles.network.createBlogService
import org.example.blog.ArticleInfo

// Task: Implement blocking and non-blocking versions for articles loading

// This function is invoked when you select the "BLOCKING" option in the UI.
fun loadArticles(serviceBlocking: BlogServiceBlocking): List<Article> {
    TODO()
}

// This function is invoked when you select the "SUSPENDING" option in the UI.
suspend fun loadArticles(service: BlogService): List<Article> {
    TODO()
}

fun main0() {
    val serviceBlocking = BlogServiceBlocking()
    val articles = loadArticles(serviceBlocking)
    println(articles)
}

suspend fun main() {
    val service = createBlogService()
    println(loadArticles(service))
}