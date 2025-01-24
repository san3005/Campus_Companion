package com.maggie.rapidsync.model

object Constants {

    const val FCM_TOKEN_KEY: String = "FCM"
    const val BASE_URL = "http://rapidsync-env.eba-ikqxcugg.us-east-2.elasticbeanstalk.com/"
    const val BASE_URL_OPENAI = "https://api.openai.com/"
    const val PREFS_NAME = "RAPIDSYNC"
    const val TOKEN = "TOKEN"
    const val USER = "USER"
    const val IS_LOGGED_IN = "IS_LOGGED_IN"
const val CHAT_GPT_API_KEY = "your-secret-api-key"

    // Stripe-related keys, recommended to fetch from BuildConfig or environment variables
    val STRIPE_PUBLISHABLE_KEY: String = BuildConfig.STRIPE_PUBLISHABLE_KEY
    val STRIPE_ACCOUNT_ID: String = BuildConfig.STRIPE_ACCOUNT_ID

    const val TAMUCC_LOGO =
        "https://scontent-lga3-1.xx.fbcdn.net/v/t39.30808-6/351992889_1432049487633771_1408132778448320997_n.jpg?_nc_cat=106&ccb=1-7&_nc_sid=5f2048&_nc_ohc=V-YhjPwngycAX_Gi5TD&_nc_ht=scontent-lga3-1.xx&oh=00_AfCVs95ubvKLoP4bKhUVOxC0rFf_on9H2G9mieKK_jOD7w&oe=6603F1E5"
}
