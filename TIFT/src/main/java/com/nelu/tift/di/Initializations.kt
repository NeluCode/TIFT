package com.nelu.tift.di

import androidx.room.Room
import com.nelu.tift.config.Constant.BASE_URL
import com.nelu.tift.config.Constant.DATABASE_NAME
import com.nelu.tift.data.apis.ApiService
import com.nelu.tift.db.AppDatabase
import com.nelu.tift.db.dao.DaoDownloads
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Initializations {

    /** Remote */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** Remote API's */
    val apiService: ApiService get() = retrofit.create(ApiService::class.java)


    /** Local */
    private val appDatabase = Room.databaseBuilder(
        KitTIFT.application,
        AppDatabase::class.java,
        DATABASE_NAME
    ).allowMainThreadQueries().build()

    /** Local Data Access Object's */
    val daoDownloads: DaoDownloads get() = appDatabase.userDao()
}