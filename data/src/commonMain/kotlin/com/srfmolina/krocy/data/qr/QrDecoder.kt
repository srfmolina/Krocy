package com.srfmolina.krocy.data.qr

import com.srfmolina.krocy.domain.model.server.QrFrame

/**
 * Reads a QR code out of a grayscale frame. Both platforms use the same ZXing implementation;
 * the expect/actual pair exists only because ZXing cannot be declared in commonMain.
 */
internal interface QrDecoder {
    fun decode(frame: QrFrame): String?
}

internal expect fun createQrDecoder(): QrDecoder
