package org.example

import org.example.WorkshopServerConfig.HOST
import org.example.WorkshopServerConfig.PORT
import org.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    createWorkshopServer().start(wait = true)
}

fun createWorkshopServer(): WorkshopServer = WorkshopServer()

class WorkshopServer {
    private val server = embeddedServer(
        Netty,
        port = PORT,
        host = HOST,
        module = Application::module,
    )

    fun start(wait: Boolean) {
        server.start(wait = wait)
    }

    fun stop() {
        server.stop(gracePeriodMillis = 1_000, timeoutMillis = 5_000)
    }
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()

    configureStatusRouting()
    configureArticlesRouting()
}
