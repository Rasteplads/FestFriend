package com.rasteplads.festfriend.model

sealed class Resource<T>(val data: T?, val message: String?, val errorResponse: String?) {
    class Success<T>(data: T) : Resource<T>(data, null, null)
    class Error<T>(message: String) : Resource<T>(null , message, null)
    class ErrorResponse<T>(errorResponse: String) : Resource<T>(null, null, errorResponse)
}