package com.techyourchance.android.database

import androidx.annotation.WorkerThread
import androidx.room.RoomDatabase
import java.util.concurrent.Callable

class TransactionsController(private val roomDatabase: RoomDatabase) {
    /**
     * Executes the specified Runnable in a database transaction.
     * The transaction will be marked as successful unless an exception is thrown in the Runnable.
     * If the exception is thrown in the Runnable, it'll be propagated to the caller.
     */
    @WorkerThread
    fun runInTransaction(runnable: Runnable) {
        roomDatabase.runInTransaction(runnable)
    }

    /**
     * Executes the specified Callable in a database transaction.
     * The transaction will be marked as successful unless an exception is thrown in the Callable.
     * @return The value returned from the Callable.
     */
    @WorkerThread
    fun <V> runInTransaction(callable: Callable<V>): V {
        return roomDatabase.runInTransaction(callable)
    }

}