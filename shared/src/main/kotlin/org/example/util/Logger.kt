package org.example.util

fun log(message: Any?) {
    println("[CLIENT] ${currentTime()} [${Thread.currentThread().name}] $message")
}