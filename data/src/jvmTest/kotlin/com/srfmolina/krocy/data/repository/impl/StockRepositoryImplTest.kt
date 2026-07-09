package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.datasource.remote.stock.StockDataSource
import com.srfmolina.krocy.domain.model.stock.NewPurchase
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.openapitools.client.models.CurrentStockResponse
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner
import org.openapitools.client.models.ProductDetailsResponse
import org.openapitools.client.models.StockLogEntry
import kotlin.test.Test
import kotlin.test.assertEquals

class StockRepositoryImplTest {

    private val genericStub = object : GenericEntityDataSource {
        override suspend fun getQuantityUnits() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun getLocations() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun getProductGroups() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun createProduct(body: ObjectsEntityGet200ResponseInner) = Result.success(1)
        override suspend fun getProducts() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun getShoppingLocations() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
    }

    private class StockDataSourceStub : StockDataSource {
        var purchasedProductId: Int? = null
        var purchasedAmount: Double? = null
        var purchasedDate: LocalDate? = null
        var purchasedPrice: Double? = null
        var purchasedLocationId: Int? = null
        var purchasedShoppingLocationId: Int? = null
        var purchasedNote: String? = null
        var stockFetches = 0

        override suspend fun getStock(): Result<List<CurrentStockResponse>> {
            stockFetches++
            return Result.success(emptyList())
        }

        override suspend fun consume(productId: Int, amount: Double) =
            Result.success(emptyList<StockLogEntry>())

        override suspend fun open(productId: Int, amount: Double) =
            Result.success(emptyList<StockLogEntry>())

        override suspend fun add(productId: Int, amount: Double) =
            Result.success(emptyList<StockLogEntry>())

        override suspend fun purchase(
            productId: Int,
            amount: Double,
            bestBeforeDate: LocalDate?,
            price: Double?,
            locationId: Int?,
            shoppingLocationId: Int?,
            note: String?,
        ): Result<List<StockLogEntry>> {
            purchasedProductId = productId
            purchasedAmount = amount
            purchasedDate = bestBeforeDate
            purchasedPrice = price
            purchasedLocationId = locationId
            purchasedShoppingLocationId = shoppingLocationId
            purchasedNote = note
            return Result.success(emptyList())
        }

        override suspend fun getProductDetails(productId: Int) =
            Result.success(ProductDetailsResponse())
    }

    @Test
    fun `purchase forwards the request and refreshes the stock cache`() = runBlocking {
        val stub = StockDataSourceStub()
        val repo = StockRepositoryImpl(stub, genericStub, baseUrl = "http://test")

        repo.purchase(
            NewPurchase(
                productId = 7,
                amountStockUnits = 12.0,
                dueDate = LocalDate(2026, 7, 15),
                pricePerStockUnit = 0.5,
                locationId = 11,
                shoppingLocationId = 5,
                note = "oferta",
            )
        )

        assertEquals(7, stub.purchasedProductId)
        assertEquals(12.0, stub.purchasedAmount)
        assertEquals(LocalDate(2026, 7, 15), stub.purchasedDate)
        assertEquals(0.5, stub.purchasedPrice)
        assertEquals(11, stub.purchasedLocationId)
        assertEquals(5, stub.purchasedShoppingLocationId)
        assertEquals("oferta", stub.purchasedNote)
        assertEquals(1, stub.stockFetches)
    }
}
