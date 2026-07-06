package com.srfmolina.krocy.data.datasource.remote.conversion

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class QuConversionDtoTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Test
    fun `parses grocy response including string numbers`() {
        // Grocy serialises numbers as strings; isLenient must cope with that.
        val parsed = json.decodeFromString<List<QuConversionDto>>(
            """[{"id":"5","product_id":null,"from_qu_id":"2","to_qu_id":"1","factor":"6.0"}]"""
        )

        assertEquals(1, parsed.size)
        assertEquals(5, parsed[0].id)
        assertNull(parsed[0].productId)
        assertEquals(2, parsed[0].fromQuId)
        assertEquals(1, parsed[0].toQuId)
        assertEquals(6.0, parsed[0].factor)
    }

    @Test
    fun `serialises create body with snake_case names`() {
        val body = json.encodeToString(
            QuConversionDto.serializer(),
            QuConversionDto(productId = 9, fromQuId = 2, toQuId = 1, factor = 6.0),
        )

        assertEquals("""{"product_id":9,"from_qu_id":2,"to_qu_id":1,"factor":6.0}""", body)
    }
}
