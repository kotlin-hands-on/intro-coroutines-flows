package org.example.articles.util

import org.example.articles.ui.LoadingMode
import java.util.prefs.Preferences

private fun prefNode(): Preferences = Preferences.userRoot().node("ContributorsUI")

fun loadStoredMode(): LoadingMode {
    return LoadingMode.valueOf(prefNode().get("loading_mode", LoadingMode.BLOCKING.name))
}

fun removeStoredMode() {
    prefNode().removeNode()
}

fun saveParams(loadingMode: LoadingMode) {
    val prefNode = prefNode()
    prefNode.put("loading_mode", loadingMode.name)
    prefNode.sync()
}