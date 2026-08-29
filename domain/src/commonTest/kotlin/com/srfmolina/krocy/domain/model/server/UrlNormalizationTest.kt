package com.srfmolina.krocy.domain.model.server

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UrlNormalizationTest {

    @Test
    fun `https is never a cleartext risk`() {
        assertFalse(isCleartextRisk("https://grocy.midominio.com"))
    }

    @Test
    fun `plain http on the lan is not flagged`() {
        assertFalse(isCleartextRisk("http://192.168.1.20:8123"))
        assertFalse(isCleartextRisk("http://10.10.10.11:8080"))
        assertFalse(isCleartextRisk("http://172.16.0.5"))
        assertFalse(isCleartextRisk("http://172.31.255.1"))
        assertFalse(isCleartextRisk("http://homeassistant.local:8123"))
        assertFalse(isCleartextRisk("http://localhost:8080"))
    }

    @Test
    fun `plain http to a public host is flagged`() {
        assertTrue(isCleartextRisk("http://grocy.midominio.com"))
        assertTrue(isCleartextRisk("http://203.0.113.10:8080"))
        assertTrue(isCleartextRisk("http://172.32.0.1")) // just outside 172.16.0.0/12
        assertTrue(isCleartextRisk("http://100.10.10.11")) // not the 10.0.0.0/8 block
    }
}
