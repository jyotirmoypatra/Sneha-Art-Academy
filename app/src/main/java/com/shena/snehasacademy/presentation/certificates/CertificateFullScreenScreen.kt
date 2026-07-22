package com.shena.snehasacademy.presentation.certificates

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.PixelCopy
import android.view.Window
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect as ComposeRect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

private suspend fun capturePixelCopyBitmap(window: Window, rect: android.graphics.Rect): Bitmap =
    suspendCancellableCoroutine { continuation ->
        val bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888)
        PixelCopy.request(
            window,
            rect,
            bitmap,
            { result ->
                if (result == PixelCopy.SUCCESS) {
                    continuation.resume(bitmap)
                } else {
                    continuation.resumeWithException(RuntimeException("Screen capture failed (code $result)"))
                }
            },
            Handler(Looper.getMainLooper())
        )
    }

private fun saveBitmapAsPng(context: Context, bitmap: Bitmap, fileName: String): Boolean = try {
    val resolver = context.contentResolver
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/SnehasAcademy")
    }
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    if (uri != null) {
        resolver.openOutputStream(uri)?.use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        true
    } else {
        false
    }
} catch (e: Exception) {
    false
}

private fun saveBitmapAsPdf(context: Context, bitmap: Bitmap, fileName: String): Boolean = try {
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
    val page = document.startPage(pageInfo)
    page.canvas.drawBitmap(bitmap, 0f, 0f, null)
    document.finishPage(page)

    val resolver = context.contentResolver
    val values = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
        put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/SnehasAcademy")
    }
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
    val saved = if (uri != null) {
        resolver.openOutputStream(uri)?.use { out -> document.writeTo(out) }
        true
    } else {
        false
    }
    document.close()
    saved
} catch (e: Exception) {
    false
}

/** Full-screen certificate viewer with export-to-PNG and export-to-PDF actions. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateFullScreenScreen(data: CertificateTemplateData, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var certificateBounds by remember { mutableStateOf<ComposeRect?>(null) }
    var isExporting by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    fun export(asPdf: Boolean) {
        val activity = context.findActivity()
        val bounds = certificateBounds
        if (activity == null || bounds == null) return
        isExporting = true
        statusMessage = null
        scope.launch {
            try {
                val rect = android.graphics.Rect(
                    bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()
                )
                val bitmap = capturePixelCopyBitmap(activity.window, rect)
                val fileName = "Certificate_${data.certificateId}"
                val success = if (asPdf) {
                    saveBitmapAsPdf(context, bitmap, "$fileName.pdf")
                } else {
                    saveBitmapAsPng(context, bitmap, "$fileName.png")
                }
                statusMessage = if (success) {
                    if (asPdf) "Saved to Downloads/SnehasAcademy" else "Saved to Pictures/SnehasAcademy"
                } else {
                    "Failed to save. Please try again."
                }
            } catch (e: Exception) {
                statusMessage = e.localizedMessage ?: "Failed to export. Please try again."
            } finally {
                isExporting = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Certificate Preview") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { if (!isExporting) export(asPdf = false) }, enabled = !isExporting) {
                        Icon(Icons.Rounded.Image, contentDescription = "Export as PNG")
                    }
                    IconButton(onClick = { if (!isExporting) export(asPdf = true) }, enabled = !isExporting) {
                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = "Export as PDF")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CertificateTemplate(
                data = data,
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates -> certificateBounds = coordinates.boundsInWindow() }
            )
            statusMessage?.let { message ->
                Text(message, modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.bodySmall)
            }
            if (isExporting) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
