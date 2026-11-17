package com.example.proj_soundvault.data.remote

import com.example.proj_soundvault.data.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTHENTICATION (SRS 3.2) ---
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<UserDto>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginData>>

    @POST("auth/refresh")
    fun refreshToken(@Body request: RefreshRequest): Call<ApiResponse<RefreshData>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Any?>>

    // --- AUDIO BRANDS (SRS 3.3) ---
    @GET("brands")
    suspend fun getBrands(@Query("search") search: String? = null): Response<ApiResponse<List<AudioBrand>>>

    @POST("brands")
    suspend fun createBrand(@Body request: Map<String, String>): Response<ApiResponse<AudioBrand>>

    @PUT("brands/{id}")
    suspend fun updateBrand(
        @Path("id") id: Int,
        @Body request: Map<String, String>
    ): Response<ApiResponse<AudioBrand>>

    @DELETE("brands/{id}")
    suspend fun deleteBrand(@Path("id") id: Int): Response<ApiResponse<Any?>>
}