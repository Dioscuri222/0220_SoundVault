package com.example.proj_soundvault.data.model

import com.google.gson.annotations.SerializedName
import java.text.NumberFormat
import java.util.Locale

data class ListingItem(
    @SerializedName("id_item")
    val idItem: Int,

    @SerializedName("seller_id")
    val sellerId: Int,

    @SerializedName("brand_id")
    val brandId: Int,

    @SerializedName("brand_name")
    val brandName: String? = null,

    @SerializedName("seller_name")
    val sellerName: String? = null,

    @SerializedName("seller_phone")
    val sellerPhone: String? = null,

    @SerializedName("item_name")
    val itemName: String,

    @SerializedName("category")
    val category: String, // "IEM", "DAC_AMP", "CABLE", "ACCESSORY"

    @SerializedName("driver_type")
    val driverType: String,

    @SerializedName("connector_type")
    val connectorType: String,

    @SerializedName("sound_signature")
    val soundSignature: String,

    @SerializedName("item_condition")
    val itemCondition: String, // "NEW", "USED"

    @SerializedName("price")
    val price: String, // PostgreSQL DECIMAL dikembalikan sebagai String ("210000.00")

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("status")
    val status: String = "AVAILABLE", // "AVAILABLE", "RESERVED", "SOLD"

    @SerializedName("created_at")
    val createdAt: String? = null
) {
    fun priceAsDouble(): Double = price.toDoubleOrNull() ?: 0.0

    fun formattedPrice(): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID")).apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }
        return "Rp ${formatter.format(priceAsDouble().toLong())}"
    }

    fun formattedCondition(): String = when (itemCondition.uppercase()) {
        "NEW" -> "Baru (NEW)"
        "USED" -> "Bekas (USED)"
        else -> itemCondition
    }

    fun formattedCategory(): String = when (category.uppercase()) {
        "IEM" -> "IEM"
        "DAC_AMP" -> "DAC / Amp"
        "CABLE" -> "Kabel Audio"
        "ACCESSORY" -> "Aksesori"
        else -> category
    }
}

data class ListingRequest(
    @SerializedName("brand_id")
    val brandId: Int,

    @SerializedName("item_name")
    val itemName: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("driver_type")
    val driverType: String,

    @SerializedName("connector_type")
    val connectorType: String,

    @SerializedName("sound_signature")
    val soundSignature: String,

    @SerializedName("item_condition")
    val itemCondition: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("description")
    val description: String? = null
)

data class ListingFilter(
    val search: String = "",
    val category: String? = null,
    val brandId: Int? = null,
    val driverType: String? = null,
    val connectorType: String? = null,
    val soundSignature: String? = null,
    val itemCondition: String? = null,
    val status: String? = null
) {
    fun activeFilterCount(): Int {
        var count = 0
        if (!category.isNullOrBlank()) count++
        if (brandId != null) count++
        if (!driverType.isNullOrBlank()) count++
        if (!connectorType.isNullOrBlank()) count++
        if (!soundSignature.isNullOrBlank()) count++
        if (!itemCondition.isNullOrBlank()) count++
        if (!status.isNullOrBlank()) count++
        return count
    }

    fun hasActiveFilters(): Boolean = activeFilterCount() > 0
}

object ListingConstants {
    val CATEGORIES = listOf("IEM", "DAC_AMP", "CABLE", "ACCESSORY")
    val CONDITIONS = listOf("NEW", "USED")
    val DRIVER_SUGGESTIONS = listOf("1DD", "Multi-DD", "BA", "Hybrid", "Planar", "Tribrid")
    val CONNECTOR_SUGGESTIONS = listOf("2-Pin 0.78mm", "MMCX", "QDC", "3.5mm", "4.4mm Balanced", "USB-C")
    val SIGNATURE_SUGGESTIONS = listOf("Harman", "Warm", "Neutral", "V-Shaped", "Bright", "Basshead")
}