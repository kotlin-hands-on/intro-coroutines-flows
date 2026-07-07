package org.example.articles

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.WorkshopServerConfig.PORT
import org.example.articles.network.BlogServiceBlocking
import org.example.articles.network.createBlogService
import org.example.articles.ui.ArticlesViewModel
import org.example.articles.ui.views.ArticlesView

private const val browserHost = "localhost"
private const val serverRootPath = "/"
private const val serverRootUrl = "http://$browserHost:$PORT$serverRootPath"

@Composable
@Preview
fun ArticlesApp(serverStatus: ServerStatus, onRetryServerStart: () -> Unit) {
    MaterialTheme {
        ProvideTextStyle(LocalTextStyle.current.copy(letterSpacing = 0.sp)) {
            when (serverStatus) {
                ServerStatus.Starting -> ServerStatusMessage("Starting server...")
                is ServerStatus.Failed -> ServerStatusMessage(
                    message = "Server failed to start: ${serverStatus.error}",
                    onRetry = onRetryServerStart,
                )
                ServerStatus.Running -> ArticlesContent()
            }
        }
    }
}

@Composable
private fun ArticlesContent() {
    val uriHandler = LocalUriHandler.current
    val blogService = remember { createBlogService() }
    val viewModel = viewModel {
        ArticlesViewModel(
            blockingService = BlogServiceBlocking(),
            service = blogService,
        )
    }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("Server running on ")
            Text(
                text = "$browserHost:$PORT$serverRootPath",
                color = MaterialTheme.colors.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { uriHandler.openUri(serverRootUrl) },
            )
        }
        ArticlesView(viewModel)
    }
}

@Composable
private fun ServerStatusMessage(message: String, onRetry: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message)
        if (onRetry != null) {
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}
