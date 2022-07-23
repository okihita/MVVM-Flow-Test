package com.okihita.accenture.util

sealed class ResultException {
    class NoMoreResultException(message: String = "") : Exception(message)
    class EmptyResultException(message: String = "") : Exception(message)
}
