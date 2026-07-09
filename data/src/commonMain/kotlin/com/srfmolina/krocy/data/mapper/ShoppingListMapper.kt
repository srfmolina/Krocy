package com.srfmolina.krocy.data.mapper

import com.srfmolina.krocy.domain.model.shoppinglist.ShoppingListEntry
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner

/** Maps a `shopping_list` row to the domain, with the joined master data resolved by the caller. */
internal fun ObjectsEntityGet200ResponseInner.toShoppingListEntry(
    productName: String,
    groupName: String?,
    unitNames: Pair<String, String>,
): ShoppingListEntry = ShoppingListEntry(
    id = id ?: 0,
    productId = productId ?: 0,
    productName = productName,
    groupName = groupName,
    amount = amount ?: 0.0,
    unitName = unitNames.first,
    unitNamePlural = unitNames.second,
    done = done == 1,
)
