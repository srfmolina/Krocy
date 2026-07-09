package com.srfmolina.krocy.ui.presentation.feature.shoppinglist.model

/** A product group section of the shopping list. [name] is null for ungrouped products. */
internal data class ShoppingGroupUi(
    val name: String?,
    val entries: List<ShoppingListEntryUi>,
) {
    val displayName: String get() = name ?: UNGROUPED_DISPLAY_NAME

    val doneCount: Int get() = entries.count { it.done }

    val allDone: Boolean get() = entries.isNotEmpty() && doneCount == entries.size

    companion object {
        const val UNGROUPED_DISPLAY_NAME = "Otros"
    }
}
