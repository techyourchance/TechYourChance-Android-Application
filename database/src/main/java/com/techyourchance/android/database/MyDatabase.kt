package com.techyourchance.android.database

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.techyourchance.android.database.converters.AppMemoryInfoConverter
import com.techyourchance.android.database.converters.BackgroundTasksMemoryConverter

/**
 * The function of this wrapper class is to encapsulate [MyRoomDatabase] dependency, such
 * that the clients won't need to "know" about it.
 * This class must be a global object (i.e. one instance per application)
 */
class MyDatabase(context: Context, gson: Gson) {
    private val myRoomDatabase: MyRoomDatabase

    init {
        myRoomDatabase = Room.databaseBuilder(
            context,
            MyRoomDatabase::class.java,
            DatabaseConstants.DATABASE_NAME
        ).apply {
//            addTypeConverter(AppMemoryInfoConverter(gson))
//            addTypeConverter(BackgroundTasksMemoryConverter(gson))
        }.build()
    }

    val transactionsController get() = TransactionsController(myRoomDatabase)

    val backgroundTasksMemoryDao get() = myRoomDatabase.backgroundTasksMemoryDao

}