package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.qr.QrDecoder
import com.srfmolina.krocy.domain.model.server.QrFrame
import com.srfmolina.krocy.domain.repository.QrScannerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class QrScannerRepositoryImpl(
    private val decoder: QrDecoder,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : QrScannerRepository {

    override suspend fun decode(frame: QrFrame): String? =
        withContext(dispatcher) { decoder.decode(frame) }
}
