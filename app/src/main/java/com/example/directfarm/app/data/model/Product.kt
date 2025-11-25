package com.example.directfarm.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String = "",
    val farmerId: String = "",
    val farmerName: String = "",
    val farmerLocation: String = "",
    val farmerPhone: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",
    val pricePerKg: Double = 0.0,
    val availableQuantity: Double = 0.0,
    val imageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {

    fun getFormattedPrice(): String = "৳%.2f/kg".format(pricePerKg)

    fun getFormattedQuantity(): String = "%.1f kg available".format(availableQuantity)

    fun isAvailable(): Boolean = availableQuantity > 0

    companion object {
        val CATEGORIES = listOf(
            "Vegetables",
            "Fruits",
            "Grains",
            "Dairy",
            "Other"
        )
    }
}