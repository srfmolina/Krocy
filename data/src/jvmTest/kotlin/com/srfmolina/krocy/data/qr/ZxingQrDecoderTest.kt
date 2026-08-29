package com.srfmolina.krocy.data.qr

import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.srfmolina.krocy.domain.model.server.QrFrame
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ZxingQrDecoderTest {

    /** Renders [text] as a QR code and flattens it into the grayscale frame the decoder eats. */
    private fun qrFrameOf(text: String, size: Int = 240): QrFrame {
        val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
        val luminance = ByteArray(size * size)
        for (y in 0 until size) {
            for (x in 0 until size) {
                luminance[y * size + x] = if (matrix[x, y]) 0 else 255.toByte()
            }
        }
        return QrFrame(luminance = luminance, width = size, height = size)
    }

    @Test
    fun `decodes a rendered grocy payload`() {
        val payload = "http://10.10.10.11:8080/api|LwasdaXXXXXXXXXXXXXXX"
        assertEquals(payload, ZxingQrDecoder().decode(qrFrameOf(payload)))
    }

    @Test
    fun `decodes a long home assistant payload`() {
        val payload =
            "http://192.168.1.20:8123/api/hassio_ingress/abc123_igrocy-addon/api|vXNvFkey"
        assertEquals(payload, ZxingQrDecoder().decode(qrFrameOf(payload)))
    }

    @Test
    fun `a blank frame decodes to null`() {
        val size = 240
        val blank = QrFrame(
            luminance = ByteArray(size * size) { 255.toByte() },
            width = size,
            height = size
        )
        assertNull(ZxingQrDecoder().decode(blank))
    }
}
