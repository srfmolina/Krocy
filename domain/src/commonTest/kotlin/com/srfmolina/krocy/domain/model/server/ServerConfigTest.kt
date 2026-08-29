package com.srfmolina.krocy.domain.model.server

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ServerConfigTest {

    @Test
    fun `demo config points at the public demo server without api key`() {
        assertEquals("https://en.demo.grocy.info/api", ServerConfig.Demo.apiBaseUrl)
        assertNull(ServerConfig.Demo.apiKey)
    }

    @Test
    fun `self hosted derives api url and trims trailing slashes`() {
        val config = ServerConfig.SelfHosted(serverUrl = "https://grocy.casa/", apiKey = "abc")
        assertEquals("https://grocy.casa", config.grocyBaseUrl)
        assertEquals("https://grocy.casa/api", config.apiBaseUrl)
    }

    @Test
    fun `home assistant derives ingress url`() {
        val config = ServerConfig.HomeAssistant(
            haServerUrl = "http://homeassistant.local:8123/",
            ingressProxyId = "s65bor48v40w3r0m8v",
            longLivedToken = "token",
            apiKey = "abc"
        )
        assertEquals(
            "http://homeassistant.local:8123/api/hassio_ingress/s65bor48v40w3r0m8v",
            config.grocyBaseUrl
        )
        assertEquals(
            "http://homeassistant.local:8123/api/hassio_ingress/s65bor48v40w3r0m8v/api",
            config.apiBaseUrl
        )
    }

    @Test
    fun `validation supports grocy 4`() {
        assertTrue(ServerValidation.forVersion("4.5.0").isSupported)
        assertFalse(ServerValidation.forVersion("3.3.2").isSupported)
        assertFalse(ServerValidation.forVersion("garbage").isSupported)
        assertEquals("4.5.0", ServerValidation.forVersion("4.5.0").grocyVersion)
    }
}
