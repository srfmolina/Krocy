package com.srfmolina.krocy.ui.presentation.common.scanner

import android.Manifest
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
import androidx.camera.lifecycle.awaitInstance
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
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlinx.coroutines.CancellationException

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
    var cameraError by remember { mutableStateOf(false) }

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
            try {
                val provider = ProcessCameraProvider.awaitInstance(context)
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
                                // A frame we cannot read (an unusual OEM buffer layout, an
                                // out-of-range stride) is a dropped frame, not a crash - the
                                // same reasoning as the provider/bind guards above. CameraX
                                // runs this on its own executor, so an uncaught Throwable here
                                // would reach the thread's default handler and take the app down.
                                onFrame(image.toQrFrame())
                            } catch (e: Throwable) {
                                // Drop the frame and keep scanning.
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
            } catch (e: CancellationException) {
                throw e // never swallow cancellation
            } catch (e: Throwable) {
                // No usable camera, or the provider failed to start. The manifest marks the
                // camera as not required, so this must degrade to a message rather than take
                // the app down - the form still works by hand.
                cameraError = true
            }
        }

        if (cameraError) {
            CameraErrorMessage(onDismiss = onDismiss)
            return@Surface
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

@Composable
private fun CameraErrorMessage(onDismiss: () -> Unit) {
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
            text = "No se ha podido iniciar la cámara. Puedes introducir los datos a mano.",
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onDismiss) { Text("Volver") }
    }
}

/**
 * Copies the Y (luminance) plane into a tightly packed buffer. The camera pads rows to a stride
 * that is often wider than the image, so a straight bulk copy would shear the picture.
 */
private fun ImageProxy.toQrFrame(): QrFrame {
    val plane = planes[0]
    val data = packLuminance(plane.buffer, plane.rowStride, width, height)
    return QrFrame(luminance = data, width = width, height = height)
}

/**
 * Pure pixel-packing logic pulled out of [toQrFrame] so it can be unit-tested without any
 * CameraX types: copies a possibly row-padded buffer into a tightly packed `width * height`
 * byte array.
 */
internal fun packLuminance(buffer: ByteBuffer, rowStride: Int, width: Int, height: Int): ByteArray {
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
    return data
}
