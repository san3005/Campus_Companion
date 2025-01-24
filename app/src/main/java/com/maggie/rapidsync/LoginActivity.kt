package com.maggie.rapidsync

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.showToast
import com.maggie.rapidsync.databinding.ActivityLoginBinding
import com.maggie.rapidsync.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 123

    private val loginViewModel: LoginViewModel by viewModels()
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // write code to request notification permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
            }else{
                Log.e("Permission", "Permission not tiramisu")
            }
        }


        binding.btnLogin.setOnClickListener {
            if (binding.etEmail.text.toString().isNotEmpty() && binding.etPassword.text.toString()
                    .isNotEmpty()
            )
                loginViewModel.login(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                )
            else
                showToast("Please fill in all fields")
        }


        lifecycleScope.launch {
            loginViewModel.loginResponse.collect {
                when (it) {

                    is NetworkResult.Loading -> {
//                        showToast("Loading")
                    }

                    is NetworkResult.Success -> {
                        showToast("Login Successful")
                        startActivity(DashBoardActivity.newIntent(this@LoginActivity))
                    }

                    is NetworkResult.Error -> {
                        showToast(it.errorMessage)
                    }

                    else -> {

                    }
                }
            }
        }


    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can show notifications
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "App will not work properly without notification permission", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        fun newIntent(dashBoardActivity: DashBoardActivity): Intent {
            return Intent(dashBoardActivity, LoginActivity::class.java)
        }
    }
}