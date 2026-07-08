package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDataSource
import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import kotlinx.coroutines.runBlocking
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner
import org.openapitools.client.models.QuantityUnitConversion
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
                    QuantityUnitConversion(id = 1, productId = null, fromQuId = 2, toQuId = 1, factor = 6.0),
                    QuantityUnitConversion(id = 2, productId = 42, fromQuId = 3, toQuId = 1, factor = 100.0),
                )
            )

            override suspend fun getQuConversionsForProduct(productId: Int) =
                Result.success(emptyList<QuantityUnitConversion>())

            override suspend fun createQuConversion(body: QuantityUnitConversion) = Result.success(1)

            override suspend fun updateQuConversion(id: Int, body: QuantityUnitConversion) = Result.success(Unit)
        }
        val repo = MasterRepositoryImpl(genericStub, conversionsStub)

        val result = repo.getQuConversions()

        assertEquals(1, result.size)
        assertEquals(1, result[0].id)
        assertEquals(null, result[0].productId)
    }
}
