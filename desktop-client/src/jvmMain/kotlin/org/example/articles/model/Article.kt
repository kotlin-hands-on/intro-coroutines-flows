package org.example.articles.model

import org.example.blog.ArticleInfo
import org.example.blog.Comment
import org.example.blog.User

data class Article(
    val info: ArticleInfo,
    val comments: List<Comment>
) {
    val author: User
        get() = info.author
    val commenters: Set<User>
        get() = comments.map { it.author }.toSet()

    val activeUsers: Set<User>
        get() = commenters + author
}

fun List<Article>.findActiveUsers(): Set<User> =
    flatMap { it.activeUsers }.toSet()

