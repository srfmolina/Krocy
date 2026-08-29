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
    if (host == "localhost" || host == "::1" || host == "[::1]") return true
    if (host.endsWith(".local") || host.endsWith(".lan") || host.endsWith(".home.arpa")) return true

    // Only a genuine dotted-quad IPv4 literal can be a private address. Checking string
    // prefixes instead would read "10.attacker.com" as a LAN host and silently drop the
    // cleartext warning for a public server.
    val octets = host.split('.')
    if (octets.size != 4) return false
    val numbers = octets.map { octet -> octet.toIntOrNull() ?: return false }
    if (numbers.any { it !in 0..255 }) return false

    return when {
        numbers[0] == 127 -> true                          // loopback
        numbers[0] == 10 -> true                           // 10.0.0.0/8
        numbers[0] == 192 && numbers[1] == 168 -> true     // 192.168.0.0/16
        numbers[0] == 172 && numbers[1] in 16..31 -> true  // 172.16.0.0/12
        numbers[0] == 169 && numbers[1] == 254 -> true     // link-local
        else -> false
    }
}
