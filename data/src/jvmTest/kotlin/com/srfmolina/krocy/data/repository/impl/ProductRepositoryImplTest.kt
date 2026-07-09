package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDataSource
import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.datasource.remote.stock.StockDataSource
import com.srfmolina.krocy.domain.model.product.NewQuConversion
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.openapitools.client.models.CurrentStockResponse
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner
import org.openapitools.client.models.Product
import org.openapitools.client.models.ProductDetailsResponse
import org.openapitools.client.models.QuantityUnit
import org.openapitools.client.models.QuantityUnitConversion
import org.openapitools.client.models.StockLogEntry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProductRepositoryImplTest {

    private val genericStub = object : GenericEntityDataSource {
        override suspend fun getQuantityUnits() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun getLocations() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun getProductGroups() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun createProduct(body: ObjectsEntityGet200ResponseInner) = Result.success(1)
        override suspend fun getProducts() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun getShoppingLocations() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
    }

    private val stockStub = object : StockDataSource {
        override suspend fun getStock() = Result.success(emptyList<CurrentStockResponse>())
        override suspend fun consume(productId: Int, amount: Double) = Result.success(emptyList<StockLogEntry>())
        override suspend fun open(productId: Int, amount: Double) = Result.success(emptyList<StockLogEntry>())
        override suspend fun add(productId: Int, amount: Double) = Result.success(emptyList<StockLogEntry>())
        override suspend fun purchase(
            productId: Int,
            amount: Double,
            bestBeforeDate: LocalDate?,
            price: Double?,
            locationId: Int?,
            shoppingLocationId: Int?,
            note: String?,
        ) = Result.success(emptyList<StockLogEntry>())
        override suspend fun getProductDetails(productId: Int) = Result.success(
            ProductDetailsResponse(
                product = Product(id = productId, name = "Leche", locationId = 11, defaultBestBeforeDays = 7),
                quantityUnitStock = QuantityUnit(id = 1, name = "Unidad", namePlural = "Unidades"),
                defaultQuantityUnitPurchase = QuantityUnit(id = 2, name = "Paquete", namePlural = "Paquetes"),
                quConversionFactorPurchaseToStock = 6.0,
            )
        )
    }

    private class ConversionsStub(
        private val existing: List<QuantityUnitConversion>,
        private val serverFiltersByProduct: Boolean = true,
    ) : QuConversionDataSource {
        var updatedId: Int? = null
        var updatedBody: QuantityUnitConversion? = null
        var createdBody: QuantityUnitConversion? = null

        override suspend fun getQuConversions() = Result.success(existing)

        override suspend fun getQuConversionsForProduct(productId: Int) =
            Result.success(
                if (serverFiltersByProduct) existing.filter { it.productId == productId } else existing
            )

        override suspend fun createQuConversion(body: QuantityUnitConversion): Result<Int> {
            createdBody = body
            return Result.success(21)
        }

        override suspend fun updateQuConversion(id: Int, body: QuantityUnitConversion): Result<Unit> {
            updatedId = id
            updatedBody = body
            return Result.success(Unit)
        }
    }

    @Test
    fun `updates the auto-created conversion row when one exists`() = runBlocking {
        // Grocy creates 1:1 purchase<->stock rows alongside the product.
        val stub = ConversionsStub(
            listOf(
                QuantityUnitConversion(id = 19, productId = 37, fromQuId = 3, toQuId = 6, factor = 1.0),
                QuantityUnitConversion(id = 20, productId = 37, fromQuId = 6, toQuId = 3, factor = 1.0),
            )
        )
        val repo = ProductRepositoryImpl(genericStub, stub, stockStub)

        val id = repo.createQuConversion(NewQuConversion(productId = 37, fromQuId = 3, toQuId = 6, factor = 3.0))

        assertEquals(19, id)
        assertEquals(19, stub.updatedId)
        assertEquals(3.0, stub.updatedBody?.factor)
        assertNull(stub.createdBody)
    }

    @Test
    fun `creates the conversion when none exists for the pair`() = runBlocking {
        val stub = ConversionsStub(emptyList())
        val repo = ProductRepositoryImpl(genericStub, stub, stockStub)

        val id = repo.createQuConversion(NewQuConversion(productId = 38, fromQuId = 9, toQuId = 11, factor = 3.0))

        assertEquals(21, id)
        assertEquals(3.0, stub.createdBody?.factor)
        assertNull(stub.updatedId)
    }

    @Test
    fun `never updates another product's or a global conversion even if the server filter fails`() = runBlocking {
        // Same unit pair, but a global row (null productId) and another product's row.
        val stub = ConversionsStub(
            listOf(
                QuantityUnitConversion(id = 5, productId = null, fromQuId = 3, toQuId = 6, factor = 2.0),
                QuantityUnitConversion(id = 6, productId = 99, fromQuId = 3, toQuId = 6, factor = 4.0),
            ),
            serverFiltersByProduct = false,
        )
        val repo = ProductRepositoryImpl(genericStub, stub, stockStub)

        val id = repo.createQuConversion(NewQuConversion(productId = 37, fromQuId = 3, toQuId = 6, factor = 3.0))

        assertEquals(21, id)
        assertNull(stub.updatedId)
        assertEquals(3.0, stub.createdBody?.factor)
    }

    @Test
    fun `getProductPurchaseInfo maps the product details`() = runBlocking {
        val repo = ProductRepositoryImpl(genericStub, ConversionsStub(emptyList()), stockStub)

        val info = repo.getProductPurchaseInfo(7)

        assertEquals(7, info.productId)
        assertEquals("Paquete", info.purchaseUnitName)
        assertEquals(6.0, info.conversionFactorPurchaseToStock)
        assertEquals(7, info.defaultBestBeforeDays)
        assertEquals(11, info.defaultLocationId)
    }

    @Test
    fun `getProducts maps entity rows to product options`() = runBlocking {
        val generic = object : GenericEntityDataSource by genericStub {
            override suspend fun getProducts() = Result.success(
                listOf(ObjectsEntityGet200ResponseInner(id = 7, name = "Leche"))
            )
        }
        val repo = ProductRepositoryImpl(generic, ConversionsStub(emptyList()), stockStub)

        val products = repo.getProducts()

        assertEquals(listOf(com.srfmolina.krocy.domain.model.product.ProductOption(7, "Leche")), products)
    }
}
