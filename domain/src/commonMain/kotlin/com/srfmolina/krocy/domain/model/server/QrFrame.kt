package com.srfmolina.krocy.domain.model.server

/**
 * One grayscale camera frame on its way to the QR decoder: [luminance] holds `width * height`
 * bytes, one per pixel, row-major.
 *
 * Deliberately not a data class - a ByteArray in a generated equals/hashCode compares by
 * identity, which is a trap. Frames are transient carriers: never stored, never compared.
 *
 * No rotation field: QR finder patterns are rotation-invariant, so ZXing reads a sideways
 * frame without help.
 */
class QrFrame(
    val luminance: ByteArray,
    val width: Int,
    val height: Int
)
