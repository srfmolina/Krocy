package com.srfmolina.krocy.domain.model.server

/**
 * The result of examining a single camera frame.
 *
 * [NotFound] and [NotGrocy] must stay distinct: most frames simply contain no QR and have to be
 * ignored in silence, while a QR that is not a Grocy payload has to raise an error - otherwise
 * the user points the camera at the wrong code and watches nothing happen forever.
 */
sealed interface QrScanOutcome {
    data object NotFound : QrScanOutcome
    data object NotGrocy : QrScanOutcome
    data class Found(val credentials: GrocyQrCredentials) : QrScanOutcome
}
