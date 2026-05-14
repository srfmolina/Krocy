# PrintApi

All URIs are relative to *http://xxx*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**printShoppinglistThermalGet**](PrintApi.md#printShoppinglistThermalGet) | **GET** /print/shoppinglist/thermal | Prints the shoppinglist with a thermal printer |


<a id="printShoppinglistThermalGet"></a>
# **printShoppinglistThermalGet**
> PrintShoppinglistThermalGet200Response printShoppinglistThermalGet(list, printHeader)

Prints the shoppinglist with a thermal printer

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = PrintApi()
val list : kotlin.Int = 56 // kotlin.Int | Shopping list id
val printHeader : kotlin.Int = true // kotlin.Int | Prints Grocy logo if true
try {
    val result : PrintShoppinglistThermalGet200Response = apiInstance.printShoppinglistThermalGet(list, printHeader)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling PrintApi#printShoppinglistThermalGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling PrintApi#printShoppinglistThermalGet")
    e.printStackTrace()
}
```

### Parameters
| **list** | **kotlin.Int**| Shopping list id | [optional] [default to 1] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **printHeader** | **kotlin.Int**| Prints Grocy logo if true | [optional] [default to true] |

### Return type

[**PrintShoppinglistThermalGet200Response**](PrintShoppinglistThermalGet200Response.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

