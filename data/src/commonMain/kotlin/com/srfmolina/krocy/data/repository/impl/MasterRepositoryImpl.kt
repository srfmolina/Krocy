package com.srfmolina.krocy.data.repository.impl

import com.srfmolina.krocy.data.datasource.remote.generic.GenericEntityDataSource
import com.srfmolina.krocy.data.mapper.toLocation
import com.srfmolina.krocy.data.mapper.toProductGroup
import com.srfmolina.krocy.data.mapper.toQuantityUnit
import com.srfmolina.krocy.domain.model.masterdata.Location
import com.srfmolina.krocy.domain.model.masterdata.ProductGroup
import com.srfmolina.krocy.domain.model.masterdata.QuantityUnit
import com.srfmolina.krocy.domain.repository.MasterRepository

internal class MasterRepositoryImpl(
    private val genericEntityDataSource: GenericEntityDataSource
) : MasterRepository {

    override suspend fun getQuantityUnits(): List<QuantityUnit> =
        genericEntityDataSource.getQuantityUnits().getOrThrow().map { it.toQuantityUnit() }

    override suspend fun getLocations(): List<Location> =
        genericEntityDataSource.getLocations().getOrThrow().map { it.toLocation() }

    override suspend fun getProductGroups(): List<ProductGroup> =
        genericEntityDataSource.getProductGroups().getOrThrow().map { it.toProductGroup() }
}
