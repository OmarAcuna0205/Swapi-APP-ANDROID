package com.swapi.androidClassMp1.ids.location

import com.swapi.androidClassMp1.ids.location.models.LocationModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface LocationApiService {
    @GET("locations")
    suspend fun getLocations(): List<LocationModel>

    companion object {
        private const val BASE_URL = "https://eleventenbackend.onrender.com/api/"

        fun create(): LocationApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LocationApiService::class.java)
        }
    }
}