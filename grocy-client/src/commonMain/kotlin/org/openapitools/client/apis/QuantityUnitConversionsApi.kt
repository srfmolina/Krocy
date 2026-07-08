package org.openapitools.client.apis

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.openapitools.client.infrastructure.ApiClient
import org.openapitools.client.infrastructure.HttpResponse
import org.openapitools.client.infrastructure.RequestConfig
import org.openapitools.client.infrastructure.RequestMethod
import org.openapitools.client.infrastructure.map
import org.openapitools.client.infrastructure.toMultiValue
import org.openapitools.client.infrastructure.wrap
import org.openapitools.client.models.ObjectsEntityPost200Response
import org.openapitools.client.models.QuantityUnitConversion

// Workaround for broken Grocy spec: /objects/quantity_unit_conversions has no usable schema in
// the spec, so GenericEntityInteractionsApi's merged response model drops the conversion fields
// (from_qu_id/to_qu_id/factor). This hand-written API mirrors the generated generic-entity
// endpoints but typed with the QuantityUnitConversion model. Protected via .openapi-generator-ignore.
open class QuantityUnitConversionsApi : ApiClient {

    constructor(
        baseUrl: String = ApiClient.BASE_URL,
        httpClientEngine: HttpClientEngine? = null,
        httpClientConfig: ((HttpClientConfig<*>) -> Unit)? = null,
        jsonSerializer: Json = ApiClient.JSON_DEFAULT
    ) : super(baseUrl = baseUrl, httpClientEngine = httpClientEngine, httpClientConfig = httpClientConfig, jsonBlock = jsonSerializer)

    constructor(
        baseUrl: String,
        httpClient: HttpClient
    ): super(baseUrl = baseUrl, httpClient = httpClient)

    /**
     * Returns all quantity unit conversions
     *
     * @param query An array of filter conditions in the form of &#x60;&lt;field&gt;&lt;condition&gt;&lt;value&gt;&#x60;, e.g. &#x60;product_id&#x3D;1&#x60; (optional)
     * @return kotlin.collections.List<QuantityUnitConversion>
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun objectsQuantityUnitConversionsGet(query: kotlin.collections.List<kotlin.String>? = null): HttpResponse<kotlin.collections.List<QuantityUnitConversion>> {

        val localVariableAuthNames = listOf<String>("ApiKeyAuth")

        val localVariableBody =
            io.ktor.client.utils.EmptyContent

        val localVariableQuery = mutableMapOf<String, List<String>>()
        query?.apply { localVariableQuery["query[]"] = toMultiValue(this, "multi") }
        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.GET,
            "/objects/quantity_unit_conversions",
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = true,
        )

        return request(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap<ObjectsQuantityUnitConversionsGetResponse>().map { value }
    }

    @Serializable(ObjectsQuantityUnitConversionsGetResponse.Companion::class)
    private class ObjectsQuantityUnitConversionsGetResponse(val value: List<QuantityUnitConversion>) {
        companion object : KSerializer<ObjectsQuantityUnitConversionsGetResponse> {
            private val serializer: KSerializer<List<QuantityUnitConversion>> = serializer<List<QuantityUnitConversion>>()
            override val descriptor = serializer.descriptor
            override fun serialize(encoder: Encoder, value: ObjectsQuantityUnitConversionsGetResponse) = serializer.serialize(encoder, value.value)
            override fun deserialize(decoder: Decoder) = ObjectsQuantityUnitConversionsGetResponse(serializer.deserialize(decoder))
        }
    }

    /**
     * Adds a single quantity unit conversion
     *
     * @param quantityUnitConversion A valid quantity unit conversion object
     * @return ObjectsEntityPost200Response
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun objectsQuantityUnitConversionsPost(quantityUnitConversion: QuantityUnitConversion): HttpResponse<ObjectsEntityPost200Response> {

        val localVariableAuthNames = listOf<String>("ApiKeyAuth")

        val localVariableBody = quantityUnitConversion

        val localVariableQuery = mutableMapOf<String, List<String>>()
        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.POST,
            "/objects/quantity_unit_conversions",
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = true,
        )

        return jsonRequest(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap()
    }

    /**
     * Edits the given quantity unit conversion
     *
     * @param objectId A valid quantity unit conversion id
     * @param quantityUnitConversion A valid quantity unit conversion object
     * @return void
     */
    open suspend fun objectsQuantityUnitConversionsObjectIdPut(objectId: kotlin.Int, quantityUnitConversion: QuantityUnitConversion): HttpResponse<Unit> {

        val localVariableAuthNames = listOf<String>("ApiKeyAuth")

        val localVariableBody = quantityUnitConversion

        val localVariableQuery = mutableMapOf<String, List<String>>()
        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.PUT,
            "/objects/quantity_unit_conversions/{objectId}".replace("{" + "objectId" + "}", "$objectId"),
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = true,
        )

        return jsonRequest(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap()
    }

}
