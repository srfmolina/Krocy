package com.srfmolina.krocy.data.qr

import com.google.zxing.BinaryBitmap
import com.google.zxing.ChecksumException
import com.google.zxing.FormatException
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.srfmolina.krocy.domain.model.server.QrFrame

internal class ZxingQrDecoder : QrDecoder {

    override fun decode(frame: QrFrame): String? {
        val source = PlanarYUVLuminanceSource(
            frame.luminance,
            frame.width,
            frame.height,
            0,
            0,
            frame.width,
            frame.height,
            false
        )
        // A fresh reader per call: QRCodeReader is not thread safe and construction is cheap.
        return try {
            QRCodeReader().decode(BinaryBitmap(HybridBinarizer(source))).text
        } catch (_: NotFoundException) {
            null // no QR in this frame - by far the common case while the user aims
        } catch (_: ChecksumException) {
            null // a QR too damaged or blurry to trust
        } catch (_: FormatException) {
            null // something QR-shaped that is not a valid QR
        }
    }
}
