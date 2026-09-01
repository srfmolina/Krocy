package com.srfmolina.krocy.ui.presentation.common.scanner

import androidx.compose.runtime.Composable
import com.srfmolina.krocy.domain.model.server.QrFrame

/** False where no camera scanner exists; callers must not offer scanning then. */
internal expect val isQrScannerSupported: Boolean

/**
 * Full-screen camera viewfinder that emits every analysed frame through [onFrame].
 *
 * It decodes nothing and knows nothing about Grocy - the caller decides what a frame means and
 * when to stop. [onFrame] is called on a camera worker thread.
 */
@Composable
internal expect fun CameraQrScanner(
    onFrame: (QrFrame) -> Unit,
    onDismiss: () -> Unit
)
