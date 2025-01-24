package com.maggie.rapidsync.commons

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocalDataStore(private val sharedPreferences: SharedPreferences, private val gson: Gson) {


    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun <T> saveObject(key: String, value: T) {
        val json = gson.toJson(value)
        saveString(key, json)
    }

    fun <T> getObject(key: String, valueType: TypeToken<T>): T? {
        val json = getString(key, null)
        return if (json != null) {
            gson.fromJson(json, valueType.type)
        } else {
            null
        }
    }

    fun removeObject(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }
}
