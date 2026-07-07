package org.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureStatusRouting() {
    routing {
        get("/") {
            call.respondText(
                """
                The server is running. Here's its capabilities:
                - GET /articles
                - GET /articles/{id}/comments
                - GET /articles/{id}/comments?failure=0.3
                """.trimIndent()
            )
        }
    }
}
