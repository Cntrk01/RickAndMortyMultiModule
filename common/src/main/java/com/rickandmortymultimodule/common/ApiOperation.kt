package com.rickandmortymultimodule.common

sealed interface ApiOperation<T>{
    data class Success<T>(val data : T) : ApiOperation<T>
    data class Failure<T>(val exception: Exception) : ApiOperation<T>

    fun <R> mapSuccess(callback : (T) -> R) : ApiOperation<R> {
        return when(this){
            is Success -> Success(data = callback(data))
            is Failure -> Failure(exception = exception)
        }
    }

    suspend fun <R> operationFlatMap(callback: suspend (T) -> ApiOperation<R>): ApiOperation<R> {
        return when (this) {
            is Success -> callback(data)
            is Failure -> Failure(this.exception)
        }
    }

    fun onSuccess(callback : (T) -> Unit) : ApiOperation<T> {
        if (this is Success) callback(data)
        return this
    }

    fun onFailure(callback : (Exception) -> Unit) : ApiOperation<T> {
        if (this is Failure) callback(exception)
        return this
    }
}