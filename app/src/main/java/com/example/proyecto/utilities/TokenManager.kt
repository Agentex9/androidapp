package com.example.proyecto.utilities

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.proyecto.models.UserInfo

class TokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs", // file name
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? = prefs.getString("auth_token", null)

    fun clearToken() {
        prefs.edit().remove("auth_token").apply()
        clearUser()
    }

    // --- User ---
    fun saveUser(user: UserInfo?) {
        if (user == null) return
        prefs.edit()
            .putString("user_id", user.id)
            .putString("user_name", user.first_name)
            .putString("user_surname", user.last_name)
            .putString("user_phone", user.phone_number)
            .apply()
    }

    fun getUser(): UserInfo? {
        val id = prefs.getString("user_id", null) ?: return null
        val name = prefs.getString("user_name", null)
        val surname = prefs.getString("user_surname", null)
        val phone = prefs.getString("user_phone", null)
        return UserInfo(id, name, surname, phone)
    }

    fun clearUser() {
        prefs.edit()
            .remove("user_id")
            .remove("user_name")
            .remove("user_surname")
            .remove("user_phone")
            .apply()
    }
}
