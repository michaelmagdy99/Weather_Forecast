package com.example.weatherforecast.utilities

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        @Volatile
        private var instance: SharedPreferencesHelper? = null

        fun getInstance(context: Context): SharedPreferencesHelper {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferencesHelper(context).also { instance = it }
            }
        }
    }

    fun saveData(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun loadData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun removeData(key: String) {
        editor.remove(key)
        editor.apply()
    }
}
