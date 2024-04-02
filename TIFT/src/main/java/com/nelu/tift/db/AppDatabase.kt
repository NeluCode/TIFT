package com.nelu.tift.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nelu.tift.db.dao.DaoDownloads
import com.nelu.tift.data.model.local.ModelDownloads

@Database(entities = [ModelDownloads::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): DaoDownloads
}