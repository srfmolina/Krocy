# RecipesApi

All URIs are relative to *http://xxx*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**recipesFulfillmentGet**](RecipesApi.md#recipesFulfillmentGet) | **GET** /recipes/fulfillment | Get stock fulfillment information for all recipe |
| [**recipesRecipeIdAddNotFulfilledProductsToShoppinglistPost**](RecipesApi.md#recipesRecipeIdAddNotFulfilledProductsToShoppinglistPost) | **POST** /recipes/{recipeId}/add-not-fulfilled-products-to-shoppinglist | Adds all missing products for the given recipe to the shopping list |
| [**recipesRecipeIdConsumePost**](RecipesApi.md#recipesRecipeIdConsumePost) | **POST** /recipes/{recipeId}/consume | Consumes all in stock ingredients of the given recipe (for ingredients that are only partially in stock, the in stock amount will be consumed) |
| [**recipesRecipeIdCopyPost**](RecipesApi.md#recipesRecipeIdCopyPost) | **POST** /recipes/{recipeId}/copy | Copies a recipe |
| [**recipesRecipeIdFulfillmentGet**](RecipesApi.md#recipesRecipeIdFulfillmentGet) | **GET** /recipes/{recipeId}/fulfillment | Get stock fulfillment information for the given recipe |
| [**recipesRecipeIdPrintlabelGet**](RecipesApi.md#recipesRecipeIdPrintlabelGet) | **GET** /recipes/{recipeId}/printlabel | Prints the Grocycode label of the given recipe on the configured label printer |


<a id="recipesFulfillmentGet"></a>
# **recipesFulfillmentGet**
> kotlin.collections.List&lt;RecipeFulfillmentResponse&gt; recipesFulfillmentGet(query, order, limit, offset)

Get stock fulfillment information for all recipe

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = RecipesApi()
val query : kotlin.collections.List<kotlin.String> =  // kotlin.collections.List<kotlin.String> | An array of filter conditions, each of them is a string in the form of `<field><condition><value>` where<br>`<field>` is a valid field name<br>`<condition>` is a comparison operator, one of<br>&nbsp;&nbsp;`=` equal<br>&nbsp;&nbsp;`!=` not equal<br>&nbsp;&nbsp;`~` LIKE<br>&nbsp;&nbsp;`!~` not LIKE<br>&nbsp;&nbsp;`<` less<br>&nbsp;&nbsp;`>` greater<br>&nbsp;&nbsp;`<=` less or equal<br>&nbsp;&nbsp;`>=` greater or equal<br>&nbsp;&nbsp;`§` regular expression<br>`<value>` is the value to search for
val order : kotlin.String = order_example // kotlin.String | A valid field name by which the response should be ordered, use the separator `:` to specify the sort order (`asc` or `desc`, defaults to `asc` when omitted)
val limit : kotlin.Int = 56 // kotlin.Int | The maximum number of objects to return
val offset : kotlin.Int = 56 // kotlin.Int | The number of objects to skip
try {
    val result : kotlin.collections.List<RecipeFulfillmentResponse> = apiInstance.recipesFulfillmentGet(query, order, limit, offset)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling RecipesApi#recipesFulfillmentGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling RecipesApi#recipesFulfillmentGet")
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

[**kotlin.collections.List&lt;RecipeFulfillmentResponse&gt;**](RecipeFulfillmentResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="recipesRecipeIdAddNotFulfilledProductsToShoppinglistPost"></a>
# **recipesRecipeIdAddNotFulfilledProductsToShoppinglistPost**
> recipesRecipeIdAddNotFulfilledProductsToShoppinglistPost(recipeId, recipesRecipeIdAddNotFulfilledProductsToShoppinglistPostRequest)

Adds all missing products for the given recipe to the shopping list

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = RecipesApi()
val recipeId : kotlin.String = recipeId_example // kotlin.String | A valid recipe id
val recipesRecipeIdAddNotFulfilledProductsToShoppinglistPostRequest : RecipesRecipeIdAddNotFulfilledProductsToShoppinglistPostRequest =  // RecipesRecipeIdAddNotFulfilledProductsToShoppinglistPostRequest | 
try {
    apiInstance.recipesRecipeIdAddNotFulfilledProductsToShoppinglistPost(recipeId, recipesRecipeIdAddNotFulfilledProductsToShoppinglistPostRequest)
} catch (e: ClientException) {
    println("4xx response calling RecipesApi#recipesRecipeIdAddNotFulfilledProductsToShoppinglistPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling RecipesApi#recipesRecipeIdAddNotFulfilledProductsToShoppinglistPost")
    e.printStackTrace()
}
```

### Parameters
| **recipeId** | **kotlin.String**| A valid recipe id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **recipesRecipeIdAddNotFulfilledProductsToShoppinglistPostRequest** | [**RecipesRecipeIdAddNotFulfilledProductsToShoppinglistPostRequest**](RecipesRecipeIdAddNotFulfilledProductsToShoppinglistPostRequest.md)|  | [optional] |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

<a id="recipesRecipeIdConsumePost"></a>
# **recipesRecipeIdConsumePost**
> recipesRecipeIdConsumePost(recipeId)

Consumes all in stock ingredients of the given recipe (for ingredients that are only partially in stock, the in stock amount will be consumed)

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = RecipesApi()
val recipeId : kotlin.String = recipeId_example // kotlin.String | A valid recipe id
try {
    apiInstance.recipesRecipeIdConsumePost(recipeId)
} catch (e: ClientException) {
    println("4xx response calling RecipesApi#recipesRecipeIdConsumePost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling RecipesApi#recipesRecipeIdConsumePost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **recipeId** | **kotlin.String**| A valid recipe id | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="recipesRecipeIdCopyPost"></a>
# **recipesRecipeIdCopyPost**
> RecipesRecipeIdCopyPost200Response recipesRecipeIdCopyPost(recipeId)

Copies a recipe

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = RecipesApi()
val recipeId : kotlin.Int = 56 // kotlin.Int | A valid recipe id of the recipe to copy
try {
    val result : RecipesRecipeIdCopyPost200Response = apiInstance.recipesRecipeIdCopyPost(recipeId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling RecipesApi#recipesRecipeIdCopyPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling RecipesApi#recipesRecipeIdCopyPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **recipeId** | **kotlin.Int**| A valid recipe id of the recipe to copy | |

### Return type

[**RecipesRecipeIdCopyPost200Response**](RecipesRecipeIdCopyPost200Response.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="recipesRecipeIdFulfillmentGet"></a>
# **recipesRecipeIdFulfillmentGet**
> RecipeFulfillmentResponse recipesRecipeIdFulfillmentGet(recipeId)

Get stock fulfillment information for the given recipe

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = RecipesApi()
val recipeId : kotlin.String = recipeId_example // kotlin.String | A valid recipe id
try {
    val result : RecipeFulfillmentResponse = apiInstance.recipesRecipeIdFulfillmentGet(recipeId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling RecipesApi#recipesRecipeIdFulfillmentGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling RecipesApi#recipesRecipeIdFulfillmentGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **recipeId** | **kotlin.String**| A valid recipe id | |

### Return type

[**RecipeFulfillmentResponse**](RecipeFulfillmentResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="recipesRecipeIdPrintlabelGet"></a>
# **recipesRecipeIdPrintlabelGet**
> kotlin.String recipesRecipeIdPrintlabelGet(recipeId)

Prints the Grocycode label of the given recipe on the configured label printer

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = RecipesApi()
val recipeId : kotlin.Int = 56 // kotlin.Int | A valid recipe id
try {
    val result : kotlin.String = apiInstance.recipesRecipeIdPrintlabelGet(recipeId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling RecipesApi#recipesRecipeIdPrintlabelGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling RecipesApi#recipesRecipeIdPrintlabelGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **recipeId** | **kotlin.Int**| A valid recipe id | |

### Return type

**kotlin.String**

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

