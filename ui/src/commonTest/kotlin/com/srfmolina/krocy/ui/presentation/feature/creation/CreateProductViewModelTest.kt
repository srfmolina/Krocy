package com.srfmolina.krocy.ui.presentation.feature.creation

import com.srfmolina.krocy.domain.model.masterdata.Location
import com.srfmolina.krocy.domain.model.masterdata.ProductGroup
import com.srfmolina.krocy.domain.model.masterdata.QuConversion
import com.srfmolina.krocy.domain.model.masterdata.QuantityUnit
import com.srfmolina.krocy.domain.model.masterdata.ShoppingLocation
import com.srfmolina.krocy.domain.model.product.NewProduct
import com.srfmolina.krocy.domain.model.product.NewQuConversion
import com.srfmolina.krocy.domain.model.product.ProductOption
import com.srfmolina.krocy.domain.model.product.ProductPurchaseInfo
import com.srfmolina.krocy.domain.repository.MasterRepository
import com.srfmolina.krocy.domain.repository.ProductRepository
import com.srfmolina.krocy.domain.usecase.masterdata.GetLocationsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetProductGroupsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetQuConversionsUseCase
import com.srfmolina.krocy.domain.usecase.masterdata.GetQuantityUnitsUseCase
import com.srfmolina.krocy.domain.usecase.product.CreateProductUseCase
import com.srfmolina.krocy.domain.usecase.product.CreateQuConversionUseCase
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class CreateProductViewModelTest {

    private val masterStub = object : MasterRepository {
        override suspend fun getQuantityUnits() = emptyList<QuantityUnit>()
        override suspend fun getLocations() = emptyList<Location>()
        override suspend fun getProductGroups() = emptyList<ProductGroup>()
        override suspend fun getQuConversions() = emptyList<QuConversion>()
        override suspend fun getShoppingLocations() = emptyList<ShoppingLocation>()
    }

    private class ProductRepositoryFake : ProductRepository {
        var createdProducts = 0

        override suspend fun createProduct(product: NewProduct): Int {
            delay(100) // keep the request in flight so a second submit can race it
            createdProducts++
            return 1
        }

        override suspend fun createQuConversion(conversion: NewQuConversion): Int = 1

        override suspend fun getProducts() = emptyList<ProductOption>()

        override suspend fun getProductPurchaseInfo(productId: Int): ProductPurchaseInfo =
            error("not used in this test")
    }

    private fun viewModel(repo: ProductRepository) = CreateProductViewModel(
        createProductUseCase = CreateProductUseCase(repo),
        createQuConversionUseCase = CreateQuConversionUseCase(repo),
        getQuantityUnitsUseCase = GetQuantityUnitsUseCase(masterStub),
        getLocationsUseCase = GetLocationsUseCase(masterStub),
        getProductGroupsUseCase = GetProductGroupsUseCase(masterStub),
        getQuConversionsUseCase = GetQuConversionsUseCase(masterStub),
    )

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `a second submit while one is in flight does not create a duplicate product`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val repo = ProductRepositoryFake()
        val viewModel = viewModel(repo)

        // Both events carry the same pre-submit snapshot, as a fast double-tap would.
        val form = State(name = "Leche", stockUnitId = 1, purchaseUnitId = 1, locationId = 2)
        viewModel.launchEvent(Event.OnSubmit(form))
        viewModel.launchEvent(Event.OnSubmit(form))
        advanceUntilIdle()

        assertEquals(1, repo.createdProducts)
        assertFalse(viewModel.state.value.isSubmitting)
        assertEquals(
            Effect.ProductCreated("Leche", conversionWarning = false),
            viewModel.effect.first(),
        )
        // The rejected submit must not have produced a second effect.
        assertNull(withTimeoutOrNull(100) { viewModel.effect.first() })
    }
}
