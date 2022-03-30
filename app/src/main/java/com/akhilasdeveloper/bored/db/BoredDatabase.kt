package com.akhilasdeveloper.bored.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.akhilasdeveloper.bored.db.dao.BoredDao
import com.akhilasdeveloper.bored.db.table.BoredTable

@Database(
    entities = [BoredTable::class],
    version = 1
)
abstract class BoredDatabase : RoomDatabase() {
    abstract fun getBoredDao(): BoredDao
}