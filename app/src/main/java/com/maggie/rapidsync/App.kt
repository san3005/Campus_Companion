package com.maggie.rapidsync

import android.app.Application
import com.maggie.rapidsync.model.Constants
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Timber for logging
        Timber.plant(Timber.DebugTree())

        PaymentConfiguration.init(applicationContext, Constants.STRIPE_PUBLISHABLE_KEY)

        // Initialize Stripe with debug logging
//        Stripe(this, Constants.STRIPE_PUBLISHABLE_KEY, enableLogging = true)
    }
}