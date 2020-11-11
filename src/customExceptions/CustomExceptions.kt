package com.example.customExceptions

class NotLoggedInExceptionException(message: String) : Exception(message)

class InsufficientPermissionsException(message: String) : Exception(message)

class IllegalArgumentException(message: String) : Exception(message)

class FailedToInteractWithResourceException(message: String) : Exception(message)

data class ServerError(val errorType: String, val errorMessage: String)

fun <T> catchExceptions(body: () -> T): Pair<T?, ServerError?> {
    return try {
        Pair(body(), null)
    } catch (e: NotLoggedInExceptionException) {
        Pair(null, ServerError(errorType = "NotLoggedInExceptionException", errorMessage = e.message ?: ""))
    } catch (e: InsufficientPermissionsException) {
        Pair(null, ServerError(errorType = "InsufficientPermissionsException", errorMessage = e.message ?: ""))
    } catch (e: IllegalArgumentException) {
        Pair(null, ServerError(errorType = "IllegalArgumentException", errorMessage = e.message ?: ""))
    } catch (e: FailedToInteractWithResourceException) {
        Pair(null, ServerError(errorType = "FailedToInteractWithResourceException", errorMessage = e.message ?: ""))
    } catch (e: Throwable) {
        Pair(null, ServerError(errorType = "InternalServerError", errorMessage = e.message ?: ""))
    }
}