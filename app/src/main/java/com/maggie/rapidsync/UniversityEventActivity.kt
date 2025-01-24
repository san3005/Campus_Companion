package com.maggie.rapidsync

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.maggie.rapidsync.databinding.ActivityUniversityEventBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UniversityEventActivity : AppCompatActivity() {


    private val binding by lazy { ActivityUniversityEventBinding.inflate(layoutInflater) }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.title.text = intent.getStringExtra("name")
        binding.description.text = intent.getStringExtra("description")
        binding.date.text = intent.getStringExtra("date")
        binding.time.text = intent.getStringExtra("time") + ", " + intent.getStringExtra("location")
        binding.image.load(intent.getStringExtra("image"))
    }

    companion object {
        fun newIntent(dashBoardActivity: DashBoardActivity): Intent {
            return Intent(dashBoardActivity, UniversityEventActivity::class.java)
        }
    }
}