package com.srfmolina.krocy.domain.repository

import com.srfmolina.krocy.domain.model.masterdata.Location
import com.srfmolina.krocy.domain.model.masterdata.ProductGroup
import com.srfmolina.krocy.domain.model.masterdata.QuantityUnit

interface MasterRepository {
    suspend fun getQuantityUnits(): List<QuantityUnit>

    suspend fun getLocations(): List<Location>

    suspend fun getProductGroups(): List<ProductGroup>
}
