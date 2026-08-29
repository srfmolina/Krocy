package com.srfmolina.krocy.domain.model.server

/**
 * URI schemes are case-insensitive (RFC 3986), so "HTTPS://" is as valid as "https://" -
 * pasted URLs and mobile autocapitalization both produce mixed-case schemes.
 */
fun hasHttpScheme(url: String): Boolean =
    url.startsWith("http://", ignoreCase = true) || url.startsWith("https://", ignoreCase = true)

/**
 * Lowercases the scheme and nothing else: host and path may be legitimately case-sensitive,
 * and the normalized form is what gets persisted and compared.
 */
fun normalizeUrlScheme(url: String): String = when {
    url.startsWith("https://", ignoreCase = true) -> "https://" + url.substring("https://".length)
    url.startsWith("http://", ignoreCase = true) -> "http://" + url.substring("http://".length)
    else -> url
}

/**
 * True when [url] would send credentials in the clear across a network that is not the user's
 * own. Plain http on the LAN is a core Krocy use case (bare Grocy boxes, the Home Assistant
 * add-on on `homeassistant.local`) and is not flagged; plain http to a public host means the
 * API key and any Home Assistant token cross the internet readable by anyone on the path.
 *
 * This is the rule Android's network security config cannot express, which is why the app
 * permits cleartext globally and the judgement is made here instead.
 */
fun isCleartextRisk(url: String): Boolean {
    if (!url.startsWith("http://", ignoreCase = true)) return false
    val host = url.substringAfter("://").substringBefore('/').substringBefore(':').lowercase()
    return !isPrivateOrLocalHost(host)
}

private fun isPrivateOrLocalHost(host: String): Boolean {
    if (host == "localhost" || host == "127.0.0.1" || host == "[::1]" || host == "::1") return true
    if (host.endsWith(".local") || host.endsWith(".lan") || host.endsWith(".home.arpa")) return true
    if (host.startsWith("10.") || host.startsWith("192.168.")) return true
    val octets = host.split('.')
    if (octets.size == 4 && octets[0] == "172") {
        val second = octets[1].toIntOrNull()
        if (second != null && second in 16..31) return true // 172.16.0.0/12
    }
    return false
}
