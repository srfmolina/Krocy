package com.srfmolina.krocy.data.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class HassMessagesTest {

    @Test
    fun `auth message carries the token`() {
        val json = Json.parseToJsonElement(HassMessages.auth("my-token")).jsonObject
        assertEquals("auth", json["type"]?.jsonPrimitive?.content)
        assertEquals("my-token", json["access_token"]?.jsonPrimitive?.content)
    }

    @Test
    fun `session request targets the supervisor ingress endpoint`() {
        val json = Json.parseToJsonElement(HassMessages.sessionRequest(7)).jsonObject
        assertEquals("supervisor/api", json["type"]?.jsonPrimitive?.content)
        assertEquals("/ingress/session", json["endpoint"]?.jsonPrimitive?.content)
        assertEquals("post", json["method"]?.jsonPrimitive?.content)
        assertEquals("7", json["id"]?.jsonPrimitive?.content)
    }

    @Test
    fun `parses the handshake states`() {
        assertIs<HassMessages.Incoming.AuthRequired>(
            HassMessages.parse("""{"type":"auth_required","ha_version":"2026.1"}""")
        )
        assertIs<HassMessages.Incoming.AuthOk>(
            HassMessages.parse("""{"type":"auth_ok","ha_version":"2026.1"}""")
        )
        assertIs<HassMessages.Incoming.AuthInvalid>(
            HassMessages.parse("""{"type":"auth_invalid","message":"Invalid access token"}""")
        )
    }

    @Test
    fun `parses a successful session result`() {
        val result = HassMessages.parse(
            """{"id":1,"type":"result","success":true,"result":{"session":"abc123"}}"""
        )
        assertIs<HassMessages.Incoming.SessionResult>(result)
        assertEquals("abc123", result.session)
        assertEquals(true, result.success)
    }

    @Test
    fun `parses a failed result and unknown messages`() {
        val failed = HassMessages.parse("""{"id":1,"type":"result","success":false}""")
        assertIs<HassMessages.Incoming.SessionResult>(failed)
        assertEquals(null, failed.session)
        assertEquals(false, failed.success)
        assertIs<HassMessages.Incoming.Other>(HassMessages.parse("""{"type":"pong"}"""))
        assertIs<HassMessages.Incoming.Other>(HassMessages.parse("not json at all"))
    }
}
