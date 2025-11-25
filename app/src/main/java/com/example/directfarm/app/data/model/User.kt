package com.example.directfarm.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val location: String = "",
    val userType: String = UserType.CUSTOMER.name,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {

    fun isCustomer() = userType == UserType.CUSTOMER.name
    fun isFarmer() = userType == UserType.FARMER.name

    companion object {
        fun empty() = User()
    }
}