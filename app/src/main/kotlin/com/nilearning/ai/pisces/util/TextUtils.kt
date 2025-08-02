package com.nilearning.ai.pisces.util

fun <T> MutableList<T>.removeLastCompat(): T? {
    return if (isNotEmpty()) {
        removeAt(size - 1)
    } else {
        null
    }
}