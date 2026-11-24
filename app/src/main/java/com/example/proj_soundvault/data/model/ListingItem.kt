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
