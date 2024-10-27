package com.mshell.geminiapi.core.data

sealed class Resource<out T> {
    data object Loading: Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error<out T>(val error: Throwable) : Resource<T>()
    data object Empty : Resource<Nothing>() // Using an object for Empty state
}

