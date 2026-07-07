package org.example.articles.tasks

import org.example.articles.model.Article
import org.example.articles.network.BlogService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

// Task: Implement concurrent loading of comments using flows
fun observeArticlesConcurrently(service: BlogService): Flow<Article> = channelFlow {
    val list = service.getArticleInfoList()
    for (articleInfo in list) {
        launch {
            send(Article(articleInfo, service.getComments(articleInfo)))
        }
    }
}
