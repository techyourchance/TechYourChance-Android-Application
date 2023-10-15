package com.techyourchance.android.common.usecases

/**
 * Standard representation of flow execution's result. This class covers the "simple" cases. Flows
 * that need more involved return values can implement their own custom result types.
 */
sealed class UseCaseResult<T> {
    data class Success<T>(val data: T): UseCaseResult<T>()
    data class Failure<T>(val errorCode: Int, val errorMessage: String): UseCaseResult<T>() {
        constructor(otherFailure: Failure<out Any>): this(otherFailure.errorCode, otherFailure.errorMessage)
    }
}
