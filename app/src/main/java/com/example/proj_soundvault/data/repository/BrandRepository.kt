package com.example.proj_soundvault.data.repository

import com.example.proj_soundvault.data.model.ApiResponse
import com.example.proj_soundvault.data.model.AudioBrand
import com.example.proj_soundvault.data.remote.ApiService
import com.google.gson.Gson
import retrofit2.Response

class BrandRepository(private val apiService: ApiService) {
    private val gson = Gson()

    suspend fun getBrands(search: String? = null): Result<List<AudioBrand>> {
        return try {
            val query = search?.trim()?.takeIf { it.isNotEmpty() }
            val response = apiService.getBrands(query)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(extractErrorMessage(response, "Gagal memuat daftar merek")))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Koneksi gagal: ${e.localizedMessage}"))
        }
    }

    suspend fun createBrand(
        brandName: String,
        countryOrigin: String,
        description: String
    ): Result<AudioBrand> {
        return try {
            val payload = mapOf(
                "brand_name" to brandName.trim(),
                "country_origin" to countryOrigin.trim(),
                "description" to description.trim()
            )
            val response = apiService.createBrand(payload)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(extractErrorMessage(response, "Gagal menambahkan merek")))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Koneksi gagal: ${e.localizedMessage}"))
        }
    }

    suspend fun updateBrand(
        id: Int,
        brandName: String,
        countryOrigin: String,
        description: String
    ): Result<AudioBrand> {
        return try {
            val payload = mapOf(
                "brand_name" to brandName.trim(),
                "country_origin" to countryOrigin.trim(),
                "description" to description.trim()
            )
            val response = apiService.updateBrand(id, payload)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(extractErrorMessage(response, "Gagal memperbarui merek")))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Koneksi gagal: ${e.localizedMessage}"))
        }
    }

    suspend fun deleteBrand(id: Int): Result<String> {
        return try {
            val response = apiService.deleteBrand(id)
            val body = response.body()
            if (response.isSuccessful && body != null && body.success) {
                Result.success(body.message)
            } else {
                Result.failure(Exception(extractErrorMessage(response, "Gagal menghapus merek")))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Koneksi gagal: ${e.localizedMessage}"))
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