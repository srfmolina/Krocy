package com.srfmolina.krocy.ui.presentation.feature.creation

import com.srfmolina.krocy.ui.presentation.common.model.OptionsUi
import com.srfmolina.krocy.ui.presentation.common.model.QuConversionUi
import com.srfmolina.krocy.ui.presentation.common.model.SelectableOptionUi
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.State
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateProductStateTest {

    private val units = OptionsUi(
        options = listOf(SelectableOptionUi(1, "Unidad"), SelectableOptionUi(2, "Paquete")),
        isLoading = false,
    )

    private fun baseState() = State(
        quantityUnits = units,
        locations = OptionsUi(isLoading = false),
        productGroups = OptionsUi(isLoading = false),
        conversionsLoading = false,
        name = "Leche",
        stockUnitId = 1,
        purchaseUnitId = 1,
        locationId = 10,
    )

    @Test
    fun `same units need no conversion`() {
        val state = baseState()
        assertFalse(state.unitsDiffer)
        assertFalse(state.needsConversionFactor)
        assertTrue(state.isValid)
    }

    @Test
    fun `direct global conversion is found`() {
        val state = baseState().copy(
            purchaseUnitId = 2,
            conversions = listOf(QuConversionUi(fromQuId = 2, toQuId = 1, factor = 6.0)),
        )
        assertTrue(state.unitsDiffer)
        assertEquals(6.0, state.existingConversionFactor)
        assertFalse(state.needsConversionFactor)
        assertTrue(state.isValid)
    }

    @Test
    fun `reverse global conversion is inverted`() {
        val state = baseState().copy(
            purchaseUnitId = 2,
            conversions = listOf(QuConversionUi(fromQuId = 1, toQuId = 2, factor = 4.0)),
        )
        assertEquals(0.25, state.existingConversionFactor)
        assertFalse(state.needsConversionFactor)
    }

    @Test
    fun `missing conversion requires a positive factor`() {
        val state = baseState().copy(purchaseUnitId = 2)
        assertNull(state.existingConversionFactor)
        assertTrue(state.needsConversionFactor)
        assertFalse(state.isValid)
        assertTrue("Factor de conversión" in state.missingFields)

        assertFalse(state.copy(conversionFactor = "0").isValid)
        assertFalse(state.copy(conversionFactor = "abc").isValid)
        assertTrue(state.copy(conversionFactor = "6").isValid)
    }

    @Test
    fun `missing fields lists blank required inputs`() {
        val state = baseState().copy(name = "", locationId = null)
        assertEquals(listOf("Nombre", "Ubicación"), state.missingFields)
    }

    @Test
    fun `best before days must be blank or non-negative integers`() {
        assertTrue(baseState().copy(defaultBestBeforeDays = "7").isValid)
        assertTrue(baseState().copy(defaultBestBeforeDays = "").isValid)
        assertFalse(baseState().copy(defaultBestBeforeDays = "-1").isValid)
        assertFalse(baseState().copy(defaultBestBeforeDaysAfterOpen = "x").isValid)
    }

    @Test
    fun `unit names resolve from loaded options`() {
        val state = baseState().copy(purchaseUnitId = 2)
        assertEquals("Unidad", state.stockUnitName)
        assertEquals("Paquete", state.purchaseUnitName)
    }
}
