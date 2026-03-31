# StockByBarcodeApi

All URIs are relative to *http://xxx*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**stockProductsByBarcodeBarcodeAddPost**](StockByBarcodeApi.md#stockProductsByBarcodeBarcodeAddPost) | **POST** /stock/products/by-barcode/{barcode}/add | Adds the given amount of the by its barcode given product to stock |
| [**stockProductsByBarcodeBarcodeConsumePost**](StockByBarcodeApi.md#stockProductsByBarcodeBarcodeConsumePost) | **POST** /stock/products/by-barcode/{barcode}/consume | Removes the given amount of the by its barcode given product from stock |
| [**stockProductsByBarcodeBarcodeGet**](StockByBarcodeApi.md#stockProductsByBarcodeBarcodeGet) | **GET** /stock/products/by-barcode/{barcode} | Returns details of the given product by its barcode |
| [**stockProductsByBarcodeBarcodeInventoryPost**](StockByBarcodeApi.md#stockProductsByBarcodeBarcodeInventoryPost) | **POST** /stock/products/by-barcode/{barcode}/inventory | Inventories the by its barcode given product (adds/removes based on the given new amount) |
| [**stockProductsByBarcodeBarcodeOpenPost**](StockByBarcodeApi.md#stockProductsByBarcodeBarcodeOpenPost) | **POST** /stock/products/by-barcode/{barcode}/open | Marks the given amount of the by its barcode given product as opened |
| [**stockProductsByBarcodeBarcodeTransferPost**](StockByBarcodeApi.md#stockProductsByBarcodeBarcodeTransferPost) | **POST** /stock/products/by-barcode/{barcode}/transfer | Transfers the given amount of the by its barcode given product from one location to another (this is currently not supported for tare weight handling enabled products) |


<a id="stockProductsByBarcodeBarcodeAddPost"></a>
# **stockProductsByBarcodeBarcodeAddPost**
> kotlin.collections.List&lt;StockLogEntry&gt; stockProductsByBarcodeBarcodeAddPost(barcode, stockProductsByBarcodeBarcodeAddPostRequest)

Adds the given amount of the by its barcode given product to stock

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockByBarcodeApi()
val barcode : kotlin.String = barcode_example // kotlin.String | Barcode
val stockProductsByBarcodeBarcodeAddPostRequest : StockProductsByBarcodeBarcodeAddPostRequest =  // StockProductsByBarcodeBarcodeAddPostRequest | 
try {
    val result : kotlin.collections.List<StockLogEntry> = apiInstance.stockProductsByBarcodeBarcodeAddPost(barcode, stockProductsByBarcodeBarcodeAddPostRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockByBarcodeApi#stockProductsByBarcodeBarcodeAddPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockByBarcodeApi#stockProductsByBarcodeBarcodeAddPost")
    e.printStackTrace()
}
```

### Parameters
| **barcode** | **kotlin.String**| Barcode | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockProductsByBarcodeBarcodeAddPostRequest** | [**StockProductsByBarcodeBarcodeAddPostRequest**](StockProductsByBarcodeBarcodeAddPostRequest.md)|  | |

### Return type

[**kotlin.collections.List&lt;StockLogEntry&gt;**](StockLogEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockProductsByBarcodeBarcodeConsumePost"></a>
# **stockProductsByBarcodeBarcodeConsumePost**
> kotlin.collections.List&lt;StockLogEntry&gt; stockProductsByBarcodeBarcodeConsumePost(barcode, stockProductsByBarcodeBarcodeConsumePostRequest)

Removes the given amount of the by its barcode given product from stock

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockByBarcodeApi()
val barcode : kotlin.String = barcode_example // kotlin.String | Barcode
val stockProductsByBarcodeBarcodeConsumePostRequest : StockProductsByBarcodeBarcodeConsumePostRequest =  // StockProductsByBarcodeBarcodeConsumePostRequest | 
try {
    val result : kotlin.collections.List<StockLogEntry> = apiInstance.stockProductsByBarcodeBarcodeConsumePost(barcode, stockProductsByBarcodeBarcodeConsumePostRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockByBarcodeApi#stockProductsByBarcodeBarcodeConsumePost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockByBarcodeApi#stockProductsByBarcodeBarcodeConsumePost")
    e.printStackTrace()
}
```

### Parameters
| **barcode** | **kotlin.String**| Barcode | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockProductsByBarcodeBarcodeConsumePostRequest** | [**StockProductsByBarcodeBarcodeConsumePostRequest**](StockProductsByBarcodeBarcodeConsumePostRequest.md)|  | |

### Return type

[**kotlin.collections.List&lt;StockLogEntry&gt;**](StockLogEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockProductsByBarcodeBarcodeGet"></a>
# **stockProductsByBarcodeBarcodeGet**
> ProductDetailsResponse stockProductsByBarcodeBarcodeGet(barcode)

Returns details of the given product by its barcode

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockByBarcodeApi()
val barcode : kotlin.String = barcode_example // kotlin.String | Barcode
try {
    val result : ProductDetailsResponse = apiInstance.stockProductsByBarcodeBarcodeGet(barcode)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockByBarcodeApi#stockProductsByBarcodeBarcodeGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockByBarcodeApi#stockProductsByBarcodeBarcodeGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **barcode** | **kotlin.String**| Barcode | |

### Return type

[**ProductDetailsResponse**](ProductDetailsResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockProductsByBarcodeBarcodeInventoryPost"></a>
# **stockProductsByBarcodeBarcodeInventoryPost**
> kotlin.collections.List&lt;StockLogEntry&gt; stockProductsByBarcodeBarcodeInventoryPost(barcode, stockProductsByBarcodeBarcodeInventoryPostRequest)

Inventories the by its barcode given product (adds/removes based on the given new amount)

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockByBarcodeApi()
val barcode : kotlin.String = barcode_example // kotlin.String | Barcode
val stockProductsByBarcodeBarcodeInventoryPostRequest : StockProductsByBarcodeBarcodeInventoryPostRequest =  // StockProductsByBarcodeBarcodeInventoryPostRequest | 
try {
    val result : kotlin.collections.List<StockLogEntry> = apiInstance.stockProductsByBarcodeBarcodeInventoryPost(barcode, stockProductsByBarcodeBarcodeInventoryPostRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockByBarcodeApi#stockProductsByBarcodeBarcodeInventoryPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockByBarcodeApi#stockProductsByBarcodeBarcodeInventoryPost")
    e.printStackTrace()
}
```

### Parameters
| **barcode** | **kotlin.String**| Barcode | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockProductsByBarcodeBarcodeInventoryPostRequest** | [**StockProductsByBarcodeBarcodeInventoryPostRequest**](StockProductsByBarcodeBarcodeInventoryPostRequest.md)|  | |

### Return type

[**kotlin.collections.List&lt;StockLogEntry&gt;**](StockLogEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockProductsByBarcodeBarcodeOpenPost"></a>
# **stockProductsByBarcodeBarcodeOpenPost**
> kotlin.collections.List&lt;StockLogEntry&gt; stockProductsByBarcodeBarcodeOpenPost(barcode, stockProductsByBarcodeBarcodeOpenPostRequest)

Marks the given amount of the by its barcode given product as opened

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockByBarcodeApi()
val barcode : kotlin.String = barcode_example // kotlin.String | Barcode
val stockProductsByBarcodeBarcodeOpenPostRequest : StockProductsByBarcodeBarcodeOpenPostRequest =  // StockProductsByBarcodeBarcodeOpenPostRequest | 
try {
    val result : kotlin.collections.List<StockLogEntry> = apiInstance.stockProductsByBarcodeBarcodeOpenPost(barcode, stockProductsByBarcodeBarcodeOpenPostRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockByBarcodeApi#stockProductsByBarcodeBarcodeOpenPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockByBarcodeApi#stockProductsByBarcodeBarcodeOpenPost")
    e.printStackTrace()
}
```

### Parameters
| **barcode** | **kotlin.String**| Barcode | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockProductsByBarcodeBarcodeOpenPostRequest** | [**StockProductsByBarcodeBarcodeOpenPostRequest**](StockProductsByBarcodeBarcodeOpenPostRequest.md)|  | |

### Return type

[**kotlin.collections.List&lt;StockLogEntry&gt;**](StockLogEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockProductsByBarcodeBarcodeTransferPost"></a>
# **stockProductsByBarcodeBarcodeTransferPost**
> kotlin.collections.List&lt;StockLogEntry&gt; stockProductsByBarcodeBarcodeTransferPost(barcode, stockProductsProductIdTransferPostRequest)

Transfers the given amount of the by its barcode given product from one location to another (this is currently not supported for tare weight handling enabled products)

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockByBarcodeApi()
val barcode : kotlin.String = barcode_example // kotlin.String | Barcode
val stockProductsProductIdTransferPostRequest : StockProductsProductIdTransferPostRequest =  // StockProductsProductIdTransferPostRequest | 
try {
    val result : kotlin.collections.List<StockLogEntry> = apiInstance.stockProductsByBarcodeBarcodeTransferPost(barcode, stockProductsProductIdTransferPostRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockByBarcodeApi#stockProductsByBarcodeBarcodeTransferPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockByBarcodeApi#stockProductsByBarcodeBarcodeTransferPost")
    e.printStackTrace()
}
```

### Parameters
| **barcode** | **kotlin.String**| Barcode | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockProductsProductIdTransferPostRequest** | [**StockProductsProductIdTransferPostRequest**](StockProductsProductIdTransferPostRequest.md)|  | |

### Return type

[**kotlin.collections.List&lt;StockLogEntry&gt;**](StockLogEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

