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
    val authority = url.substringAfter("://").substringBefore('/')
    return !isPrivateOrLocalHost(hostOf(authority).lowercase())
}

/**
 * Drops the port from an authority. An IPv6 literal carries colons inside its brackets, so its
 * port is whatever follows the closing bracket - cutting at the first colon would leave a bare
 * "[" and read every IPv6 host, loopback included, as a public one.
 */
private fun hostOf(authority: String): String {
    if (!authority.startsWith("[")) return authority.substringBefore(':')
    val close = authority.indexOf(']')
    // An unclosed bracket is not a literal we can vouch for; leave it to be flagged.
    return if (close == -1) authority else authority.substring(0, close + 1)
}

private fun isPrivateOrLocalHost(host: String): Boolean {
    if (host == "localhost" || host == "::1") return true
    if (host.endsWith(".local") || host.endsWith(".lan") || host.endsWith(".home.arpa")) return true
    if (host.startsWith("[") && host.endsWith("]")) {
        return isPrivateIpv6(host.substring(1, host.length - 1))
    }

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

/**
 * The IPv6 counterpart of the private IPv4 blocks above: loopback, unique local (fc00::/7) and
 * link local (fe80::/10) are the ranges that can only mean the user's own network. Anything we
 * cannot parse as one of those is treated as public, so a malformed literal warns rather than
 * silently dropping the warning.
 */
private fun isPrivateIpv6(address: String): Boolean {
    val bare = address.substringBefore('%') // a link-local address may carry a zone id
    if (bare == "::1") return true
    val firstGroup = bare.substringBefore(':').toIntOrNull(16) ?: return false
    return firstGroup in 0xfc00..0xfdff || firstGroup in 0xfe80..0xfebf
}
