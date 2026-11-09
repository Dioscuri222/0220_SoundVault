package com.example.proj_soundvault.data.remote

import com.example.proj_soundvault.data.model.AudioBrand
import retrofit2.http.*

interface ApiService {
    @GET("brands")
    suspend fun getBrands(): List<AudioBrand>

    @POST("brands")
    suspend fun addBrand(@Body brand: AudioBrand): AudioBrand

    @DELETE("brands/{id}")
    suspend fun deleteBrand(@Path("id") id: Int)
}