package com.example.directfarm.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.directfarm.app.data.model.User
import com.example.directfarm.app.data.model.UserType

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    fun saveUser(user: User) {
        prefs.edit().apply {
            putString(KEY_USER_ID, user.uid)
            putString(KEY_EMAIL, user.email)
            putString(KEY_NAME, user.fullName)
            putString(KEY_PHONE, user.phoneNumber)
            putString(KEY_LOCATION, user.location)
            putString(KEY_USER_TYPE, user.userType)
            apply()
        }
    }

    fun getUser(): User? {
        val uid = prefs.getString(KEY_USER_ID, null) ?: return null
        return User(
            uid = uid,
            email = prefs.getString(KEY_EMAIL, "") ?: "",
            fullName = prefs.getString(KEY_NAME, "") ?: "",
            phoneNumber = prefs.getString(KEY_PHONE, "") ?: "",
            location = prefs.getString(KEY_LOCATION, "") ?: "",
            userType = prefs.getString(KEY_USER_TYPE, UserType.CUSTOMER.name) ?: UserType.CUSTOMER.name
        )
    }

    fun isLoggedIn(): Boolean = prefs.getString(KEY_USER_ID, null) != null

    fun clearUser() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "DirectFarmPrefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_NAME = "name"
        private const val KEY_PHONE = "phone"
        private const val KEY_LOCATION = "location"
        private const val KEY_USER_TYPE = "user_type"
    }
}