package com.srfmolina.krocy.ui.presentation.common.scanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.srfmolina.krocy.domain.model.server.QrFrame
import com.srfmolina.krocy.ui.presentation.theme.spacing
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

internal actual val isQrScannerSupported: Boolean = true

@Composable
internal actual fun CameraQrScanner(
    onFrame: (QrFrame) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        permissionDenied = !granted
    }

    LaunchedEffect(Unit) {
        if (!permissionGranted) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (!permissionGranted) {
            PermissionMessage(denied = permissionDenied, onDismiss = onDismiss)
            return@Surface
        }

        var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
        var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
        val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

        DisposableEffect(Unit) {
            onDispose {
                cameraProvider?.unbindAll()
                analysisExecutor.shutdown()
            }
        }

        LaunchedEffect(Unit) {
            val provider = awaitProcessCameraProvider(context)
            cameraProvider = provider

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider { request -> surfaceRequest = request }
            }
            val analysis = ImageAnalysis.Builder()
                // Decoding is slower than capture; queued frames would only ever be stale.
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(analysisExecutor) { image ->
                        try {
                            onFrame(image.toQrFrame())
                        } finally {
                            image.close()
                        }
                    }
                }

            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analysis
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            surfaceRequest?.let { request ->
                CameraXViewfinder(
                    surfaceRequest = request,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = "Apunta al código QR de Grocy",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(MaterialTheme.spacing.s4)
            )
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(MaterialTheme.spacing.s4)
            ) { Text("Cancelar") }
        }
    }
}

@Composable
private fun PermissionMessage(denied: Boolean, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.spacing.s4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.s3,
            alignment = Alignment.CenterVertically
        )
    ) {
        Text(
            text = if (denied) {
                "Krocy necesita permiso de cámara para leer el código QR. " +
                    "Puedes concederlo en los ajustes del sistema, o introducir los datos a mano."
            } else {
                "Solicitando permiso de cámara…"
            },
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onDismiss) { Text("Volver") }
    }
}

/**
 * CameraX 1.6.2 does not ship the `awaitInstance` coroutine extension yet - only the classic
 * `ListenableFuture` accessor - so this bridges it with `suspendCancellableCoroutine` instead of
 * pulling in `kotlinx-coroutines-guava` for a single call site.
 */
private suspend fun awaitProcessCameraProvider(context: Context): ProcessCameraProvider =
    suspendCancellableCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener(
            { continuation.resume(future.get()) },
            ContextCompat.getMainExecutor(context)
        )
    }

/**
 * Copies the Y (luminance) plane into a tightly packed buffer. The camera pads rows to a stride
 * that is often wider than the image, so a straight bulk copy would shear the picture.
 */
private fun ImageProxy.toQrFrame(): QrFrame {
    val plane = planes[0]
    val buffer = plane.buffer
    val rowStride = plane.rowStride
    val data = ByteArray(width * height)

    if (rowStride == width) {
        buffer.get(data)
    } else {
        val row = ByteArray(rowStride)
        for (y in 0 until height) {
            buffer.position(y * rowStride)
            val available = minOf(rowStride, buffer.remaining())
            buffer.get(row, 0, available)
            row.copyInto(data, destinationOffset = y * width, startIndex = 0, endIndex = width)
        }
    }
    return QrFrame(luminance = data, width = width, height = height)
}
