package com.example.geminiapi.core.data

sealed class Resource<T>(val data: T? = null, val message: String? = null, val error: Throwable? = null) {
    class Loading<T> :Resource<T>()
    class Success<T>(data: T?) : Resource<T>(data)
}
