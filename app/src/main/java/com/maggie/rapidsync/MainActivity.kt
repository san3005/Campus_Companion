package com.maggie.rapidsync

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.maggie.rapidsync.commons.LocalDataStore
import com.maggie.rapidsync.databinding.ActivityMainBinding
import com.maggie.rapidsync.model.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @Inject
    lateinit var localDataStore: LocalDataStore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (localDataStore.getBoolean(Constants.IS_LOGGED_IN, false)) {
            startActivity(Intent(this, DashBoardActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


    }
}