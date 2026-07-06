package com.srfmolina.krocy.data.datasource.remote.conversion

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class QuConversionDataSourceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) : QuConversionDataSource {

    override suspend fun getQuConversions(): Result<List<QuConversionDto>> = runCatching {
        httpClient.get("$baseUrl/objects/quantity_unit_conversions") {
            expectSuccess = true
        }.body()
    }

    override suspend fun getQuConversionsForProduct(productId: Int): Result<List<QuConversionDto>> =
        runCatching {
            httpClient.get("$baseUrl/objects/quantity_unit_conversions") {
                parameter("query[]", "product_id=$productId")
                expectSuccess = true
            }.body()
        }

    override suspend fun createQuConversion(body: QuConversionDto): Result<Int> = runCatching {
        val response: CreatedObjectDto = httpClient.post("$baseUrl/objects/quantity_unit_conversions") {
            contentType(ContentType.Application.Json)
            setBody(body)
            expectSuccess = true
        }.body()
        response.createdObjectId ?: error("Grocy did not return a created object id")
    }

    override suspend fun updateQuConversion(id: Int, body: QuConversionDto): Result<Unit> =
        runCatching {
            httpClient.put("$baseUrl/objects/quantity_unit_conversions/$id") {
                contentType(ContentType.Application.Json)
                setBody(body)
                // PUT answers 204 with an empty body, so success must be checked via status.
                expectSuccess = true
            }
        }
}
