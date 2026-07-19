package com.srfmolina.krocy.data.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Messages of the Home Assistant WebSocket handshake used to obtain an ingress
 * session key (mirrors grocy-android's flow):
 * auth_required -> auth(token) -> auth_ok -> supervisor/api POST /ingress/session -> result.
 */
internal object HassMessages {

    sealed interface Incoming {
        data object AuthRequired : Incoming
        data object AuthOk : Incoming
        data object AuthInvalid : Incoming
        data class SessionResult(val session: String?, val success: Boolean) : Incoming
        data class Other(val raw: String) : Incoming
    }

    fun auth(token: String): String = buildJsonObject {
        put("type", "auth")
        put("access_token", token)
    }.toString()

    fun sessionRequest(id: Int): String = buildJsonObject {
        put("type", "supervisor/api")
        put("endpoint", "/ingress/session")
        put("method", "post")
        put("id", id)
    }.toString()

    fun parse(text: String): Incoming {
        val json: JsonObject = runCatching {
            Json.parseToJsonElement(text).jsonObject
        }.getOrElse { return Incoming.Other(text) }
        return when (json["type"]?.jsonPrimitive?.content) {
            "auth_required" -> Incoming.AuthRequired
            "auth_ok" -> Incoming.AuthOk
            "auth_invalid" -> Incoming.AuthInvalid
            "result" -> Incoming.SessionResult(
                session = runCatching {
                    json["result"]?.jsonObject?.get("session")?.jsonPrimitive?.content
                }.getOrNull(),
                success = json["success"]?.jsonPrimitive?.boolean ?: false
            )
            else -> Incoming.Other(text)
        }
    }
}
