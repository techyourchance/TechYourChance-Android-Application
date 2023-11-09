package com.techyourchance.android.database

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.techyourchance.android.database.migrations.Migration_1_2

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
            addMigrations(Migration_1_2())
//            addTypeConverter(AppMemoryInfoConverter(gson))
        }.build()
    }

    val transactionsController get() = TransactionsController(myRoomDatabase)

    val backgroundTasksMemoryDao get() = myRoomDatabase.backgroundTasksMemoryDao

}