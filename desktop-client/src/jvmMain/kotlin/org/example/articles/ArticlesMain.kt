package org.example.articles

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

fun main() = application {
    val appState = remember { ArticlesAppState(ServerController()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(appState) {
        appState.startServer()
    }
    DisposableEffect(appState) {
        onDispose {
            appState.shutdown()
        }
    }

    Window(
        onCloseRequest = {
            appState.shutdown()
            exitApplication()
        },
        title = "Articles",
        state = rememberWindowState(width = 760.dp, height = 760.dp),
    ) {
        ArticlesApp(
            serverStatus = appState.serverStatus,
            onRetryServerStart = { scope.launch { appState.startServer() } },
        )
    }
}

sealed interface ServerStatus {
    data object Starting : ServerStatus
    data object Running : ServerStatus
    data class Failed(val error: String) : ServerStatus
}

private class ArticlesAppState(
    private val serverController: ServerController,
) {
    var serverStatus by mutableStateOf<ServerStatus>(ServerStatus.Starting)
        private set

    suspend fun startServer() {
        serverStatus = ServerStatus.Starting
        serverStatus = try {
            serverController.start()
            ServerStatus.Running
        } catch (exception: CancellationException) {
            throw exception
        } catch (throwable: Throwable) {
            ServerStatus.Failed(throwable.message ?: throwable::class.simpleName.orEmpty())
        }
    }

    fun shutdown() {
        serverController.stop()
    }
}
