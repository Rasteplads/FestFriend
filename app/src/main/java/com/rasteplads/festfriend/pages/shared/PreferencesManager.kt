package com.rasteplads.festfriend.pages.shared

import android.content.Context
import android.content.SharedPreferences


// Feel free to add more keys
object PreferenceKeys {
    val KEY_USERNAME = "username"
    val KEY_PASSWORD = "password"
    val KEY_GROUPID = "groupid"

}

/*
Use like this in composable:
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
*/
class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun setString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }
}
