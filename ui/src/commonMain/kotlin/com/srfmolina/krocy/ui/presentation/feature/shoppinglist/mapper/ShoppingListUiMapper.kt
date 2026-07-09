package com.srfmolina.krocy.ui.presentation.feature.shoppinglist.mapper

import com.srfmolina.krocy.domain.model.shoppinglist.ShoppingListEntry
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.model.ShoppingGroupUi
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.model.ShoppingListEntryUi
import kotlin.math.floor

internal fun List<ShoppingListEntry>.toUi(): List<ShoppingGroupUi> =
    groupBy { it.groupName }
        .map { (groupName, entries) ->
            ShoppingGroupUi(name = groupName, entries = entries.map { it.toUi() })
        }
        // Alphabetical, with the ungrouped section always last.
        .sortedWith(compareBy({ it.name == null }, { it.displayName.lowercase() }))

internal fun ShoppingListEntry.toUi(): ShoppingListEntryUi = ShoppingListEntryUi(
    id = id,
    name = productName,
    quantity = "${amount.toAmountText()} ${if (amount == 1.0) unitName else unitNamePlural}",
    done = done,
)

/** Flips one entry's done flag, e.g. for an optimistic cross-off before the API confirms. */
internal fun List<ShoppingGroupUi>.withEntryDone(entryId: Int, done: Boolean): List<ShoppingGroupUi> =
    map { group ->
        group.copy(
            entries = group.entries.map { entry ->
                if (entry.id == entryId) entry.copy(done = done) else entry
            }
        )
    }

private fun Double.toAmountText(): String =
    if (this == floor(this)) toInt().toString() else toString()
