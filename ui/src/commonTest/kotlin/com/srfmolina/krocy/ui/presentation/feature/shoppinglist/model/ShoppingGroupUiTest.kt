package com.srfmolina.krocy.ui.presentation.feature.shoppinglist.model

import kotlin.test.Test
import kotlin.test.assertNotEquals

class ShoppingGroupUiTest {

    @Test
    fun `a group named Otros and the ungrouped section have distinct lazy-list keys`() {
        val named = ShoppingGroupUi(name = "Otros", entries = emptyList())
        val ungrouped = ShoppingGroupUi(name = null, entries = emptyList())

        // Both display as "Otros", but lazy-list keys must be unique or composition throws.
        assertNotEquals(named.key, ungrouped.key)
    }
}
