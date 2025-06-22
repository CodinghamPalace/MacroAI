package com.macroai.ui.components

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ImageCaptureButtons(
    onImageCaptured: (Bitmap) -> Unit,
    isEnabled: Boolean = true
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher - move up before it's used
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Safety check for non-null imageUri
            val currentUri = imageUri
            if (currentUri != null) {
                // Image captured successfully, convert URI to bitmap
                val bitmap = uriToBitmap(context, currentUri)
                bitmap?.let { onImageCaptured(it) }
            }
        }
    }

    // Request camera permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, launch camera with the already set imageUri
            val currentUri = imageUri
            if (currentUri != null) {
                cameraLauncher.launch(currentUri)
            }
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bitmap = uriToBitmap(context, uri)
            bitmap?.let { onImageCaptured(it) }
        }
    }

    Row {
        // Camera button
        IconButton(
            onClick = {
                // Check camera permission
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission already granted, launch camera
                    imageUri = createTempImageUri(context)
                    val currentUri = imageUri
                    if (currentUri != null) {
                        cameraLauncher.launch(currentUri)
                    }
                } else {
                    // Request camera permission - first create URI
                    imageUri = createTempImageUri(context)
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            enabled = isEnabled
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = "Take Photo"
            )
        }

        // Gallery button
        IconButton(
            onClick = { galleryLauncher.launch("image/*") },
            enabled = isEnabled
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = "Choose from Gallery"
            )
        }
    }
}

/**
 * Create a temporary URI for storing camera image
 */
private fun createTempImageUri(context: Context): Uri? {
    return try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, use MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val resolver = context.contentResolver
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            // For older versions, use FileProvider
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile(imageFileName, ".jpg", storageDir)
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Convert URI to Bitmap
 */
private fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
