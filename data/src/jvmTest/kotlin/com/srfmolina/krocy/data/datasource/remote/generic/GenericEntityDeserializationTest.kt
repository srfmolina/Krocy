package com.srfmolina.krocy.data.datasource.remote.generic

import org.openapitools.client.infrastructure.ApiClient
import org.openapitools.client.models.ObjectsEntityGet200ResponseInner
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
