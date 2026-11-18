package org.example.plugins

import org.example.blog.Comment
import org.example.data.ArticlesFakeData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import kotlin.random.Random

fun Application.configureArticlesRouting() {
    routing {
        get("/articles") {
            if (call.respondWithRandomFailure()) return@get
            call.respond(ArticlesFakeData.getArticles())
        }
        get("/articles/{id}/comments") {
            call.parameters["id"]?.toIntOrNull()?.let { id ->
                if (call.respondWithRandomFailure()) return@get
                delay(ArticlesFakeData.getDelay(id))
                call.respond(ArticlesFakeData.getComments(id))
            } ?: run {
                call.respond(listOf<Comment>())
            }
        }
    }
}

private suspend fun ApplicationCall.respondWithRandomFailure(): Boolean {
    val failureProbability = request.queryParameters["failure"]?.toDoubleOrNull() ?: 0.0
    if (Random.nextDouble() >= failureProbability) return false
    respond(HttpStatusCode.InternalServerError)
    return true
}
