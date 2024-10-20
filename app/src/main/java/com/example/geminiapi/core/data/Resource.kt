package com.example.geminiapi.core.data

sealed class Resource<out T> {
    data object Loading: Resource<Nothing>()
    data class Success<out T>(val data: T, val isFirstResponse: Boolean = false) : Resource<T>()
    data class Error<out T>(val error: Throwable, val data: T? = null) : Resource<T>()
    data object Empty : Resource<Nothing>() // Using an object for Empty state
}

