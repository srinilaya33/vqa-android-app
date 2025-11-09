package com.example.vqaapp

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    
    private lateinit var imageView: ImageView
    private lateinit var btnCapture: Button
    private lateinit var etQuestion: EditText
    private lateinit var btnVoice: Button
    private lateinit var btnAsk: Button
    private lateinit var tvAnswer: TextView
    
    private var currentBitmap: Bitmap? = null
    
    // Permission codes
    private companion object {
        const val CAMERA_PERMISSION_CODE = 100
        const val CAMERA_REQUEST_CODE = 101
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize views
        imageView = findViewById(R.id.imageView)
        btnCapture = findViewById(R.id.btnCapture)
        etQuestion = findViewById(R.id.etQuestion)
        btnVoice = findViewById(R.id.btnVoice)
        btnAsk = findViewById(R.id.btnAsk)
        tvAnswer = findViewById(R.id.tvAnswer)
        
        // Set up click listeners
        btnCapture.setOnClickListener {
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }
        
        btnVoice.setOnClickListener {
            Toast.makeText(this, "Type your question for now", Toast.LENGTH_SHORT).show()
        }
        
        btnAsk.setOnClickListener {
            askQuestionToBackend()
        }
        
        // Show initial message
        tvAnswer.text = "Take a picture and ask a question to get started!"
    }
    
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }
    
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            currentBitmap = imageBitmap
            imageView.setImageBitmap(imageBitmap)
            tvAnswer.text = "Picture taken! Now enter your question."
        }
    }
    
    private fun askQuestionToBackend() {
        val question = etQuestion.text.toString().trim()
        
        if (question.isEmpty()) {
            Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (currentBitmap == null) {
            Toast.makeText(this, "Please take a picture first", Toast.LENGTH_SHORT).show()
            return
        }
        
        tvAnswer.text = "Asking question... Please wait"
        btnAsk.isEnabled = false
        
        // Run network call in background thread
        Thread {
            try {
                val imageBytes = bitmapToByteArray(currentBitmap!!)
                val response = makeHttpRequest(question, imageBytes)
                
                runOnUiThread {
                    btnAsk.isEnabled = true
                    tvAnswer.text = response
                }
                
            } catch (e: Exception) {
                runOnUiThread {
                    btnAsk.isEnabled = true
                    tvAnswer.text = "❌ Error: ${e.message}\n\nPlease check:\n1. Backend is running at 10.12.79.24:8000\n2. Same WiFi network\n3. Firewall allows port 8000"
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
    
    private fun makeHttpRequest(question: String, imageBytes: ByteArray): String {
        val boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW"
        val url = URL("http://10.12.79.24:8000/answer")
        val connection = url.openConnection() as HttpURLConnection
        
        try {
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            connection.setRequestProperty("User-Agent", "Android VQA App")
            
            val outputStream = connection.outputStream
            val writer = PrintWriter(OutputStreamWriter(outputStream, "UTF-8"), true)
            
            // Add question part
            writer.append("--$boundary\r\n")
            writer.append("Content-Disposition: form-data; name=\"question\"\r\n\r\n")
            writer.append(question).append("\r\n")
            
            // Add image part
            writer.append("--$boundary\r\n")
            writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"\r\n")
            writer.append("Content-Type: image/jpeg\r\n\r\n")
            writer.flush()
            
            outputStream.write(imageBytes)
            outputStream.flush()
            
            writer.append("\r\n")
            writer.append("--$boundary--\r\n")
            writer.flush()
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                return "✅ SUCCESS!\n\nResponse: $response"
            } else {
                return "❌ Server Error: $responseCode - ${connection.responseMessage}"
            }
            
        } finally {
            connection.disconnect()
        }
    }
    
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return stream.toByteArray()
    }
}