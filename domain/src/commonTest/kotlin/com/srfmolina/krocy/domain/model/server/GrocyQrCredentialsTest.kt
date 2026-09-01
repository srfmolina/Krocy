package com.srfmolina.krocy.domain.model.server

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GrocyQrCredentialsTest {

    @Test
    fun `parses a self hosted payload`() {
        assertEquals(
            GrocyQrCredentials.SelfHosted("http://10.10.10.11:8080", "LwasdaXXXXXXXXXXXXXXX"),
            GrocyQrCredentials.parse("http://10.10.10.11:8080/api|LwasdaXXXXXXXXXXXXXXX")
        )
    }

    @Test
    fun `parses a home assistant ingress payload`() {
        val raw = "http://192.168.1.20:8123/api/hassio_ingress/abc123_igrocy-addon/api|vXNvFkey"
        assertEquals(
            GrocyQrCredentials.HomeAssistant(
                haServerUrl = "http://192.168.1.20:8123",
                ingressProxyId = "abc123_igrocy-addon",
                apiKey = "vXNvFkey"
            ),
            GrocyQrCredentials.parse(raw)
        )
    }

    @Test
    fun `accepts an uppercase scheme and lowercases only the scheme`() {
        assertEquals(
            GrocyQrCredentials.SelfHosted("https://Grocy.Example.com", "key"),
            GrocyQrCredentials.parse("HTTPS://Grocy.Example.com/api|key")
        )
    }

    @Test
    fun `accepts a payload without the api suffix`() {
        assertEquals(
            GrocyQrCredentials.SelfHosted("https://grocy.casa", "key"),
            GrocyQrCredentials.parse("https://grocy.casa|key")
        )
    }

    @Test
    fun `trims surrounding whitespace and trailing slashes`() {
        assertEquals(
            GrocyQrCredentials.SelfHosted("https://grocy.casa", "key"),
            GrocyQrCredentials.parse("  https://grocy.casa/api/  |  key  ")
        )
    }

    @Test
    fun `rejects a payload without a separator`() {
        assertNull(GrocyQrCredentials.parse("https://grocy.casa/api"))
    }

    @Test
    fun `rejects a payload with two separators`() {
        assertNull(GrocyQrCredentials.parse("https://grocy.casa/api|key|extra"))
    }

    @Test
    fun `rejects a blank api key`() {
        assertNull(GrocyQrCredentials.parse("https://grocy.casa/api|   "))
    }

    @Test
    fun `rejects a non http payload`() {
        assertNull(GrocyQrCredentials.parse("grocy.casa/api|key"))
    }

    @Test
    fun `rejects a scheme with no host`() {
        assertNull(GrocyQrCredentials.parse("http:///api|key"))
    }

    @Test
    fun `rejects an authority that is only a port`() {
        assertNull(GrocyQrCredentials.parse("http://:8080/api|key"))
    }

    @Test
    fun `rejects an ingress payload with an empty proxy id`() {
        assertNull(GrocyQrCredentials.parse("http://ha.local:8123/api/hassio_ingress//api|key"))
    }

    @Test
    fun `rejects an ingress payload whose proxy id carries extra path segments`() {
        assertNull(
            GrocyQrCredentials.parse("http://ha.local:8123/api/hassio_ingress/proxy/extra/api|key")
        )
    }

    @Test
    fun `rejects an arbitrary non grocy qr`() {
        assertNull(GrocyQrCredentials.parse("WIFI:S:MiRed;T:WPA;P:secreto;;"))
    }

    // --- Untrusted-input hardening. A QR is attacker-controllable: a sticker, a printed
    // sheet, an image on a page. Each case below is a way to make the request we finally
    // send differ from what the user believes they scanned. ---

    @Test
    fun `rejects an api key carrying a header injection`() {
        assertNull(GrocyQrCredentials.parse("https://grocy.casa/api|key\r\nX-Evil: 1"))
        assertNull(GrocyQrCredentials.parse("https://grocy.casa/api|key\nX-Evil: 1"))
    }

    @Test
    fun `rejects a url carrying a control character`() {
        assertNull(GrocyQrCredentials.parse("https://grocy.casa\u0000/api|key"))
    }

    @Test
    fun `rejects userinfo that hides the real host`() {
        assertNull(GrocyQrCredentials.parse("http://grocy.midominio.com@evil.host/api|key"))
    }

    @Test
    fun `rejects a url with a query or a fragment`() {
        assertNull(GrocyQrCredentials.parse("https://grocy.casa/api?next=evil|key"))
        assertNull(GrocyQrCredentials.parse("https://grocy.casa/api#evil|key"))
    }

    @Test
    fun `rejects a url with a backslash`() {
        assertNull(GrocyQrCredentials.parse("https://grocy.casa\\@evil.host/api|key"))
    }

    @Test
    fun `rejects dot segments in the path`() {
        assertNull(
            GrocyQrCredentials.parse("http://ha.local:8123/api/hassio_ingress/../evil/api|key")
        )
    }

    @Test
    fun `rejects a proxy id that could steer the request path`() {
        assertNull(GrocyQrCredentials.parse("http://ha.local:8123/api/hassio_ingress/a%2fb/api|k"))
        assertNull(GrocyQrCredentials.parse("http://ha.local:8123/api/hassio_ingress/a b/api|k"))
    }

    @Test
    fun `rejects a non ascii homograph host`() {
        assertNull(GrocyQrCredentials.parse("https://grocy.c\u0430sa/api|key")) // Cyrillic a
    }

    @Test
    fun `rejects an oversized payload`() {
        assertNull(GrocyQrCredentials.parse("https://grocy.casa/api|" + "k".repeat(4096)))
    }

    @Test
    fun `rejects an oversized api key inside a short payload`() {
        assertNull(GrocyQrCredentials.parse("https://grocy.casa/api|" + "k".repeat(513)))
    }

    @Test
    fun `rejects an oversized url`() {
        assertNull(GrocyQrCredentials.parse("https://grocy.casa/" + "a".repeat(300) + "/api|key"))
    }

    @Test
    fun `rejects a percent encoded userinfo separator in the authority`() {
        assertNull(GrocyQrCredentials.parse("http://grocy.midominio.com%40evil.host/api|k"))
    }

    @Test
    fun `accepts an ipv6 literal authority`() {
        assertEquals(
            GrocyQrCredentials.SelfHosted("http://[::1]:8080", "key"),
            GrocyQrCredentials.parse("http://[::1]:8080/api|key")
        )
    }
}
