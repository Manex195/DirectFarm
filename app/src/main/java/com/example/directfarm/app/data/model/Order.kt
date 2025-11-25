package com.example.directfarm.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    DELIVERED,
    CANCELLED
}

@Parcelize
data class Order(
    val id: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val farmerId: String = "",
    val farmerName: String = "",
    val farmerPhone: String = "",
    val productId: String = "",
    val productName: String = "",
    val productImageUrl: String = "",
    val quantity: Double = 0.0,
    val pricePerKg: Double = 0.0,
    val totalPrice: Double = 0.0,
    val deliveryAddress: String = "",
    val status: String = OrderStatus.PENDING.name,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {

    fun getFormattedTotal(): String = "৳%.2f".format(totalPrice)

    fun getFormattedQuantity(): String = "%.1f kg".format(quantity)

    fun getStatusColor(): Int = when (status) {
        OrderStatus.PENDING.name -> android.R.color.holo_orange_dark
        OrderStatus.CONFIRMED.name -> android.R.color.holo_blue_dark
        OrderStatus.DELIVERED.name -> android.R.color.holo_green_dark
        OrderStatus.CANCELLED.name -> android.R.color.holo_red_dark
        else -> android.R.color.darker_gray
    }
}