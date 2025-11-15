package com.example.myapp.data.remote

import java.io.IOException

/**
 * Custom exception for Retrofit API errors
 */
sealed class RetrofitException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    /**
     * HTTP error response (4xx, 5xx)
     */
    data class HttpError(
        val code: Int,
        val message: String?,
        val body: String?
    ) : RetrofitException(
        message = "HTTP $code: ${message ?: "Unknown error"}",
        cause = null
    )
    
    /**
     * Network error (no internet, timeout, etc.)
     */
    data class NetworkError(
        override val cause: Throwable
    ) : RetrofitException(
        message = "Network error: ${cause.message}",
        cause = cause
    )
    
    /**
     * Unexpected error
     */
    data class UnexpectedError(
        override val cause: Throwable
    ) : RetrofitException(
        message = "Unexpected error: ${cause.message}",
        cause = cause
    )
    
    companion object {
        /**
         * Converts a Throwable to a RetrofitException
         */
        fun fromThrowable(throwable: Throwable): RetrofitException {
            return when (throwable) {
                is IOException -> NetworkError(throwable)
                is retrofit2.HttpException -> {
                    val code = throwable.code()
                    val message = throwable.message()
                    val body = throwable.response()?.errorBody()?.string()
                    HttpError(code, message, body)
                }
                else -> UnexpectedError(throwable)
            }
        }
    }
}

