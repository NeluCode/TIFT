package com.nelu.tift.di

import com.nelu.tift.data.apis.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Initializations {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://us-central1-snaptik-pro.cloudfunctions.net/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService get() = retrofit.create(ApiService::class.java)
}