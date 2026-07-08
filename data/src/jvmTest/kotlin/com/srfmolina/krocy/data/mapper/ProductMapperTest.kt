package com.srfmolina.krocy.data.mapper

import com.srfmolina.krocy.domain.model.product.NewProduct
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductMapperTest {

    @Test
    fun `maps advanced fields to dto`() {
        val dto = NewProduct(
            name = "Leche",
            quIdStock = 1,
            quIdPurchase = 2,
            locationId = 3,
            defaultBestBeforeDays = 7,
            defaultBestBeforeDaysAfterOpen = 3,
            shouldNotBeFrozen = true,
        ).toCreateDto()

        assertEquals(7, dto.defaultBestBeforeDays)
        assertEquals(3, dto.defaultBestBeforeDaysAfterOpen)
        assertEquals(1, dto.shouldNotBeFrozen)
    }

    @Test
    fun `defaults advanced fields when absent`() {
        val dto = NewProduct(name = "Pan", quIdStock = 1, quIdPurchase = 1, locationId = 3).toCreateDto()

        assertEquals(0, dto.defaultBestBeforeDays)
        assertEquals(0, dto.defaultBestBeforeDaysAfterOpen)
        assertEquals(0, dto.shouldNotBeFrozen)
    }
}
