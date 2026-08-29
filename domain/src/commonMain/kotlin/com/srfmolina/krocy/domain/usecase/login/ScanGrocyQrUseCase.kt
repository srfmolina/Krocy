package com.srfmolina.krocy.domain.usecase.login

import com.srfmolina.krocy.domain.model.server.GrocyQrCredentials
import com.srfmolina.krocy.domain.model.server.QrFrame
import com.srfmolina.krocy.domain.model.server.QrScanOutcome
import com.srfmolina.krocy.domain.repository.QrScannerRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCase

class ScanGrocyQrUseCase(
    private val qrScannerRepository: QrScannerRepository
) : ResultUseCase<QrFrame, QrScanOutcome>() {
    override suspend fun execute(params: QrFrame): QrScanOutcome {
        val decoded = qrScannerRepository.decode(params) ?: return QrScanOutcome.NotFound
        val credentials = GrocyQrCredentials.parse(decoded) ?: return QrScanOutcome.NotGrocy
        return QrScanOutcome.Found(credentials)
    }
}
