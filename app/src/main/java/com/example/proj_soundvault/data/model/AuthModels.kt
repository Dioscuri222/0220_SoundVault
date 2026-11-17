package com.example.proj_soundvault.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    @SerializedName("nama_lengkap") val namaLengkap: String,
    val email: String,
    val password: String,
    @SerializedName("no_telepon") val noTelepon: String
)

data class RefreshRequest(
    @SerializedName("refresh_token") val refreshToken: String
)

data class UserDto(
    @SerializedName("id_user") val idUser: Int,
    @SerializedName("nama_lengkap") val namaLengkap: String,
    val email: String,
    @SerializedName("no_telepon") val noTelepon: String? = null,
    val role: String // "ADMIN" atau "USER"
)

data class LoginData(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    val user: UserDto
)

data class RefreshData(
    @SerializedName("access_token") val accessToken: String
)