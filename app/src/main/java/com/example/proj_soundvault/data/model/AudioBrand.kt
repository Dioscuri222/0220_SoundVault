package com.example.proj_soundvault.data.model

import com.google.gson.annotations.SerializedName

data class AudioBrand(
    @SerializedName("id_brand") val idBrand: Int = 0,
    @SerializedName("brand_name") val brandName: String = "",
    @SerializedName("country_origin") val countryOrigin: String = "",
    val description: String? = null,
    @SerializedName("created_by") val createdBy: Int? = null,
    @SerializedName("admin_name") val adminName: String? = null
)