package com.srfmolina.krocy.data.qr

internal actual fun createQrDecoder(): QrDecoder = ZxingQrDecoder()
