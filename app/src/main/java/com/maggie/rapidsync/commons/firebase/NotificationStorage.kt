package com.maggie.rapidsync.commons.firebase

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.maggie.rapidsync.model.pojo.Notification

object NotificationStorage {

    private const val PREFS_NAME = "NotificationPrefs"
    private const val KEY_NOTIFICATIONS = "notifications"

    fun saveNotification(context: Context, notification: Notification) {
        val notifications = getNotifications(context).toMutableList()
        notifications.add(notification)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(notifications)
        editor.putString(KEY_NOTIFICATIONS, json)
        editor.apply()
    }

    fun getNotifications(context: Context): List<Notification> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString(KEY_NOTIFICATIONS, null)
        val type = object : TypeToken<List<Notification>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}
