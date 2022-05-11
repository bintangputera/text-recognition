package com.bintangpoetra.text_recognition.utils

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage

fun imageFromBitmap(bitmap: Bitmap): InputImage {
    val rotationDegrees = 0
    // [START image_from_bitmap]
    // [END image_from_bitmap]
    return InputImage.fromBitmap(bitmap, 0)
}