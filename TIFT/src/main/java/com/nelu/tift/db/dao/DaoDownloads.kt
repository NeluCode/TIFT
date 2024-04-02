package com.nelu.tift.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nelu.tift.data.model.local.ModelDownloads

@Dao
interface DaoDownloads {

    @Query("SELECT * FROM ModelDownloads")
    fun getAllDownloads(): List<ModelDownloads>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownloads(data: ModelDownloads)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownloads(data: List<ModelDownloads>)
}