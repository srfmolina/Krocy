package com.srfmolina.krocy.data.mapper

import org.openapitools.client.models.ObjectsEntityGet200ResponseInner
import org.openapitools.client.models.Product
import org.openapitools.client.models.ProductDetailsResponse
import org.openapitools.client.models.QuantityUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PurchaseMapperTest {

    @Test
    fun `maps entity rows to product options and shopping locations`() {
        val row = ObjectsEntityGet200ResponseInner(id = 7, name = "Leche")

        assertEquals(7, row.toProductOption().id)
        assertEquals("Leche", row.toProductOption().name)
        assertEquals(7, row.toShoppingLocation().id)
        assertEquals("Leche", row.toShoppingLocation().name)
    }

    @Test
    fun `maps product details to purchase info`() {
        val details = ProductDetailsResponse(
            product = Product(id = 7, name = "Leche", locationId = 11, defaultBestBeforeDays = 7),
            quantityUnitStock = QuantityUnit(id = 1, name = "Unidad", namePlural = "Unidades"),
            defaultQuantityUnitPurchase = QuantityUnit(id = 2, name = "Paquete", namePlural = "Paquetes"),
            quConversionFactorPurchaseToStock = 6.0,
            lastPrice = 0.5,
            lastShoppingLocationId = 5,
        )

        val info = details.toProductPurchaseInfo()

        assertEquals(7, info.productId)
        assertEquals("Paquete", info.purchaseUnitName)
        assertEquals("Paquetes", info.purchaseUnitNamePlural)
        assertEquals("Unidad", info.stockUnitName)
        assertEquals("Unidades", info.stockUnitNamePlural)
        assertEquals(6.0, info.conversionFactorPurchaseToStock)
        assertEquals(7, info.defaultBestBeforeDays)
        assertEquals(11, info.defaultLocationId)
        assertEquals(0.5, info.lastPrice)
        assertEquals(5, info.lastShoppingLocationId)
    }

    @Test
    fun `missing factor defaults to 1 and blank plurals fall back to the singular`() {
        val details = ProductDetailsResponse(
            product = Product(id = 7, name = "Leche"),
            quantityUnitStock = QuantityUnit(id = 1, name = "Unidad", namePlural = ""),
            defaultQuantityUnitPurchase = QuantityUnit(id = 1, name = "Unidad"),
        )

        val info = details.toProductPurchaseInfo()

        assertEquals(1.0, info.conversionFactorPurchaseToStock)
        assertEquals("Unidad", info.purchaseUnitNamePlural)
        assertEquals("Unidad", info.stockUnitNamePlural)
        assertNull(info.lastPrice)
        assertNull(info.lastShoppingLocationId)
    }
}
