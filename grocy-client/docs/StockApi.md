# StockApi

All URIs are relative to *http://xxx*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**stockBarcodesExternalLookupBarcodeGet**](StockApi.md#stockBarcodesExternalLookupBarcodeGet) | **GET** /stock/barcodes/external-lookup/{barcode} | Executes an external barcode lookoup via the configured plugin with the given barcode |
| [**stockBookingsBookingIdGet**](StockApi.md#stockBookingsBookingIdGet) | **GET** /stock/bookings/{bookingId} | Returns the given stock booking |
| [**stockBookingsBookingIdUndoPost**](StockApi.md#stockBookingsBookingIdUndoPost) | **POST** /stock/bookings/{bookingId}/undo | Undoes a booking |
| [**stockEntryEntryIdGet**](StockApi.md#stockEntryEntryIdGet) | **GET** /stock/entry/{entryId} | Returns details of the given stock |
| [**stockEntryEntryIdPrintlabelGet**](StockApi.md#stockEntryEntryIdPrintlabelGet) | **GET** /stock/entry/{entryId}/printlabel | Prints the Grocycode / stock entry label of the given entry on the configured label printer |
| [**stockEntryEntryIdPut**](StockApi.md#stockEntryEntryIdPut) | **PUT** /stock/entry/{entryId} | Edits the stock entry |
| [**stockGet**](StockApi.md#stockGet) | **GET** /stock | Returns all products which are currently in stock incl. the next due date per product |
| [**stockLocationsLocationIdEntriesGet**](StockApi.md#stockLocationsLocationIdEntriesGet) | **GET** /stock/locations/{locationId}/entries | Returns all stock entries of the given location |
| [**stockProductsProductIdAddPost**](StockApi.md#stockProductsProductIdAddPost) | **POST** /stock/products/{productId}/add | Adds the given amount of the given product to stock |
| [**stockProductsProductIdConsumePost**](StockApi.md#stockProductsProductIdConsumePost) | **POST** /stock/products/{productId}/consume | Removes the given amount of the given product from stock |
| [**stockProductsProductIdEntriesGet**](StockApi.md#stockProductsProductIdEntriesGet) | **GET** /stock/products/{productId}/entries | Returns all stock entries of the given product in order of next use (Opened first, then first due first, then first in first out) |
| [**stockProductsProductIdGet**](StockApi.md#stockProductsProductIdGet) | **GET** /stock/products/{productId} | Returns details of the given product |
| [**stockProductsProductIdInventoryPost**](StockApi.md#stockProductsProductIdInventoryPost) | **POST** /stock/products/{productId}/inventory | Inventories the given product (adds/removes based on the given new amount) |
| [**stockProductsProductIdLocationsGet**](StockApi.md#stockProductsProductIdLocationsGet) | **GET** /stock/products/{productId}/locations | Returns all locations where the given product currently has stock |
| [**stockProductsProductIdOpenPost**](StockApi.md#stockProductsProductIdOpenPost) | **POST** /stock/products/{productId}/open | Marks the given amount of the given product as opened |
| [**stockProductsProductIdPriceHistoryGet**](StockApi.md#stockProductsProductIdPriceHistoryGet) | **GET** /stock/products/{productId}/price-history | Returns the price history of the given product |
| [**stockProductsProductIdPrintlabelGet**](StockApi.md#stockProductsProductIdPrintlabelGet) | **GET** /stock/products/{productId}/printlabel | Prints the Grocycode label of the given product on the configured label printer |
| [**stockProductsProductIdToKeepMergeProductIdToRemovePost**](StockApi.md#stockProductsProductIdToKeepMergeProductIdToRemovePost) | **POST** /stock/products/{productIdToKeep}/merge/{productIdToRemove} | Merges two products into one |
| [**stockProductsProductIdTransferPost**](StockApi.md#stockProductsProductIdTransferPost) | **POST** /stock/products/{productId}/transfer | Transfers the given amount of the given product from one location to another (this is currently not supported for tare weight handling enabled products) |
| [**stockShoppinglistAddExpiredProductsPost**](StockApi.md#stockShoppinglistAddExpiredProductsPost) | **POST** /stock/shoppinglist/add-expired-products | Adds expired products to the given shopping list |
| [**stockShoppinglistAddMissingProductsPost**](StockApi.md#stockShoppinglistAddMissingProductsPost) | **POST** /stock/shoppinglist/add-missing-products | Adds currently missing products (below defined min. stock amount) to the given shopping list |
| [**stockShoppinglistAddOverdueProductsPost**](StockApi.md#stockShoppinglistAddOverdueProductsPost) | **POST** /stock/shoppinglist/add-overdue-products | Adds overdue products to the given shopping list |
| [**stockShoppinglistAddProductPost**](StockApi.md#stockShoppinglistAddProductPost) | **POST** /stock/shoppinglist/add-product | Adds the given amount of the given product to the given shopping list |
| [**stockShoppinglistClearPost**](StockApi.md#stockShoppinglistClearPost) | **POST** /stock/shoppinglist/clear | Removes all items from the given shopping list |
| [**stockShoppinglistRemoveProductPost**](StockApi.md#stockShoppinglistRemoveProductPost) | **POST** /stock/shoppinglist/remove-product | Removes the given amount of the given product from the given shopping list, if it is on it |
| [**stockTransactionsTransactionIdGet**](StockApi.md#stockTransactionsTransactionIdGet) | **GET** /stock/transactions/{transactionId} | Returns all stock bookings of the given transaction id |
| [**stockTransactionsTransactionIdUndoPost**](StockApi.md#stockTransactionsTransactionIdUndoPost) | **POST** /stock/transactions/{transactionId}/undo | Undoes a transaction |
| [**stockVolatileGet**](StockApi.md#stockVolatileGet) | **GET** /stock/volatile | Returns all products which are due soon, overdue, expired or currently missing |


<a id="stockBarcodesExternalLookupBarcodeGet"></a>
# **stockBarcodesExternalLookupBarcodeGet**
> ExternalBarcodeLookupResponse stockBarcodesExternalLookupBarcodeGet(barcode, add)

Executes an external barcode lookoup via the configured plugin with the given barcode

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val barcode : kotlin.String = barcode_example // kotlin.String | The barcode to lookup up
val add : kotlin.Boolean = true // kotlin.Boolean | When true, the product is added to the database on a successful lookup and the new product id is in included in the response
try {
    val result : ExternalBarcodeLookupResponse = apiInstance.stockBarcodesExternalLookupBarcodeGet(barcode, add)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockBarcodesExternalLookupBarcodeGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockBarcodesExternalLookupBarcodeGet")
    e.printStackTrace()
}
```

### Parameters
| **barcode** | **kotlin.String**| The barcode to lookup up | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **add** | **kotlin.Boolean**| When true, the product is added to the database on a successful lookup and the new product id is in included in the response | [optional] [default to false] |

### Return type

[**ExternalBarcodeLookupResponse**](ExternalBarcodeLookupResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockBookingsBookingIdGet"></a>
# **stockBookingsBookingIdGet**
> StockLogEntry stockBookingsBookingIdGet(bookingId)

Returns the given stock booking

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val bookingId : kotlin.Int = 56 // kotlin.Int | A valid stock booking id
try {
    val result : StockLogEntry = apiInstance.stockBookingsBookingIdGet(bookingId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockBookingsBookingIdGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockBookingsBookingIdGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **bookingId** | **kotlin.Int**| A valid stock booking id | |

### Return type

[**StockLogEntry**](StockLogEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockBookingsBookingIdUndoPost"></a>
# **stockBookingsBookingIdUndoPost**
> stockBookingsBookingIdUndoPost(bookingId)

Undoes a booking

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val bookingId : kotlin.Int = 56 // kotlin.Int | A valid stock booking id
try {
    apiInstance.stockBookingsBookingIdUndoPost(bookingId)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockBookingsBookingIdUndoPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockBookingsBookingIdUndoPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **bookingId** | **kotlin.Int**| A valid stock booking id | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockEntryEntryIdGet"></a>
# **stockEntryEntryIdGet**
> StockEntry stockEntryEntryIdGet(entryId)

Returns details of the given stock

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val entryId : kotlin.Int = 56 // kotlin.Int | A valid stock entry id
try {
    val result : StockEntry = apiInstance.stockEntryEntryIdGet(entryId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockEntryEntryIdGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockEntryEntryIdGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **entryId** | **kotlin.Int**| A valid stock entry id | |

### Return type

[**StockEntry**](StockEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockEntryEntryIdPrintlabelGet"></a>
# **stockEntryEntryIdPrintlabelGet**
> kotlin.String stockEntryEntryIdPrintlabelGet(entryId)

Prints the Grocycode / stock entry label of the given entry on the configured label printer

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val entryId : kotlin.Int = 56 // kotlin.Int | A valid stock entry id
try {
    val result : kotlin.String = apiInstance.stockEntryEntryIdPrintlabelGet(entryId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockEntryEntryIdPrintlabelGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockEntryEntryIdPrintlabelGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **entryId** | **kotlin.Int**| A valid stock entry id | |

### Return type

**kotlin.String**

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockEntryEntryIdPut"></a>
# **stockEntryEntryIdPut**
> kotlin.collections.List&lt;StockLogEntry&gt; stockEntryEntryIdPut(entryId, stockEntryEntryIdPutRequest)

Edits the stock entry

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val entryId : kotlin.Int = 56 // kotlin.Int | A valid stock entry id
val stockEntryEntryIdPutRequest : StockEntryEntryIdPutRequest =  // StockEntryEntryIdPutRequest | 
try {
    val result : kotlin.collections.List<StockLogEntry> = apiInstance.stockEntryEntryIdPut(entryId, stockEntryEntryIdPutRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockEntryEntryIdPut")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockEntryEntryIdPut")
    e.printStackTrace()
}
```

### Parameters
| **entryId** | **kotlin.Int**| A valid stock entry id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockEntryEntryIdPutRequest** | [**StockEntryEntryIdPutRequest**](StockEntryEntryIdPutRequest.md)|  | |

### Return type

[**kotlin.collections.List&lt;StockLogEntry&gt;**](StockLogEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockGet"></a>
# **stockGet**
> kotlin.collections.List&lt;CurrentStockResponse&gt; stockGet()

Returns all products which are currently in stock incl. the next due date per product

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
try {
    val result : kotlin.collections.List<CurrentStockResponse> = apiInstance.stockGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockGet")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**kotlin.collections.List&lt;CurrentStockResponse&gt;**](CurrentStockResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockLocationsLocationIdEntriesGet"></a>
# **stockLocationsLocationIdEntriesGet**
> kotlin.collections.List&lt;StockEntry&gt; stockLocationsLocationIdEntriesGet(locationId, query, order, limit, offset)

Returns all stock entries of the given location

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val locationId : kotlin.Int = 56 // kotlin.Int | A valid location id
val query : kotlin.collections.List<kotlin.String> =  // kotlin.collections.List<kotlin.String> | An array of filter conditions, each of them is a string in the form of `<field><condition><value>` where<br>`<field>` is a valid field name<br>`<condition>` is a comparison operator, one of<br>&nbsp;&nbsp;`=` equal<br>&nbsp;&nbsp;`!=` not equal<br>&nbsp;&nbsp;`~` LIKE<br>&nbsp;&nbsp;`!~` not LIKE<br>&nbsp;&nbsp;`<` less<br>&nbsp;&nbsp;`>` greater<br>&nbsp;&nbsp;`<=` less or equal<br>&nbsp;&nbsp;`>=` greater or equal<br>&nbsp;&nbsp;`§` regular expression<br>`<value>` is the value to search for
val order : kotlin.String = order_example // kotlin.String | A valid field name by which the response should be ordered, use the separator `:` to specify the sort order (`asc` or `desc`, defaults to `asc` when omitted)
val limit : kotlin.Int = 56 // kotlin.Int | The maximum number of objects to return
val offset : kotlin.Int = 56 // kotlin.Int | The number of objects to skip
try {
    val result : kotlin.collections.List<StockEntry> = apiInstance.stockLocationsLocationIdEntriesGet(locationId, query, order, limit, offset)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockLocationsLocationIdEntriesGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockLocationsLocationIdEntriesGet")
    e.printStackTrace()
}
```

### Parameters
| **locationId** | **kotlin.Int**| A valid location id | |
| **query** | [**kotlin.collections.List&lt;kotlin.String&gt;**](kotlin.String.md)| An array of filter conditions, each of them is a string in the form of &#x60;&lt;field&gt;&lt;condition&gt;&lt;value&gt;&#x60; where&lt;br&gt;&#x60;&lt;field&gt;&#x60; is a valid field name&lt;br&gt;&#x60;&lt;condition&gt;&#x60; is a comparison operator, one of&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&#x3D;&#x60; equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;!&#x3D;&#x60; not equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;~&#x60; LIKE&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;!~&#x60; not LIKE&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&lt;&#x60; less&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&gt;&#x60; greater&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&lt;&#x3D;&#x60; less or equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&gt;&#x3D;&#x60; greater or equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;§&#x60; regular expression&lt;br&gt;&#x60;&lt;value&gt;&#x60; is the value to search for | [optional] |
| **order** | **kotlin.String**| A valid field name by which the response should be ordered, use the separator &#x60;:&#x60; to specify the sort order (&#x60;asc&#x60; or &#x60;desc&#x60;, defaults to &#x60;asc&#x60; when omitted) | [optional] |
| **limit** | **kotlin.Int**| The maximum number of objects to return | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **offset** | **kotlin.Int**| The number of objects to skip | [optional] |

### Return type

[**kotlin.collections.List&lt;StockEntry&gt;**](StockEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockProductsProductIdAddPost"></a>
# **stockProductsProductIdAddPost**
> kotlin.collections.List&lt;StockLogEntry&gt; stockProductsProductIdAddPost(productId, stockProductsProductIdAddPostRequest)

Adds the given amount of the given product to stock

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val productId : kotlin.Int = 56 // kotlin.Int | A valid product id
val stockProductsProductIdAddPostRequest : StockProductsProductIdAddPostRequest =  // StockProductsProductIdAddPostRequest | 
try {
    val result : kotlin.collections.List<StockLogEntry> = apiInstance.stockProductsProductIdAddPost(productId, stockProductsProductIdAddPostRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockProductsProductIdAddPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockProductsProductIdAddPost")
    e.printStackTrace()
}
```

### Parameters
| **productId** | **kotlin.Int**| A valid product id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockProductsProductIdAddPostRequest** | [**StockProductsProductIdAddPostRequest**](StockProductsProductIdAddPostRequest.md)|  | |

### Return type

[**kotlin.collections.List&lt;StockLogEntry&gt;**](StockLogEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockProductsProductIdConsumePost"></a>
# **stockProductsProductIdConsumePost**
> kotlin.collections.List&lt;StockLogEntry&gt; stockProductsProductIdConsumePost(productId, stockProductsProductIdConsumePostRequest)

Removes the given amount of the given product from stock

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val productId : kotlin.Int = 56 // kotlin.Int | A valid product id
val stockProductsProductIdConsumePostRequest : StockProductsProductIdConsumePostRequest =  // StockProductsProductIdConsumePostRequest | 
try {
    val result : kotlin.collections.List<StockLogEntry> = apiInstance.stockProductsProductIdConsumePost(productId, stockProductsProductIdConsumePostRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockProductsProductIdConsumePost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockProductsProductIdConsumePost")
    e.printStackTrace()
}
```

### Parameters
| **productId** | **kotlin.Int**| A valid product id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockProductsProductIdConsumePostRequest** | [**StockProductsProductIdConsumePostRequest**](StockProductsProductIdConsumePostRequest.md)|  | |

### Return type

[**kotlin.collections.List&lt;StockLogEntry&gt;**](StockLogEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockProductsProductIdEntriesGet"></a>
# **stockProductsProductIdEntriesGet**
> kotlin.collections.List&lt;StockEntry&gt; stockProductsProductIdEntriesGet(productId, includeSubProducts, query, order, limit, offset)

Returns all stock entries of the given product in order of next use (Opened first, then first due first, then first in first out)

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val productId : kotlin.Int = 56 // kotlin.Int | A valid product id
val includeSubProducts : kotlin.Boolean = true // kotlin.Boolean | If sub products should be included (if the given product is a parent product and in addition to the ones of the given product)
val query : kotlin.collections.List<kotlin.String> =  // kotlin.collections.List<kotlin.String> | An array of filter conditions, each of them is a string in the form of `<field><condition><value>` where<br>`<field>` is a valid field name<br>`<condition>` is a comparison operator, one of<br>&nbsp;&nbsp;`=` equal<br>&nbsp;&nbsp;`!=` not equal<br>&nbsp;&nbsp;`~` LIKE<br>&nbsp;&nbsp;`!~` not LIKE<br>&nbsp;&nbsp;`<` less<br>&nbsp;&nbsp;`>` greater<br>&nbsp;&nbsp;`<=` less or equal<br>&nbsp;&nbsp;`>=` greater or equal<br>&nbsp;&nbsp;`§` regular expression<br>`<value>` is the value to search for
val order : kotlin.String = order_example // kotlin.String | A valid field name by which the response should be ordered, use the separator `:` to specify the sort order (`asc` or `desc`, defaults to `asc` when omitted)
val limit : kotlin.Int = 56 // kotlin.Int | The maximum number of objects to return
val offset : kotlin.Int = 56 // kotlin.Int | The number of objects to skip
try {
    val result : kotlin.collections.List<StockEntry> = apiInstance.stockProductsProductIdEntriesGet(productId, includeSubProducts, query, order, limit, offset)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockProductsProductIdEntriesGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockProductsProductIdEntriesGet")
    e.printStackTrace()
}
```

### Parameters
| **productId** | **kotlin.Int**| A valid product id | |
| **includeSubProducts** | **kotlin.Boolean**| If sub products should be included (if the given product is a parent product and in addition to the ones of the given product) | [optional] |
| **query** | [**kotlin.collections.List&lt;kotlin.String&gt;**](kotlin.String.md)| An array of filter conditions, each of them is a string in the form of &#x60;&lt;field&gt;&lt;condition&gt;&lt;value&gt;&#x60; where&lt;br&gt;&#x60;&lt;field&gt;&#x60; is a valid field name&lt;br&gt;&#x60;&lt;condition&gt;&#x60; is a comparison operator, one of&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&#x3D;&#x60; equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;!&#x3D;&#x60; not equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;~&#x60; LIKE&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;!~&#x60; not LIKE&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&lt;&#x60; less&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&gt;&#x60; greater&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&lt;&#x3D;&#x60; less or equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&gt;&#x3D;&#x60; greater or equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;§&#x60; regular expression&lt;br&gt;&#x60;&lt;value&gt;&#x60; is the value to search for | [optional] |
| **order** | **kotlin.String**| A valid field name by which the response should be ordered, use the separator &#x60;:&#x60; to specify the sort order (&#x60;asc&#x60; or &#x60;desc&#x60;, defaults to &#x60;asc&#x60; when omitted) | [optional] |
| **limit** | **kotlin.Int**| The maximum number of objects to return | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **offset** | **kotlin.Int**| The number of objects to skip | [optional] |

### Return type

[**kotlin.collections.List&lt;StockEntry&gt;**](StockEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockProductsProductIdGet"></a>
# **stockProductsProductIdGet**
> ProductDetailsResponse stockProductsProductIdGet(productId)

Returns details of the given product

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val productId : kotlin.Int = 56 // kotlin.Int | A valid product id
try {
    val result : ProductDetailsResponse = apiInstance.stockProductsProductIdGet(productId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockProductsProductIdGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockProductsProductIdGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **productId** | **kotlin.Int**| A valid product id | |

### Return type

[**ProductDetailsResponse**](ProductDetailsResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockProductsProductIdInventoryPost"></a>
# **stockProductsProductIdInventoryPost**
> kotlin.collections.List&lt;StockLogEntry&gt; stockProductsProductIdInventoryPost(productId, stockProductsProductIdInventoryPostRequest)

Inventories the given product (adds/removes based on the given new amount)

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val productId : kotlin.Int = 56 // kotlin.Int | A valid product id
val stockProductsProductIdInventoryPostRequest : StockProductsProductIdInventoryPostRequest =  // StockProductsProductIdInventoryPostRequest | 
try {
    val result : kotlin.collections.List<StockLogEntry> = apiInstance.stockProductsProductIdInventoryPost(productId, stockProductsProductIdInventoryPostRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockProductsProductIdInventoryPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockProductsProductIdInventoryPost")
    e.printStackTrace()
}
```

### Parameters
| **productId** | **kotlin.Int**| A valid product id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockProductsProductIdInventoryPostRequest** | [**StockProductsProductIdInventoryPostRequest**](StockProductsProductIdInventoryPostRequest.md)|  | |

### Return type

[**kotlin.collections.List&lt;StockLogEntry&gt;**](StockLogEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockProductsProductIdLocationsGet"></a>
# **stockProductsProductIdLocationsGet**
> kotlin.collections.List&lt;StockLocation&gt; stockProductsProductIdLocationsGet(productId, includeSubProducts, query, order, limit, offset)

Returns all locations where the given product currently has stock

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val productId : kotlin.Int = 56 // kotlin.Int | A valid product id
val includeSubProducts : kotlin.Boolean = true // kotlin.Boolean | If sub product locations should be included (if the given product is a parent product and in addition to the ones of the given product)
val query : kotlin.collections.List<kotlin.String> =  // kotlin.collections.List<kotlin.String> | An array of filter conditions, each of them is a string in the form of `<field><condition><value>` where<br>`<field>` is a valid field name<br>`<condition>` is a comparison operator, one of<br>&nbsp;&nbsp;`=` equal<br>&nbsp;&nbsp;`!=` not equal<br>&nbsp;&nbsp;`~` LIKE<br>&nbsp;&nbsp;`!~` not LIKE<br>&nbsp;&nbsp;`<` less<br>&nbsp;&nbsp;`>` greater<br>&nbsp;&nbsp;`<=` less or equal<br>&nbsp;&nbsp;`>=` greater or equal<br>&nbsp;&nbsp;`§` regular expression<br>`<value>` is the value to search for
val order : kotlin.String = order_example // kotlin.String | A valid field name by which the response should be ordered, use the separator `:` to specify the sort order (`asc` or `desc`, defaults to `asc` when omitted)
val limit : kotlin.Int = 56 // kotlin.Int | The maximum number of objects to return
val offset : kotlin.Int = 56 // kotlin.Int | The number of objects to skip
try {
    val result : kotlin.collections.List<StockLocation> = apiInstance.stockProductsProductIdLocationsGet(productId, includeSubProducts, query, order, limit, offset)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockProductsProductIdLocationsGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockProductsProductIdLocationsGet")
    e.printStackTrace()
}
```

### Parameters
| **productId** | **kotlin.Int**| A valid product id | |
| **includeSubProducts** | **kotlin.Boolean**| If sub product locations should be included (if the given product is a parent product and in addition to the ones of the given product) | [optional] |
| **query** | [**kotlin.collections.List&lt;kotlin.String&gt;**](kotlin.String.md)| An array of filter conditions, each of them is a string in the form of &#x60;&lt;field&gt;&lt;condition&gt;&lt;value&gt;&#x60; where&lt;br&gt;&#x60;&lt;field&gt;&#x60; is a valid field name&lt;br&gt;&#x60;&lt;condition&gt;&#x60; is a comparison operator, one of&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&#x3D;&#x60; equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;!&#x3D;&#x60; not equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;~&#x60; LIKE&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;!~&#x60; not LIKE&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&lt;&#x60; less&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&gt;&#x60; greater&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&lt;&#x3D;&#x60; less or equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&gt;&#x3D;&#x60; greater or equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;§&#x60; regular expression&lt;br&gt;&#x60;&lt;value&gt;&#x60; is the value to search for | [optional] |
| **order** | **kotlin.String**| A valid field name by which the response should be ordered, use the separator &#x60;:&#x60; to specify the sort order (&#x60;asc&#x60; or &#x60;desc&#x60;, defaults to &#x60;asc&#x60; when omitted) | [optional] |
| **limit** | **kotlin.Int**| The maximum number of objects to return | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **offset** | **kotlin.Int**| The number of objects to skip | [optional] |

### Return type

[**kotlin.collections.List&lt;StockLocation&gt;**](StockLocation.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockProductsProductIdOpenPost"></a>
# **stockProductsProductIdOpenPost**
> kotlin.collections.List&lt;StockLogEntry&gt; stockProductsProductIdOpenPost(productId, stockProductsProductIdOpenPostRequest)

Marks the given amount of the given product as opened

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val productId : kotlin.Int = 56 // kotlin.Int | A valid product id
val stockProductsProductIdOpenPostRequest : StockProductsProductIdOpenPostRequest =  // StockProductsProductIdOpenPostRequest | 
try {
    val result : kotlin.collections.List<StockLogEntry> = apiInstance.stockProductsProductIdOpenPost(productId, stockProductsProductIdOpenPostRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockProductsProductIdOpenPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockProductsProductIdOpenPost")
    e.printStackTrace()
}
```

### Parameters
| **productId** | **kotlin.Int**| A valid product id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockProductsProductIdOpenPostRequest** | [**StockProductsProductIdOpenPostRequest**](StockProductsProductIdOpenPostRequest.md)|  | |

### Return type

[**kotlin.collections.List&lt;StockLogEntry&gt;**](StockLogEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockProductsProductIdPriceHistoryGet"></a>
# **stockProductsProductIdPriceHistoryGet**
> kotlin.collections.List&lt;ProductPriceHistory&gt; stockProductsProductIdPriceHistoryGet(productId)

Returns the price history of the given product

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val productId : kotlin.Int = 56 // kotlin.Int | A valid product id
try {
    val result : kotlin.collections.List<ProductPriceHistory> = apiInstance.stockProductsProductIdPriceHistoryGet(productId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockProductsProductIdPriceHistoryGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockProductsProductIdPriceHistoryGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **productId** | **kotlin.Int**| A valid product id | |

### Return type

[**kotlin.collections.List&lt;ProductPriceHistory&gt;**](ProductPriceHistory.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockProductsProductIdPrintlabelGet"></a>
# **stockProductsProductIdPrintlabelGet**
> kotlin.String stockProductsProductIdPrintlabelGet(productId)

Prints the Grocycode label of the given product on the configured label printer

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val productId : kotlin.Int = 56 // kotlin.Int | A valid product id
try {
    val result : kotlin.String = apiInstance.stockProductsProductIdPrintlabelGet(productId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockProductsProductIdPrintlabelGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockProductsProductIdPrintlabelGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **productId** | **kotlin.Int**| A valid product id | |

### Return type

**kotlin.String**

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockProductsProductIdToKeepMergeProductIdToRemovePost"></a>
# **stockProductsProductIdToKeepMergeProductIdToRemovePost**
> stockProductsProductIdToKeepMergeProductIdToRemovePost(productIdToKeep, productIdToRemove)

Merges two products into one

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val productIdToKeep : kotlin.Int = 56 // kotlin.Int | A valid product id of the product to keep
val productIdToRemove : kotlin.Int = 56 // kotlin.Int | A valid product id of the product to remove
try {
    apiInstance.stockProductsProductIdToKeepMergeProductIdToRemovePost(productIdToKeep, productIdToRemove)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockProductsProductIdToKeepMergeProductIdToRemovePost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockProductsProductIdToKeepMergeProductIdToRemovePost")
    e.printStackTrace()
}
```

### Parameters
| **productIdToKeep** | **kotlin.Int**| A valid product id of the product to keep | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **productIdToRemove** | **kotlin.Int**| A valid product id of the product to remove | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockProductsProductIdTransferPost"></a>
# **stockProductsProductIdTransferPost**
> kotlin.collections.List&lt;StockLogEntry&gt; stockProductsProductIdTransferPost(productId, stockProductsProductIdTransferPostRequest)

Transfers the given amount of the given product from one location to another (this is currently not supported for tare weight handling enabled products)

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val productId : kotlin.Int = 56 // kotlin.Int | A valid product id
val stockProductsProductIdTransferPostRequest : StockProductsProductIdTransferPostRequest =  // StockProductsProductIdTransferPostRequest | 
try {
    val result : kotlin.collections.List<StockLogEntry> = apiInstance.stockProductsProductIdTransferPost(productId, stockProductsProductIdTransferPostRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockProductsProductIdTransferPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockProductsProductIdTransferPost")
    e.printStackTrace()
}
```

### Parameters
| **productId** | **kotlin.Int**| A valid product id | |
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

<a id="stockShoppinglistAddExpiredProductsPost"></a>
# **stockShoppinglistAddExpiredProductsPost**
> stockShoppinglistAddExpiredProductsPost(stockShoppinglistAddMissingProductsPostRequest)

Adds expired products to the given shopping list

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val stockShoppinglistAddMissingProductsPostRequest : StockShoppinglistAddMissingProductsPostRequest =  // StockShoppinglistAddMissingProductsPostRequest | 
try {
    apiInstance.stockShoppinglistAddExpiredProductsPost(stockShoppinglistAddMissingProductsPostRequest)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockShoppinglistAddExpiredProductsPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockShoppinglistAddExpiredProductsPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockShoppinglistAddMissingProductsPostRequest** | [**StockShoppinglistAddMissingProductsPostRequest**](StockShoppinglistAddMissingProductsPostRequest.md)|  | [optional] |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockShoppinglistAddMissingProductsPost"></a>
# **stockShoppinglistAddMissingProductsPost**
> stockShoppinglistAddMissingProductsPost(stockShoppinglistAddMissingProductsPostRequest)

Adds currently missing products (below defined min. stock amount) to the given shopping list

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val stockShoppinglistAddMissingProductsPostRequest : StockShoppinglistAddMissingProductsPostRequest =  // StockShoppinglistAddMissingProductsPostRequest | 
try {
    apiInstance.stockShoppinglistAddMissingProductsPost(stockShoppinglistAddMissingProductsPostRequest)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockShoppinglistAddMissingProductsPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockShoppinglistAddMissingProductsPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockShoppinglistAddMissingProductsPostRequest** | [**StockShoppinglistAddMissingProductsPostRequest**](StockShoppinglistAddMissingProductsPostRequest.md)|  | [optional] |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockShoppinglistAddOverdueProductsPost"></a>
# **stockShoppinglistAddOverdueProductsPost**
> stockShoppinglistAddOverdueProductsPost(stockShoppinglistAddMissingProductsPostRequest)

Adds overdue products to the given shopping list

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val stockShoppinglistAddMissingProductsPostRequest : StockShoppinglistAddMissingProductsPostRequest =  // StockShoppinglistAddMissingProductsPostRequest | 
try {
    apiInstance.stockShoppinglistAddOverdueProductsPost(stockShoppinglistAddMissingProductsPostRequest)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockShoppinglistAddOverdueProductsPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockShoppinglistAddOverdueProductsPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockShoppinglistAddMissingProductsPostRequest** | [**StockShoppinglistAddMissingProductsPostRequest**](StockShoppinglistAddMissingProductsPostRequest.md)|  | [optional] |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockShoppinglistAddProductPost"></a>
# **stockShoppinglistAddProductPost**
> stockShoppinglistAddProductPost(stockShoppinglistAddProductPostRequest)

Adds the given amount of the given product to the given shopping list

If the product is already on the shopping list, the given amount will increase the amount of the already existing item, otherwise a new item will be added

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val stockShoppinglistAddProductPostRequest : StockShoppinglistAddProductPostRequest =  // StockShoppinglistAddProductPostRequest | 
try {
    apiInstance.stockShoppinglistAddProductPost(stockShoppinglistAddProductPostRequest)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockShoppinglistAddProductPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockShoppinglistAddProductPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockShoppinglistAddProductPostRequest** | [**StockShoppinglistAddProductPostRequest**](StockShoppinglistAddProductPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockShoppinglistClearPost"></a>
# **stockShoppinglistClearPost**
> stockShoppinglistClearPost(stockShoppinglistClearPostRequest)

Removes all items from the given shopping list

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val stockShoppinglistClearPostRequest : StockShoppinglistClearPostRequest =  // StockShoppinglistClearPostRequest | 
try {
    apiInstance.stockShoppinglistClearPost(stockShoppinglistClearPostRequest)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockShoppinglistClearPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockShoppinglistClearPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockShoppinglistClearPostRequest** | [**StockShoppinglistClearPostRequest**](StockShoppinglistClearPostRequest.md)|  | [optional] |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockShoppinglistRemoveProductPost"></a>
# **stockShoppinglistRemoveProductPost**
> stockShoppinglistRemoveProductPost(stockShoppinglistRemoveProductPostRequest)

Removes the given amount of the given product from the given shopping list, if it is on it

If the resulting amount is &lt;&#x3D; 0, the item will be completely removed from the given list, otherwise the given amount will reduce the amount of the existing item

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val stockShoppinglistRemoveProductPostRequest : StockShoppinglistRemoveProductPostRequest =  // StockShoppinglistRemoveProductPostRequest | 
try {
    apiInstance.stockShoppinglistRemoveProductPost(stockShoppinglistRemoveProductPostRequest)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockShoppinglistRemoveProductPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockShoppinglistRemoveProductPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **stockShoppinglistRemoveProductPostRequest** | [**StockShoppinglistRemoveProductPostRequest**](StockShoppinglistRemoveProductPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="stockTransactionsTransactionIdGet"></a>
# **stockTransactionsTransactionIdGet**
> kotlin.collections.List&lt;StockLogEntry&gt; stockTransactionsTransactionIdGet(transactionId)

Returns all stock bookings of the given transaction id

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val transactionId : kotlin.String = transactionId_example // kotlin.String | A valid stock transaction id
try {
    val result : kotlin.collections.List<StockLogEntry> = apiInstance.stockTransactionsTransactionIdGet(transactionId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockTransactionsTransactionIdGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockTransactionsTransactionIdGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **transactionId** | **kotlin.String**| A valid stock transaction id | |

### Return type

[**kotlin.collections.List&lt;StockLogEntry&gt;**](StockLogEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockTransactionsTransactionIdUndoPost"></a>
# **stockTransactionsTransactionIdUndoPost**
> stockTransactionsTransactionIdUndoPost(transactionId)

Undoes a transaction

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val transactionId : kotlin.String = transactionId_example // kotlin.String | A valid stock transaction id
try {
    apiInstance.stockTransactionsTransactionIdUndoPost(transactionId)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockTransactionsTransactionIdUndoPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockTransactionsTransactionIdUndoPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **transactionId** | **kotlin.String**| A valid stock transaction id | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="stockVolatileGet"></a>
# **stockVolatileGet**
> CurrentVolatilStockResponse stockVolatileGet(dueSoonDays)

Returns all products which are due soon, overdue, expired or currently missing

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = StockApi()
val dueSoonDays : kotlin.Int = 56 // kotlin.Int | The number of days in which products are considered to be due soon
try {
    val result : CurrentVolatilStockResponse = apiInstance.stockVolatileGet(dueSoonDays)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling StockApi#stockVolatileGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling StockApi#stockVolatileGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **dueSoonDays** | **kotlin.Int**| The number of days in which products are considered to be due soon | [optional] [default to 5] |

### Return type

[**CurrentVolatilStockResponse**](CurrentVolatilStockResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

