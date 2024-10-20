package com.example.geminiapi.core.data

sealed class Resource<T>(val data: T? = null, val error: Throwable? = null) {
    class Loading<T> :Resource<T>()
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(error: Throwable? = null, data: T? = null) : Resource<T>(data, error)
    class Empty<T> : Resource<T>()
}
