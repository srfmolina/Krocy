package com.srfmolina.krocy.data.datasource.remote.generic

import kotlinx.serialization.json.jsonObject
import org.openapitools.client.infrastructure.ApiClient
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner
import org.openapitools.client.models.ProductDetailsResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class GenericEntityDeserializationTest {

    @Test
    fun `parses products response with object-valued userfields`() {
        // Grocy sends userfields as a JSON object when userfields are defined for the entity
        // (e.g. GET /objects/products with a "shop_sort" userfield configured).
        val parsed = ApiClient.JSON_DEFAULT.decodeFromString<List<ObjectsEntityGet200ResponseInner>>(
            """[{"id":1,"name":"Cookies","description":null,"product_group_id":1,"active":1,"location_id":4,""" +
                """"shopping_location_id":null,"qu_id_purchase":3,"qu_id_stock":3,"min_stock_amount":8,""" +
                """"default_best_before_days":0,"default_best_before_days_after_open":0,""" +
                """"picture_file_name":"cookies.jpg","enable_tare_weight_handling":0,"tare_weight":0,""" +
                """"not_check_stock_fulfillment_for_recipes":0,"parent_product_id":null,"calories":123,""" +
                """"due_type":1,"quick_consume_amount":1,"row_created_timestamp":"2026-07-09 00:50:06",""" +
                """"userfields":{"shop_sort":null}}]"""
        )

        assertEquals(1, parsed.size)
        assertEquals(1, parsed.first().id)
        assertEquals("Cookies", parsed.first().name)
    }

    @Test
    fun `parses product details response where has_childs is a real boolean`() {
        // The client maps spec booleans to Int because grocy sends most of them as 0/1,
        // but computed fields like has_childs on GET /stock/products/{id} arrive as true/false.
        val parsed = ApiClient.JSON_DEFAULT.decodeFromString<ProductDetailsResponse>(
            """{"product":{"id":1,"name":"Cookies","location_id":4,"qu_id_purchase":3,"qu_id_stock":3,""" +
                """"default_best_before_days":0},"product_barcodes":[],"last_purchased":"2026-07-09",""" +
                """"stock_amount":2,"quantity_unit_stock":{"id":3,"name":"Pack","name_plural":"Packs","active":1},""" +
                """"default_quantity_unit_purchase":{"id":3,"name":"Pack","name_plural":"Packs","active":1},""" +
                """"last_price":5.41,"last_shopping_location_id":1,"next_due_date":"2999-12-31",""" +
                """"average_shelf_life_days":177868.5,"spoil_rate_percent":0,"is_aggregated_amount":0,""" +
                """"has_childs":false,"qu_conversion_factor_purchase_to_stock":1}"""
        )

        assertEquals(1, parsed.product?.id)
        assertEquals("Pack", parsed.quantityUnitStock?.name)
        assertEquals(1.0, parsed.quConversionFactorPurchaseToStock)
        assertEquals(5.41, parsed.lastPrice)
    }

    @Test
    fun `parses product details response where has_childs is a 0-or-1 int`() {
        // Grocy is inconsistent: other installations/fields send booleans as 0/1 ints.
        val parsed = ApiClient.JSON_DEFAULT.decodeFromString<ProductDetailsResponse>(
            """{"product":{"id":2,"name":"Milk"},"has_childs":1}"""
        )

        assertEquals(2, parsed.product?.id)
    }

    @Test
    fun `parses shopping list rows with done and qu_id`() {
        // Real payload shape from GET /objects/shopping_list (demo server, 2026-07-09):
        // the spec omits done and qu_id, added manually here (see SPEC-DEVIATIONS.md #7).
        val parsed = ApiClient.JSON_DEFAULT.decodeFromString<List<ObjectsEntityGet200ResponseInner>>(
            """[{"id":10,"product_id":1,"note":null,"amount":5,""" +
                """"row_created_timestamp":"2026-07-09 19:09:07","shopping_list_id":2,"done":0,"qu_id":3},""" +
                """{"id":11,"product_id":2,"note":null,"amount":8,""" +
                """"row_created_timestamp":"2026-07-09 19:09:07","shopping_list_id":2,"done":1,"qu_id":3,""" +
                """"userfields":null}]"""
        )

        assertEquals(0, parsed[0].done)
        assertEquals(1, parsed[1].done)
        assertEquals(3, parsed[0].quId)
        assertEquals(2, parsed[0].shoppingListId)
        assertEquals(5.0, parsed[0].amount)
    }

    @Test
    fun `encodes a done-only body for the shopping list PUT`() {
        // Crossing an item off PUTs a partial body; with encodeDefaults off only the set field
        // is serialized, so grocy updates just the done column (see SPEC-DEVIATIONS.md #7).
        val body = ApiClient.JSON_DEFAULT.encodeToString(ObjectsEntityGet200ResponseInner(done = 1))

        val keys = ApiClient.JSON_DEFAULT.parseToJsonElement(body).jsonObject.keys
        assertEquals(setOf("done"), keys)
    }

    @Test
    fun `parses entities response where userfields is a plain string or null`() {
        // Other entities (locations, quantity_units, product_groups) omit userfields entirely,
        // and some payloads may still send it as a plain string; both shapes must decode.
        val withString = ApiClient.JSON_DEFAULT.decodeFromString<List<ObjectsEntityGet200ResponseInner>>(
            """[{"id":2,"name":"Milk","userfields":"x"}]"""
        )
        val withNull = ApiClient.JSON_DEFAULT.decodeFromString<List<ObjectsEntityGet200ResponseInner>>(
            """[{"id":3,"name":"Bread","userfields":null}]"""
        )

        assertEquals(2, withString.first().id)
        assertEquals("Milk", withString.first().name)
        assertEquals(3, withNull.first().id)
        assertEquals("Bread", withNull.first().name)
    }
}
