package com.srfmolina.krocy.ui.presentation.common.scanner

import androidx.compose.runtime.Composable
import com.srfmolina.krocy.domain.model.server.QrFrame

/** Desktop has no camera scanner; the setup screen hides the scan button entirely. */
internal actual val isQrScannerSupported: Boolean = false

@Composable
internal actual fun CameraQrScanner(
    onFrame: (QrFrame) -> Unit,
    onDismiss: () -> Unit
) = Unit
