package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDataSource
import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDto
import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import kotlinx.coroutines.runBlocking
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner
import kotlin.test.Test
import kotlin.test.assertEquals

class MasterRepositoryImplTest {

    private val genericStub = object : GenericEntityDataSource {
        override suspend fun getQuantityUnits() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun getLocations() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun getProductGroups() = Result.success(emptyList<ObjectsEntityGet200ResponseInner>())
        override suspend fun createProduct(body: ObjectsEntityGet200ResponseInner) = Result.success(1)
    }

    @Test
    fun `getQuConversions keeps only global conversions`() = runBlocking {
        val conversionsStub = object : QuConversionDataSource {
            override suspend fun getQuConversions() = Result.success(
                listOf(
                    QuConversionDto(id = 1, productId = null, fromQuId = 2, toQuId = 1, factor = 6.0),
                    QuConversionDto(id = 2, productId = 42, fromQuId = 3, toQuId = 1, factor = 100.0),
                )
            )

            override suspend fun getQuConversionsForProduct(productId: Int) =
                Result.success(emptyList<QuConversionDto>())

            override suspend fun createQuConversion(body: QuConversionDto) = Result.success(1)

            override suspend fun updateQuConversion(id: Int, body: QuConversionDto) = Result.success(Unit)
        }
        val repo = MasterRepositoryImpl(genericStub, conversionsStub)

        val result = repo.getQuConversions()

        assertEquals(1, result.size)
        assertEquals(1, result[0].id)
        assertEquals(null, result[0].productId)
    }
}
