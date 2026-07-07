package org.example.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

open class WorkshopKtorService(configureWebsockets: Boolean = false) {
    protected val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        if (configureWebsockets) {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
    }
}
