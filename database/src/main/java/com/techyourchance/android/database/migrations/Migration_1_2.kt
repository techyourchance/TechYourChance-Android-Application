package com.techyourchance.android.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration_1_2: Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `appMemoryInfo` ADD COLUMN `timestamp` INTEGER NOT NULL")
    }
}