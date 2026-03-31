# BatteriesApi

All URIs are relative to *http://xxx*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**batteriesBatteryIdChargePost**](BatteriesApi.md#batteriesBatteryIdChargePost) | **POST** /batteries/{batteryId}/charge | Tracks a charge cycle of the given battery |
| [**batteriesBatteryIdGet**](BatteriesApi.md#batteriesBatteryIdGet) | **GET** /batteries/{batteryId} | Returns details of the given battery |
| [**batteriesBatteryIdPrintlabelGet**](BatteriesApi.md#batteriesBatteryIdPrintlabelGet) | **GET** /batteries/{batteryId}/printlabel | Prints the Grocycode label of the given battery on the configured label printer |
| [**batteriesChargeCyclesChargeCycleIdUndoPost**](BatteriesApi.md#batteriesChargeCyclesChargeCycleIdUndoPost) | **POST** /batteries/charge-cycles/{chargeCycleId}/undo | Undoes a battery charge cycle |
| [**batteriesGet**](BatteriesApi.md#batteriesGet) | **GET** /batteries | Returns all batteries incl. the next estimated charge time per battery |


<a id="batteriesBatteryIdChargePost"></a>
# **batteriesBatteryIdChargePost**
> BatteryChargeCycleEntry batteriesBatteryIdChargePost(batteryId, batteriesBatteryIdChargePostRequest)

Tracks a charge cycle of the given battery

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BatteriesApi()
val batteryId : kotlin.Int = 56 // kotlin.Int | A valid battery id
val batteriesBatteryIdChargePostRequest : BatteriesBatteryIdChargePostRequest =  // BatteriesBatteryIdChargePostRequest | 
try {
    val result : BatteryChargeCycleEntry = apiInstance.batteriesBatteryIdChargePost(batteryId, batteriesBatteryIdChargePostRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BatteriesApi#batteriesBatteryIdChargePost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatteriesApi#batteriesBatteryIdChargePost")
    e.printStackTrace()
}
```

### Parameters
| **batteryId** | **kotlin.Int**| A valid battery id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **batteriesBatteryIdChargePostRequest** | [**BatteriesBatteryIdChargePostRequest**](BatteriesBatteryIdChargePostRequest.md)|  | |

### Return type

[**BatteryChargeCycleEntry**](BatteryChargeCycleEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="batteriesBatteryIdGet"></a>
# **batteriesBatteryIdGet**
> BatteryDetailsResponse batteriesBatteryIdGet(batteryId)

Returns details of the given battery

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BatteriesApi()
val batteryId : kotlin.Int = 56 // kotlin.Int | A valid battery id
try {
    val result : BatteryDetailsResponse = apiInstance.batteriesBatteryIdGet(batteryId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BatteriesApi#batteriesBatteryIdGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatteriesApi#batteriesBatteryIdGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **batteryId** | **kotlin.Int**| A valid battery id | |

### Return type

[**BatteryDetailsResponse**](BatteryDetailsResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="batteriesBatteryIdPrintlabelGet"></a>
# **batteriesBatteryIdPrintlabelGet**
> kotlin.String batteriesBatteryIdPrintlabelGet(batteryId)

Prints the Grocycode label of the given battery on the configured label printer

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BatteriesApi()
val batteryId : kotlin.Int = 56 // kotlin.Int | A valid battery id
try {
    val result : kotlin.String = apiInstance.batteriesBatteryIdPrintlabelGet(batteryId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BatteriesApi#batteriesBatteryIdPrintlabelGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatteriesApi#batteriesBatteryIdPrintlabelGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **batteryId** | **kotlin.Int**| A valid battery id | |

### Return type

**kotlin.String**

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="batteriesChargeCyclesChargeCycleIdUndoPost"></a>
# **batteriesChargeCyclesChargeCycleIdUndoPost**
> batteriesChargeCyclesChargeCycleIdUndoPost(chargeCycleId)

Undoes a battery charge cycle

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BatteriesApi()
val chargeCycleId : kotlin.Int = 56 // kotlin.Int | A valid charge cycle id
try {
    apiInstance.batteriesChargeCyclesChargeCycleIdUndoPost(chargeCycleId)
} catch (e: ClientException) {
    println("4xx response calling BatteriesApi#batteriesChargeCyclesChargeCycleIdUndoPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatteriesApi#batteriesChargeCyclesChargeCycleIdUndoPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **chargeCycleId** | **kotlin.Int**| A valid charge cycle id | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="batteriesGet"></a>
# **batteriesGet**
> kotlin.collections.List&lt;CurrentBatteryResponse&gt; batteriesGet(query, order, limit, offset)

Returns all batteries incl. the next estimated charge time per battery

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = BatteriesApi()
val query : kotlin.collections.List<kotlin.String> =  // kotlin.collections.List<kotlin.String> | An array of filter conditions, each of them is a string in the form of `<field><condition><value>` where<br>`<field>` is a valid field name<br>`<condition>` is a comparison operator, one of<br>&nbsp;&nbsp;`=` equal<br>&nbsp;&nbsp;`!=` not equal<br>&nbsp;&nbsp;`~` LIKE<br>&nbsp;&nbsp;`!~` not LIKE<br>&nbsp;&nbsp;`<` less<br>&nbsp;&nbsp;`>` greater<br>&nbsp;&nbsp;`<=` less or equal<br>&nbsp;&nbsp;`>=` greater or equal<br>&nbsp;&nbsp;`§` regular expression<br>`<value>` is the value to search for
val order : kotlin.String = order_example // kotlin.String | A valid field name by which the response should be ordered, use the separator `:` to specify the sort order (`asc` or `desc`, defaults to `asc` when omitted)
val limit : kotlin.Int = 56 // kotlin.Int | The maximum number of objects to return
val offset : kotlin.Int = 56 // kotlin.Int | The number of objects to skip
try {
    val result : kotlin.collections.List<CurrentBatteryResponse> = apiInstance.batteriesGet(query, order, limit, offset)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling BatteriesApi#batteriesGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling BatteriesApi#batteriesGet")
    e.printStackTrace()
}
```

### Parameters
| **query** | [**kotlin.collections.List&lt;kotlin.String&gt;**](kotlin.String.md)| An array of filter conditions, each of them is a string in the form of &#x60;&lt;field&gt;&lt;condition&gt;&lt;value&gt;&#x60; where&lt;br&gt;&#x60;&lt;field&gt;&#x60; is a valid field name&lt;br&gt;&#x60;&lt;condition&gt;&#x60; is a comparison operator, one of&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&#x3D;&#x60; equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;!&#x3D;&#x60; not equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;~&#x60; LIKE&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;!~&#x60; not LIKE&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&lt;&#x60; less&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&gt;&#x60; greater&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&lt;&#x3D;&#x60; less or equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&gt;&#x3D;&#x60; greater or equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;§&#x60; regular expression&lt;br&gt;&#x60;&lt;value&gt;&#x60; is the value to search for | [optional] |
| **order** | **kotlin.String**| A valid field name by which the response should be ordered, use the separator &#x60;:&#x60; to specify the sort order (&#x60;asc&#x60; or &#x60;desc&#x60;, defaults to &#x60;asc&#x60; when omitted) | [optional] |
| **limit** | **kotlin.Int**| The maximum number of objects to return | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **offset** | **kotlin.Int**| The number of objects to skip | [optional] |

### Return type

[**kotlin.collections.List&lt;CurrentBatteryResponse&gt;**](CurrentBatteryResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

