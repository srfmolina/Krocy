package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.server.QrFrame

/** Reads QR codes out of raw camera frames. */
interface QrScannerRepository {
    /** The decoded text, or null when the frame holds no readable QR code. */
    suspend fun decode(frame: QrFrame): String?
}
