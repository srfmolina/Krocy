package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDataSource
import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDto
import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.domain.model.product.NewQuConversion
import kotlinx.coroutines.runBlocking
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProductRepositoryImplTest {

    private val genericStub = object : GenericEntityDataSource {
        override suspend fun getQuantityUnits() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun getLocations() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun getProductGroups() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun createProduct(body: ObjectsEntityGet200ResponseInner) = Result.success(1)
    }

    private class ConversionsStub(
        private val existing: List<QuConversionDto>,
    ) : QuConversionDataSource {
        var updatedId: Int? = null
        var updatedBody: QuConversionDto? = null
        var createdBody: QuConversionDto? = null

        override suspend fun getQuConversions() = Result.success(existing)

        override suspend fun getQuConversionsForProduct(productId: Int) =
            Result.success(existing.filter { it.productId == productId })

        override suspend fun createQuConversion(body: QuConversionDto): Result<Int> {
            createdBody = body
            return Result.success(21)
        }

        override suspend fun updateQuConversion(id: Int, body: QuConversionDto): Result<Unit> {
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
                QuConversionDto(id = 19, productId = 37, fromQuId = 3, toQuId = 6, factor = 1.0),
                QuConversionDto(id = 20, productId = 37, fromQuId = 6, toQuId = 3, factor = 1.0),
            )
        )
        val repo = ProductRepositoryImpl(genericStub, stub)

        val id = repo.createQuConversion(NewQuConversion(productId = 37, fromQuId = 3, toQuId = 6, factor = 3.0))

        assertEquals(19, id)
        assertEquals(19, stub.updatedId)
        assertEquals(3.0, stub.updatedBody?.factor)
        assertNull(stub.createdBody)
    }

    @Test
    fun `creates the conversion when none exists for the pair`() = runBlocking {
        val stub = ConversionsStub(emptyList())
        val repo = ProductRepositoryImpl(genericStub, stub)

        val id = repo.createQuConversion(NewQuConversion(productId = 38, fromQuId = 9, toQuId = 11, factor = 3.0))

        assertEquals(21, id)
        assertEquals(3.0, stub.createdBody?.factor)
        assertNull(stub.updatedId)
    }
}
