package org.example.articles

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CancellationException

fun main() = application {
    val launcherState = remember { ArticlesLauncherState(ServerController()) }

    LaunchedEffect(launcherState) {
        launcherState.startServer()
    }
    DisposableEffect(launcherState) {
        onDispose {
            launcherState.shutdown()
        }
    }

    Window(
        onCloseRequest = {
            launcherState.shutdown()
            exitApplication()
        },
        title = "Coroutines Workshop: Articles",
        state = rememberWindowState(
            position = WindowPosition(Alignment.Center),
            width = 480.dp,
            height = 180.dp,
        ),
    ) {
        ArticlesLauncherApp(
            serverStatus = launcherState.serverStatus,
            onOpenArticles = launcherState::openArticlesWindow,
        )
    }

    if (launcherState.isArticlesWindowOpen) {
        ArticlesWindow(
            onCloseRequest = launcherState::closeArticlesWindow,
            verifyServerOnLaunch = false,
        )
    }
}

private class ArticlesLauncherState(
    private val serverController: ServerController,
) {
    var serverStatus by mutableStateOf<ServerStatus>(ServerStatus.Starting)
        private set

    var isArticlesWindowOpen by mutableStateOf(false)
        private set

    suspend fun startServer() {
        serverStatus = ServerStatus.Starting
        serverStatus = try {
            serverController.start()
            ServerStatus.Running(startedAtMillis = System.currentTimeMillis())
        } catch (exception: CancellationException) {
            throw exception
        } catch (throwable: Throwable) {
            ServerStatus.Failed(throwable.message ?: throwable::class.simpleName.orEmpty())
        }
    }

    fun shutdown() {
        serverController.stop()
    }

    fun openArticlesWindow() {
        if (serverStatus.canLaunchClients) {
            isArticlesWindowOpen = true
        }
    }

    fun closeArticlesWindow() {
        isArticlesWindowOpen = false
    }
}
