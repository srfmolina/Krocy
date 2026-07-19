package com.srfmolina.krocy.data.di

import com.srfmolina.krocy.domain.model.server.ServerConfig
import com.srfmolina.krocy.domain.repository.MasterRepository
import com.srfmolina.krocy.domain.repository.ProductRepository
import com.srfmolina.krocy.domain.repository.ShoppingListRepository
import com.srfmolina.krocy.domain.repository.StockRepository
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertNotNull

class SessionModuleTest {

    @Test
    fun `session module resolves every server-bound repository`() {
        val koin = koinApplication {
            modules(
                dataModule,
                sessionModule(ServerConfig.Demo, onSessionExpired = {})
            )
        }.koin
        assertNotNull(koin.get<StockRepository>())
        assertNotNull(koin.get<ShoppingListRepository>())
        assertNotNull(koin.get<MasterRepository>())
        assertNotNull(koin.get<ProductRepository>())
    }
}
