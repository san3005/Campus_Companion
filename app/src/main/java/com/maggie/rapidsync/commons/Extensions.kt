package com.maggie.rapidsync.commons

import android.app.Activity
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.maggie.rapidsync.model.pojo.ErrorResponse
import okhttp3.ResponseBody
import org.json.JSONObject


fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun Activity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun ResponseBody.toErrorMessage(): ErrorResponse {
    val gson = Gson()
    return gson.fromJson(this.charStream(), ErrorResponse::class.java)
}