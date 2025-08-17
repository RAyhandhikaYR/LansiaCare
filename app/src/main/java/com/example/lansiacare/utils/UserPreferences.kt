package com.example.lansiacare.utils

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "lansia_care_prefs"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    // Save user session
    fun saveUserSession(email: String, name: String) {
        prefs.edit().apply {
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // Get current user email
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    // Get current user name
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Clear user session (logout)
    fun clearSession() {
        prefs.edit().apply {
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_NAME)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }
}