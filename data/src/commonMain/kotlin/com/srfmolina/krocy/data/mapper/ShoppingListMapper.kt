package com.srfmolina.krocy.data.mapper

import com.srfmolina.krocy.domain.model.shoppinglist.ShoppingListEntry
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner

/**
 * Maps a `shopping_list` row to the domain, with the joined master data resolved by the
 * caller. Null when the row has no server id or product id: such a row cannot be crossed
 * off or deleted, so it must not render as a normal entry.
 */
internal fun ObjectsEntityGet200ResponseInner.toShoppingListEntry(
    productName: String,
    groupName: String?,
    unitNames: Pair<String, String>,
): ShoppingListEntry? = ShoppingListEntry(
    id = id ?: return null,
    productId = productId ?: return null,
    productName = productName,
    groupName = groupName,
    amount = amount ?: 0.0,
    unitName = unitNames.first,
    unitNamePlural = unitNames.second,
    done = done == 1,
)
