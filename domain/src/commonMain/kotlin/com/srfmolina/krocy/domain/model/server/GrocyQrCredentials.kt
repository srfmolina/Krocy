package com.srfmolina.krocy.domain.model.server

private const val PAYLOAD_SEPARATOR = "|"
private const val API_SUFFIX = "/api"
private const val INGRESS_MARKER = "/api/hassio_ingress/"

/** A QR carries kilobytes; nothing legitimate here is long. */
private const val MAX_PAYLOAD_LENGTH = 2048
private const val MAX_API_KEY_LENGTH = 512

/**
 * Printable ASCII, no space. Excludes CR, LF, tab and every control character - the API key is
 * sent verbatim as the `GROCY-API-KEY` header, where a CR or LF is header injection - and
 * excludes non-ASCII, which in a host name means a homograph rather than a real server.
 */
private val SAFE_TEXT = Regex("[!-~]+")

/** The proxy id is interpolated into a request path; this charset is all a real one uses. */
private val SAFE_PROXY_ID = Regex("[A-Za-z0-9_.-]+")

/**
 * What a Grocy QR code carries: `<apiBaseUrl>|<apiKey>`.
 *
 * The Home Assistant variant is missing [ServerConfig.HomeAssistant.longLivedToken] on purpose -
 * that token is minted in Home Assistant and can never appear in a Grocy QR, so the user still
 * types it. This type is therefore *not* a ServerConfig; it is only what the QR knew.
 *
 * ## Trust
 *
 * A scanned payload is **untrusted input from an attacker-controllable source** - a sticker, a
 * printed sheet, an image on a web page. Unlike a typed URL, the user did not choose this host.
 * Every field is therefore validated here, at the boundary, rather than anywhere downstream:
 * the parser is the only place that decides a QR is acceptable.
 */
sealed interface GrocyQrCredentials {

    val apiKey: String

    data class SelfHosted(
        val serverUrl: String,
        override val apiKey: String
    ) : GrocyQrCredentials

    data class HomeAssistant(
        val haServerUrl: String,
        val ingressProxyId: String,
        override val apiKey: String
    ) : GrocyQrCredentials

    companion object {

        /** Returns null for anything that is not a well-formed, safe Grocy QR payload. */
        fun parse(raw: String): GrocyQrCredentials? {
            if (raw.length > MAX_PAYLOAD_LENGTH) return null

            val parts = raw.trim().split(PAYLOAD_SEPARATOR)
            if (parts.size != 2) return null

            val apiKey = parts[1].trim()
            if (apiKey.isEmpty() || apiKey.length > MAX_API_KEY_LENGTH) return null
            if (!SAFE_TEXT.matches(apiKey)) return null

            val rawUrl = parts[0].trim()
            if (!SAFE_TEXT.matches(rawUrl) || !hasHttpScheme(rawUrl)) return null
            // A query, fragment or backslash is a way to make the URL we finally request differ
            // from the one the user reads in the form.
            if (rawUrl.any { it == '?' || it == '#' || it == '\\' }) return null

            // The trailing "/api" is Grocy's REST root; every consumer wants the server root.
            // A payload without it is still accepted - the suffix carries nothing we need.
            var url = normalizeUrlScheme(rawUrl).trimEnd('/')
            if (url.endsWith(API_SUFFIX)) url = url.removeSuffix(API_SUFFIX)
            // Re-check after stripping: "http:///api" collapses to a bare scheme, which would
            // otherwise sail through the authority check below as the host "http:".
            if (!hasHttpScheme(url)) return null

            val authorityAndPath = url.substringAfter("://")
            val authority = authorityAndPath.substringBefore('/')
            // Reject userinfo: "http://grocy.midominio.com@evil.host/" shows the user a host
            // they recognize and connects them to one they do not.
            if (authority.isEmpty() || '@' in authority) return null

            val path = authorityAndPath.removePrefix(authority)
            if (path.contains("//")) return null
            if (path.split('/').any { it == "." || it == ".." }) return null

            val markerIndex = url.indexOf(INGRESS_MARKER)
            if (markerIndex < 0) return SelfHosted(serverUrl = url, apiKey = apiKey)

            val haServerUrl = url.substring(0, markerIndex)
            val proxyId = url.substring(markerIndex + INGRESS_MARKER.length)
            if (haServerUrl.isEmpty() || !SAFE_PROXY_ID.matches(proxyId)) return null
            return HomeAssistant(haServerUrl, proxyId, apiKey)
        }
    }
}
