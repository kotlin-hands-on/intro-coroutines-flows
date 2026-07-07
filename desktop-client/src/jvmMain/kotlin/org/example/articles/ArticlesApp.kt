package org.example.articles

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.articles.network.BlogServiceBlocking
import org.example.articles.network.createBlogService
import org.example.articles.ui.ArticlesViewModel
import org.example.articles.ui.views.ArticlesView
import org.example.network.WorkshopKtorService

@Composable
@Preview
fun ArticlesApp(viewModel: ArticlesViewModel) {
    MaterialTheme {
        ProvideTextStyle(LocalTextStyle.current.copy(letterSpacing = 0.sp)) {
            ArticlesView(viewModel)
        }
    }

}

@Composable
fun ArticlesWindow(
    onCloseRequest: () -> Unit,
    verifyServerOnLaunch: Boolean,
) {
    val blogService = remember { createBlogService() }
    if (verifyServerOnLaunch) {
        LaunchedEffect(blogService) {
            (blogService as WorkshopKtorService).ensureServerIsRunning()
        }
    }
    Window(
        onCloseRequest = onCloseRequest,
        title = "Articles",
        state = rememberWindowState(width = 760.dp, height = 760.dp),
    ) {
        val viewModel = viewModel {
            ArticlesViewModel(
                blockingService = BlogServiceBlocking(),
                service = blogService,
            )
        }
        ArticlesApp(viewModel)
    }
}
