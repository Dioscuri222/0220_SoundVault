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



    // ==================== LISTING ITEMS (SRS 3.4) ====================
    @GET("listings")
    suspend fun getListings(
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("brand_id") brandId: Int? = null,
        @Query("driver_type") driverType: String? = null,
        @Query("connector_type") connectorType: String? = null,
        @Query("sound_signature") soundSignature: String? = null,
        @Query("item_condition") itemCondition: String? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<ListingItem>>>

    @GET("listings/{id}")
    suspend fun getListingById(
        @Path("id") id: Int
    ): Response<ApiResponse<ListingItem>>

    @POST("listings")
    suspend fun createListing(
        @Body request: ListingRequest
    ): Response<ApiResponse<ListingItem>>

    @PUT("listings/{id}")
    suspend fun updateListing(
        @Path("id") id: Int,
        @Body request: ListingRequest
    ): Response<ApiResponse<ListingItem>>

    @DELETE("listings/{id}")
    suspend fun deleteListing(
        @Path("id") id: Int
    ): Response<ApiResponse<Any?>>
}