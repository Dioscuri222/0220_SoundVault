package com.example.proj_soundvault.data.repository

import com.example.proj_soundvault.data.local.SessionManager
import com.example.proj_soundvault.data.model.*
import com.example.proj_soundvault.data.remote.ApiService
import com.google.gson.Gson
import retrofit2.Response

class AuthRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    private val gson = Gson()

    suspend fun login(email: String, password: String): Result<UserDto> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            val body = response.body()
            if (response.isSuccessful && body != null && body.success && body.data != null) {
                sessionManager.saveSession(
                    accessToken = body.data.accessToken,
                    refreshToken = body.data.refreshToken,
                    user = body.data.user
                )
                Result.success(body.data.user)
            } else {
                Result.failure(Exception(extractErrorMessage(response, "Login gagal")))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server: ${e.localizedMessage}"))
        }
    }

    suspend fun register(
        namaLengkap: String,
        email: String,
        password: String,
        noTelepon: String
    ): Result<String> {
        return try {
            val response = apiService.register(
                RegisterRequest(namaLengkap, email, password, noTelepon)
            )
            val body = response.body()
            if (response.isSuccessful && body != null && body.success) {
                Result.success(body.message)
            } else {
                Result.failure(Exception(extractErrorMessage(response, "Registrasi gagal")))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server: ${e.localizedMessage}"))
        }
    }

    suspend fun logout() {
        try {
            apiService.logout()
        } catch (_: Exception) {
            // Tetap hapus sesi lokal meskipun server tidak terjangkau
        } finally {
            sessionManager.clearSession()
        }
    }

    private fun extractErrorMessage(response: Response<*>, fallback: String): String {
        return try {
            val errorJson = response.errorBody()?.string()
            val parsed = gson.fromJson(errorJson, ApiResponse::class.java)
            parsed?.message ?: fallback
        } catch (_: Exception) {
            fallback
        }
    }
}