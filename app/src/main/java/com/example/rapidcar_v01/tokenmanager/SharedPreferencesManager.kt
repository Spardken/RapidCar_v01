package com.example.rapidcar_v01.tokenmanager

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String?) {
        val editor = sharedPreferences.edit()
        editor.putString("AuthToken", token)
        editor.apply()
        Log.d("SharedPreferencesManager", "Token saved: $token")
    }

    fun fetchAuthToken(): String? {
        val token = sharedPreferences.getString("AuthToken", null)
        Log.d("SharedPreferencesManager", "Fetched token: $token")
        return token
    }

    fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences
    }

    /*fun clearAuthToken() {
        val editor = sharedPreferences.edit()
        editor.remove("AuthToken")
        editor.apply()
        Log.d("SharedPreferencesManager", "AuthToken cleared")
    }*/
}