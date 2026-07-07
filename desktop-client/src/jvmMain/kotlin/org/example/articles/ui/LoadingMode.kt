package org.example.articles.ui

enum class LoadingMode {
    BLOCKING,
    SUSPENDING,
    CONCURRENT,
    NON_CANCELABLE,
    WITH_PROGRESS,
    CONCURRENT_WITH_PROGRESS,
    UNSTABLE_NETWORK;

    val title get(): String =
        name.replace('_', ' ')
}