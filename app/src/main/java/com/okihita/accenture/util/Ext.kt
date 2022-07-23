package com.okihita.accenture.util

import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toUserFriendlyErrorMessage(): String {

    return when (this) {
        is HttpException -> {
            when (this.code()) {
                403 -> "API rate limit reached."
                else -> "Error loading data. HTTP error code ${code()}"
            }
        }

        // If the internet is off so the Socket Layer couldn't resolve hostname into IP address
        is UnknownHostException -> {
            "Cannot resolve internet address. Check if your internet connections are turned on."
        }

        // If the internet was on, and hostname was resolved, but currently connection is cut
        is SocketTimeoutException -> {
            "The server doesn't reply. Check again later."
        }

        else -> {
            "Unknown error. Try again later. Error type: ${this.javaClass.simpleName}"
        }
    }
}