package com.srfmolina.krocy.data.config

/** Persisted Home Assistant ingress session key (a cache, not user-entered config). */
internal interface HassSessionStore {
    suspend fun session(): String?
    suspend fun save(session: String)
    suspend fun clear()
}
