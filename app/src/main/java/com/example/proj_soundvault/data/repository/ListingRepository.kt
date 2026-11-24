package com.example.proj_soundvault.data.repository

import com.example.proj_soundvault.data.model.ListingFilter
import com.example.proj_soundvault.data.model.ListingItem
import com.example.proj_soundvault.data.model.ListingRequest
import com.example.proj_soundvault.data.remote.ApiService
import org.json.JSONObject

class ListingRepository(private val apiService: ApiService) {

    suspend fun getListings(filter: ListingFilter = ListingFilter()): Result<List<ListingItem>> {
        return try {
            val response = apiService.getListings(
                search = filter.search.trim().takeIf { it.isNotEmpty() },
                category = filter.category?.takeIf { it.isNotBlank() },
                brandId = filter.brandId,
                driverType = filter.driverType?.takeIf { it.isNotBlank() },
                connectorType = filter.connectorType?.takeIf { it.isNotBlank() },
                soundSignature = filter.soundSignature?.takeIf { it.isNotBlank() },
                itemCondition = filter.itemCondition?.takeIf { it.isNotBlank() },
                status = filter.status?.takeIf { it.isNotBlank() }
            )
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string(), "Gagal memuat katalog listing")))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Koneksi gagal: ${e.localizedMessage ?: "Periksa jaringan Anda"}"))
        }
    }

    suspend fun getListingById(id: Int): Result<ListingItem> {
        return try {
            val response = apiService.getListingById(id)
            val item = response.body()?.data
            if (response.isSuccessful && response.body()?.success == true && item != null) {
                Result.success(item)
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string(), "Detail produk tidak ditemukan")))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Koneksi gagal: ${e.localizedMessage ?: "Periksa jaringan Anda"}"))
        }
    }

    suspend fun createListing(request: ListingRequest): Result<ListingItem> {
        return try {
            val response = apiService.createListing(request)
            val created = response.body()?.data
            if (response.isSuccessful && response.body()?.success == true && created != null) {
                Result.success(created)
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string(), "Gagal memasang listing baru")))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Koneksi gagal: ${e.localizedMessage ?: "Periksa jaringan Anda"}"))
        }
    }

    suspend fun updateListing(id: Int, request: ListingRequest): Result<ListingItem> {
        return try {
            val response = apiService.updateListing(id, request)
            val updated = response.body()?.data
            if (response.isSuccessful && response.body()?.success == true && updated != null) {
                Result.success(updated)
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string(), "Gagal memperbarui data listing")))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Koneksi gagal: ${e.localizedMessage ?: "Periksa jaringan Anda"}"))
        }
    }

    suspend fun deleteListing(id: Int): Result<String> {
        return try {
            val response = apiService.deleteListing(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "Listing berhasil dihapus")
            } else {
                Result.failure(Exception(parseErrorMessage(response.errorBody()?.string(), "Gagal menghapus listing")))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Koneksi gagal: ${e.localizedMessage ?: "Periksa jaringan Anda"}"))
        }
    }

    private fun parseErrorMessage(errorBody: String?, fallback: String): String {
        if (errorBody.isNullOrBlank()) return fallback
        return try {
            JSONObject(errorBody).optString("message").takeIf { it.isNotBlank() } ?: fallback
        } catch (_: Exception) {
            fallback
        }
    }
}