package com.maggie.rapidsync.model.pojo

import androidx.annotation.DrawableRes
import com.maggie.rapidsync.R

data class Option(
    val title: String,
    val description: String = "",
    @DrawableRes val icon: Int = R.drawable.baseline_android_24
)