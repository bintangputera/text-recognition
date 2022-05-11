package com.bintangpoetra.text_recognition

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bintangpoetra.text_recognition.databinding.ActivityMainBinding
import com.bintangpoetra.text_recognition.utils.ConstVal.CAMERA_X_RESULT
import com.bintangpoetra.text_recognition.utils.ConstVal.KEY_PICTURE
import com.bintangpoetra.text_recognition.utils.ConstVal.REQUEST_CODE_PERMISSIONS
import com.bintangpoetra.text_recognition.utils.imageFromBitmap
import com.bintangpoetra.text_recognition.utils.showToast
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var _activityMainBinding: ActivityMainBinding
    private val binding get() = _activityMainBinding

    companion object {
        private val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_activityMainBinding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        initAction()
    }

    private fun initAction() {
        binding.fabCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            launchIntentCamera.launch(intent)
        }
    }

    private val launchIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val file = it?.data?.getSerializableExtra(KEY_PICTURE) as File

            val result = BitmapFactory.decodeFile(file.path)
            val image = imageFromBitmap(result)

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if(visionText.text == " ") {
                        showToast("Text tidak terdeteksi, coba lagi")
                    } else {
                        showToast(visionText.text)
                        /*val resultText = visionText.text
                        for (block in visionText.textBlocks) {
                            val blockText = block.text
                            val blockCornerPoints = block.cornerPoints
                            val blockFrame = block.boundingBox
                            for (line in block.lines) {
                                val lineText = line.text
                                val lineCornerPoints = line.cornerPoints
                                val lineFrame = line.boundingBox
                                for (elements in line.elements) {

                                }
                            }
                        }*/
                    }
                }
                .addOnFailureListener { e ->
                    showToast(e.message.toString())
                }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!allPermissionsGranted()) {
            showToast("Message not permitted")
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

}