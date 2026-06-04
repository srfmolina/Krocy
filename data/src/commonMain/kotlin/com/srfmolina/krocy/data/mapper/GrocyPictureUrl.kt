package com.srfmolina.krocy.data.mapper

import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Builds the Grocy file-serve URL for a product picture.
 *
 * Grocy serves files at `GET /files/{group}/{fileName}` where the file name must be
 * BASE64-encoded. `appendPathSegments` percent-encodes the base64 (`/`, `+`, `=`), which
 * Grocy decodes server-side before base64-decoding. `force_serve_as=picture` enables the
 * `best_fit_width` server-side downscaling.
 */
@OptIn(ExperimentalEncodingApi::class)
internal fun buildProductPictureUrl(baseUrl: String, fileName: String, bestFitWidth: Int = 200): String {
    val encoded = Base64.encode(fileName.encodeToByteArray())
    return URLBuilder(baseUrl).apply {
        appendPathSegments("files", "productpictures", encoded)
        parameters.append("force_serve_as", "picture")
        parameters.append("best_fit_width", bestFitWidth.toString())
    }.buildString()
}
