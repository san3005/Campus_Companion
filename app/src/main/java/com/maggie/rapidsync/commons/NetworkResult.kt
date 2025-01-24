package com.maggie.rapidsync.commons

sealed class NetworkResult<out T> {
    object Loading : NetworkResult<Nothing>()
    class Success<out T>(val body: T?) : NetworkResult<T>()
    class Error<out T>(val errorMessage: String) : NetworkResult<T>()
    object Initial : NetworkResult<Nothing>()
}