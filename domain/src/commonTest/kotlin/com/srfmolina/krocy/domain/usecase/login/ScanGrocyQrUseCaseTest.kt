package com.srfmolina.krocy.domain.usecase.login

import com.srfmolina.krocy.domain.model.server.GrocyQrCredentials
import com.srfmolina.krocy.domain.model.server.QrFrame
import com.srfmolina.krocy.domain.model.server.QrScanOutcome
import com.srfmolina.krocy.domain.repository.QrScannerRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ScanGrocyQrUseCaseTest {

    private class QrScannerRepositoryFake(
        private val onDecode: (QrFrame) -> String?
    ) : QrScannerRepository {
        override suspend fun decode(frame: QrFrame): String? = onDecode(frame)
    }

    private val frame = QrFrame(luminance = ByteArray(4), width = 2, height = 2)

    @Test
    fun `an empty frame is reported as not found`() = runTest {
        val useCase = ScanGrocyQrUseCase(QrScannerRepositoryFake { null })
        assertEquals(QrScanOutcome.NotFound, useCase(frame).getOrThrow())
    }

    @Test
    fun `a non grocy qr is reported as not grocy`() = runTest {
        val useCase = ScanGrocyQrUseCase(QrScannerRepositoryFake { "WIFI:S:MiRed;;" })
        assertEquals(QrScanOutcome.NotGrocy, useCase(frame).getOrThrow())
    }

    @Test
    fun `a grocy qr is reported as found with parsed credentials`() = runTest {
        val useCase = ScanGrocyQrUseCase(
            QrScannerRepositoryFake { "https://grocy.casa/api|key123" }
        )
        val outcome = useCase(frame).getOrThrow()
        assertIs<QrScanOutcome.Found>(outcome)
        assertEquals(
            GrocyQrCredentials.SelfHosted("https://grocy.casa", "key123"),
            outcome.credentials
        )
    }

    @Test
    fun `a decoder failure surfaces as a failed result`() = runTest {
        val useCase = ScanGrocyQrUseCase(
            QrScannerRepositoryFake { throw IllegalStateException("camera closed") }
        )
        assertTrue(useCase(frame).isFailure)
    }
}
