package com.maggie.rapidsync

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.maggie.rapidsync.commons.NetworkResult
import com.maggie.rapidsync.commons.showToast
import com.maggie.rapidsync.databinding.ActivityCourseRecommendationBinding
import com.maggie.rapidsync.di.OpenAIServiceRetrofit
import com.maggie.rapidsync.model.network.ChatRequest
import com.maggie.rapidsync.model.network.ChatResponse
import com.maggie.rapidsync.model.network.Message
import com.maggie.rapidsync.model.network.OpenAIService
import com.maggie.rapidsync.model.pojo.Semester
import com.maggie.rapidsync.viewmodel.CourseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CourseRecommendationActivity : AppCompatActivity() {

    private val viewModel: CourseViewModel by viewModels()

    val binding: ActivityCourseRecommendationBinding by lazy {
        ActivityCourseRecommendationBinding.inflate(layoutInflater)
    }

    private val REQUEST_IMAGE_CAPTURE = 1
    private val PICK_IMAGE_REQUEST = 2
    private val PERMISSIONS_REQUEST = 3

    private lateinit var selectedSemester: Semester

    @Inject
    @OpenAIServiceRetrofit
    lateinit var openAIService: OpenAIService

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            checkPermissionsAndShowOptions()
        }
        getSemesters()
        observeCourses()
    }

    private fun observeCourses() {
        lifecycleScope.launch {
            viewModel.coursesBySemester.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        result.body?.let { courses ->
                            binding.courses.text = courses.joinToString(", ") { it.subject.name }
                        }

                    }

                    is NetworkResult.Error -> {
                        showToast(result.errorMessage)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getSemesters() {
        lifecycleScope.launch {
            viewModel.getSemesters()

            viewModel.semesters.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val semesters = result.body!!
                        val semesterAdapter = ArrayAdapter(
                            this@CourseRecommendationActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            semesters.map { it.name })
                        binding.spinnerSemester.adapter = semesterAdapter
                        binding.spinnerSemester.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    val semester = semesters[position]
                                    selectedSemester = semester
                                    viewModel.getCoursesBySemester(semester.id)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }
                            }
                    }

                    is NetworkResult.Error -> {
                        showToast(result.errorMessage)
                    }

                    else -> {}
                }

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissionsAndShowOptions() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ),
                PERMISSIONS_REQUEST
            )
        } else {
            // Permissions are granted, show options to the user
            showCaptureOrGalleryOptions()
        }
    }

    private fun showCaptureOrGalleryOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(this)
            .setTitle("Select Action")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> dispatchTakePictureIntent()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    processImageForTextRecognition(imageBitmap)
                }

                PICK_IMAGE_REQUEST -> {
                    val imageBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
                    processImageForTextRecognition(imageBitmap)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // All requested permissions are granted
            showCaptureOrGalleryOptions()
        } else {
            // Permissions denied, show a message
            Toast.makeText(this, "Permissions required for this feature", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processImageForTextRecognition(imageBitmap: Bitmap) {
        val image = InputImage.fromBitmap(imageBitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->

                val prompt =
                    "This is text from my transcripts " + visionText.text + " and I want to know what courses I should take next semester. Select only from " + binding.courses.text + " or else say 'No recommendation for this semester because none match your transcript.'"
                makeChatGPTRequest(prompt, visionText.text)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                toast("Text recognition failed")
                e.printStackTrace()
            }
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun makeChatGPTRequest(prompt: String, text: String) {
        if (text.isEmpty()) {
            toast("No text found in the image")
            return
        }


        // Enqueue the request
        openAIService.createCompletion(
            ChatRequest(
                messages = listOf(
                    Message(
                        role = "user",
                        content = prompt
                    )
                )
            )
        )
            .enqueue(object : retrofit2.Callback<ChatResponse> {
                override fun onResponse(
                    call: retrofit2.Call<ChatResponse>,
                    response: retrofit2.Response<ChatResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.isSuccessful) {
                            // Update UI with the response
                            val replyText =
                                response.body()?.choices?.firstOrNull()?.message?.content
                                    ?: "Not able to process the request. Please try again later."
                            runOnUiThread {
                                // Update your UI here with the replyText
                                binding.tv.text = replyText
                            }
                        }
                    }
                }

                override fun onFailure(call: retrofit2.Call<ChatResponse>, t: Throwable) {
                    // Handle failure
                    t.printStackTrace()
                    toast("Failed to make the request")
                }
            })
    }

    companion object {
        fun newIntent(dashBoardActivity: DashBoardActivity): Intent {
            return Intent(dashBoardActivity, CourseRecommendationActivity::class.java)
        }
    }
}