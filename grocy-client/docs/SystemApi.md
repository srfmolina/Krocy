# SystemApi

All URIs are relative to *http://xxx*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**systemConfigGet**](SystemApi.md#systemConfigGet) | **GET** /system/config | Returns all config settings |
| [**systemDbChangedTimeGet**](SystemApi.md#systemDbChangedTimeGet) | **GET** /system/db-changed-time | Returns the time when the database was last changed |
| [**systemInfoGet**](SystemApi.md#systemInfoGet) | **GET** /system/info | Returns information about the installed Grocy version, PHP runtime and OS |
| [**systemLocalizationStringsGet**](SystemApi.md#systemLocalizationStringsGet) | **GET** /system/localization-strings | Returns all localization strings (in the by the user desired language) |
| [**systemLogMissingLocalizationPost**](SystemApi.md#systemLogMissingLocalizationPost) | **POST** /system/log-missing-localization | Logs a missing localization string |
| [**systemTimeGet**](SystemApi.md#systemTimeGet) | **GET** /system/time | Returns the current server time |


<a id="systemConfigGet"></a>
# **systemConfigGet**
> kotlin.String systemConfigGet()

Returns all config settings

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SystemApi()
try {
    val result : kotlin.String = apiInstance.systemConfigGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SystemApi#systemConfigGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SystemApi#systemConfigGet")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

**kotlin.String**

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="systemDbChangedTimeGet"></a>
# **systemDbChangedTimeGet**
> DbChangedTimeResponse systemDbChangedTimeGet()

Returns the time when the database was last changed

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SystemApi()
try {
    val result : DbChangedTimeResponse = apiInstance.systemDbChangedTimeGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SystemApi#systemDbChangedTimeGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SystemApi#systemDbChangedTimeGet")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**DbChangedTimeResponse**](DbChangedTimeResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="systemInfoGet"></a>
# **systemInfoGet**
> SystemInfoGet200Response systemInfoGet()

Returns information about the installed Grocy version, PHP runtime and OS

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SystemApi()
try {
    val result : SystemInfoGet200Response = apiInstance.systemInfoGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SystemApi#systemInfoGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SystemApi#systemInfoGet")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**SystemInfoGet200Response**](SystemInfoGet200Response.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="systemLocalizationStringsGet"></a>
# **systemLocalizationStringsGet**
> kotlin.String systemLocalizationStringsGet()

Returns all localization strings (in the by the user desired language)

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SystemApi()
try {
    val result : kotlin.String = apiInstance.systemLocalizationStringsGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SystemApi#systemLocalizationStringsGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SystemApi#systemLocalizationStringsGet")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

**kotlin.String**

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="systemLogMissingLocalizationPost"></a>
# **systemLogMissingLocalizationPost**
> systemLogMissingLocalizationPost(missingLocalizationRequest)

Logs a missing localization string

Only when MODE &#x3D;&#x3D; &#39;dev&#39;, so should only be called then

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SystemApi()
val missingLocalizationRequest : MissingLocalizationRequest =  // MissingLocalizationRequest | A valid MissingLocalizationRequest object
try {
    apiInstance.systemLogMissingLocalizationPost(missingLocalizationRequest)
} catch (e: ClientException) {
    println("4xx response calling SystemApi#systemLogMissingLocalizationPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SystemApi#systemLogMissingLocalizationPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **missingLocalizationRequest** | [**MissingLocalizationRequest**](MissingLocalizationRequest.md)| A valid MissingLocalizationRequest object | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="systemTimeGet"></a>
# **systemTimeGet**
> TimeResponse systemTimeGet(offset)

Returns the current server time

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = SystemApi()
val offset : kotlin.Int = 56 // kotlin.Int | Offset of timestamp in seconds. Can be positive or negative.
try {
    val result : TimeResponse = apiInstance.systemTimeGet(offset)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling SystemApi#systemTimeGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling SystemApi#systemTimeGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **offset** | **kotlin.Int**| Offset of timestamp in seconds. Can be positive or negative. | [optional] |

### Return type

[**TimeResponse**](TimeResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

