package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.conversion.QuConversionDataSource
import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.mapper.toLocation
import com.srfmolina.krocy.data.mapper.toProductGroup
import com.srfmolina.krocy.data.mapper.toQuConversion
import com.srfmolina.krocy.data.mapper.toQuantityUnit
import com.srfmolina.krocy.data.mapper.toShoppingLocation
import com.srfmolina.krocy.domain.model.masterdata.Location
import com.srfmolina.krocy.domain.model.masterdata.ProductGroup
import com.srfmolina.krocy.domain.model.masterdata.QuConversion
import com.srfmolina.krocy.domain.model.masterdata.QuantityUnit
import com.srfmolina.krocy.domain.model.masterdata.ShoppingLocation
import com.srfmolina.krocy.domain.repository.MasterRepository

internal class MasterRepositoryImpl(
    private val genericEntityDataSource: GenericEntityDataSource,
    private val quConversionDataSource: QuConversionDataSource,
) : MasterRepository {

    override suspend fun getQuantityUnits(): List<QuantityUnit> =
        genericEntityDataSource.getQuantityUnits().getOrThrow().map { it.toQuantityUnit() }

    override suspend fun getLocations(): List<Location> =
        genericEntityDataSource.getLocations().getOrThrow().map { it.toLocation() }

    override suspend fun getProductGroups(): List<ProductGroup> =
        genericEntityDataSource.getProductGroups().getOrThrow().map { it.toProductGroup() }

    override suspend fun getQuConversions(): List<QuConversion> =
        quConversionDataSource.getQuConversions().getOrThrow()
            .map { it.toQuConversion() }
            .filter { it.productId == null }

    override suspend fun getShoppingLocations(): List<ShoppingLocation> =
        genericEntityDataSource.getShoppingLocations().getOrThrow().map { it.toShoppingLocation() }
}
