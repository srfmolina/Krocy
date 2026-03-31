# GenericEntityInteractionsApi

All URIs are relative to *http://xxx*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**objectsEntityGet**](GenericEntityInteractionsApi.md#objectsEntityGet) | **GET** /objects/{entity} | Returns all objects of the given entity |
| [**objectsEntityObjectIdDelete**](GenericEntityInteractionsApi.md#objectsEntityObjectIdDelete) | **DELETE** /objects/{entity}/{objectId} | Deletes a single object of the given entity |
| [**objectsEntityObjectIdGet**](GenericEntityInteractionsApi.md#objectsEntityObjectIdGet) | **GET** /objects/{entity}/{objectId} | Returns a single object of the given entity |
| [**objectsEntityObjectIdPut**](GenericEntityInteractionsApi.md#objectsEntityObjectIdPut) | **PUT** /objects/{entity}/{objectId} | Edits the given object of the given entity |
| [**objectsEntityPost**](GenericEntityInteractionsApi.md#objectsEntityPost) | **POST** /objects/{entity} | Adds a single object of the given entity |
| [**userfieldsEntityObjectIdGet**](GenericEntityInteractionsApi.md#userfieldsEntityObjectIdGet) | **GET** /userfields/{entity}/{objectId} | Returns all userfields with their values of the given object of the given entity |
| [**userfieldsEntityObjectIdPut**](GenericEntityInteractionsApi.md#userfieldsEntityObjectIdPut) | **PUT** /userfields/{entity}/{objectId} | Edits the given userfields of the given object of the given entity |


<a id="objectsEntityGet"></a>
# **objectsEntityGet**
> kotlin.collections.List&lt;ObjectsEntityGet200ResponseInner&gt; objectsEntityGet(entity, query, order, limit, offset)

Returns all objects of the given entity

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = GenericEntityInteractionsApi()
val entity : ExposedEntityNotIncludingNotListable =  // ExposedEntityNotIncludingNotListable | A valid entity name
val query : kotlin.collections.List<kotlin.String> =  // kotlin.collections.List<kotlin.String> | An array of filter conditions, each of them is a string in the form of `<field><condition><value>` where<br>`<field>` is a valid field name<br>`<condition>` is a comparison operator, one of<br>&nbsp;&nbsp;`=` equal<br>&nbsp;&nbsp;`!=` not equal<br>&nbsp;&nbsp;`~` LIKE<br>&nbsp;&nbsp;`!~` not LIKE<br>&nbsp;&nbsp;`<` less<br>&nbsp;&nbsp;`>` greater<br>&nbsp;&nbsp;`<=` less or equal<br>&nbsp;&nbsp;`>=` greater or equal<br>&nbsp;&nbsp;`§` regular expression<br>`<value>` is the value to search for
val order : kotlin.String = order_example // kotlin.String | A valid field name by which the response should be ordered, use the separator `:` to specify the sort order (`asc` or `desc`, defaults to `asc` when omitted)
val limit : kotlin.Int = 56 // kotlin.Int | The maximum number of objects to return
val offset : kotlin.Int = 56 // kotlin.Int | The number of objects to skip
try {
    val result : kotlin.collections.List<ObjectsEntityGet200ResponseInner> = apiInstance.objectsEntityGet(entity, query, order, limit, offset)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling GenericEntityInteractionsApi#objectsEntityGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling GenericEntityInteractionsApi#objectsEntityGet")
    e.printStackTrace()
}
```

### Parameters
| **entity** | [**ExposedEntityNotIncludingNotListable**](.md)| A valid entity name | |
| **query** | [**kotlin.collections.List&lt;kotlin.String&gt;**](kotlin.String.md)| An array of filter conditions, each of them is a string in the form of &#x60;&lt;field&gt;&lt;condition&gt;&lt;value&gt;&#x60; where&lt;br&gt;&#x60;&lt;field&gt;&#x60; is a valid field name&lt;br&gt;&#x60;&lt;condition&gt;&#x60; is a comparison operator, one of&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&#x3D;&#x60; equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;!&#x3D;&#x60; not equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;~&#x60; LIKE&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;!~&#x60; not LIKE&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&lt;&#x60; less&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&gt;&#x60; greater&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&lt;&#x3D;&#x60; less or equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;&gt;&#x3D;&#x60; greater or equal&lt;br&gt;&amp;nbsp;&amp;nbsp;&#x60;§&#x60; regular expression&lt;br&gt;&#x60;&lt;value&gt;&#x60; is the value to search for | [optional] |
| **order** | **kotlin.String**| A valid field name by which the response should be ordered, use the separator &#x60;:&#x60; to specify the sort order (&#x60;asc&#x60; or &#x60;desc&#x60;, defaults to &#x60;asc&#x60; when omitted) | [optional] |
| **limit** | **kotlin.Int**| The maximum number of objects to return | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **offset** | **kotlin.Int**| The number of objects to skip | [optional] |

### Return type

[**kotlin.collections.List&lt;ObjectsEntityGet200ResponseInner&gt;**](ObjectsEntityGet200ResponseInner.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="objectsEntityObjectIdDelete"></a>
# **objectsEntityObjectIdDelete**
> objectsEntityObjectIdDelete(entity, objectId)

Deletes a single object of the given entity

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = GenericEntityInteractionsApi()
val entity : ExposedEntityNotIncludingNotDeletable =  // ExposedEntityNotIncludingNotDeletable | A valid entity name
val objectId : kotlin.Int = 56 // kotlin.Int | A valid object id of the given entity
try {
    apiInstance.objectsEntityObjectIdDelete(entity, objectId)
} catch (e: ClientException) {
    println("4xx response calling GenericEntityInteractionsApi#objectsEntityObjectIdDelete")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling GenericEntityInteractionsApi#objectsEntityObjectIdDelete")
    e.printStackTrace()
}
```

### Parameters
| **entity** | [**ExposedEntityNotIncludingNotDeletable**](.md)| A valid entity name | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **objectId** | **kotlin.Int**| A valid object id of the given entity | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="objectsEntityObjectIdGet"></a>
# **objectsEntityObjectIdGet**
> ObjectsEntityGet200ResponseInner objectsEntityObjectIdGet(entity, objectId)

Returns a single object of the given entity

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = GenericEntityInteractionsApi()
val entity : ExposedEntityNotIncludingNotListable =  // ExposedEntityNotIncludingNotListable | A valid entity name
val objectId : kotlin.Int = 56 // kotlin.Int | A valid object id of the given entity
try {
    val result : ObjectsEntityGet200ResponseInner = apiInstance.objectsEntityObjectIdGet(entity, objectId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling GenericEntityInteractionsApi#objectsEntityObjectIdGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling GenericEntityInteractionsApi#objectsEntityObjectIdGet")
    e.printStackTrace()
}
```

### Parameters
| **entity** | [**ExposedEntityNotIncludingNotListable**](.md)| A valid entity name | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **objectId** | **kotlin.Int**| A valid object id of the given entity | |

### Return type

[**ObjectsEntityGet200ResponseInner**](ObjectsEntityGet200ResponseInner.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="objectsEntityObjectIdPut"></a>
# **objectsEntityObjectIdPut**
> objectsEntityObjectIdPut(entity, objectId, objectsEntityGet200ResponseInner)

Edits the given object of the given entity

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = GenericEntityInteractionsApi()
val entity : ExposedEntityNotIncludingNotEditable =  // ExposedEntityNotIncludingNotEditable | A valid entity name
val objectId : kotlin.Int = 56 // kotlin.Int | A valid object id of the given entity
val objectsEntityGet200ResponseInner : ObjectsEntityGet200ResponseInner =  // ObjectsEntityGet200ResponseInner | A valid entity object of the entity specified in parameter *entity*
try {
    apiInstance.objectsEntityObjectIdPut(entity, objectId, objectsEntityGet200ResponseInner)
} catch (e: ClientException) {
    println("4xx response calling GenericEntityInteractionsApi#objectsEntityObjectIdPut")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling GenericEntityInteractionsApi#objectsEntityObjectIdPut")
    e.printStackTrace()
}
```

### Parameters
| **entity** | [**ExposedEntityNotIncludingNotEditable**](.md)| A valid entity name | |
| **objectId** | **kotlin.Int**| A valid object id of the given entity | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **objectsEntityGet200ResponseInner** | [**ObjectsEntityGet200ResponseInner**](ObjectsEntityGet200ResponseInner.md)| A valid entity object of the entity specified in parameter *entity* | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="objectsEntityPost"></a>
# **objectsEntityPost**
> ObjectsEntityPost200Response objectsEntityPost(entity, objectsEntityGet200ResponseInner)

Adds a single object of the given entity

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = GenericEntityInteractionsApi()
val entity : ExposedEntityNotIncludingNotEditable =  // ExposedEntityNotIncludingNotEditable | A valid entity name
val objectsEntityGet200ResponseInner : ObjectsEntityGet200ResponseInner =  // ObjectsEntityGet200ResponseInner | A valid entity object of the entity specified in parameter *entity*
try {
    val result : ObjectsEntityPost200Response = apiInstance.objectsEntityPost(entity, objectsEntityGet200ResponseInner)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling GenericEntityInteractionsApi#objectsEntityPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling GenericEntityInteractionsApi#objectsEntityPost")
    e.printStackTrace()
}
```

### Parameters
| **entity** | [**ExposedEntityNotIncludingNotEditable**](.md)| A valid entity name | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **objectsEntityGet200ResponseInner** | [**ObjectsEntityGet200ResponseInner**](ObjectsEntityGet200ResponseInner.md)| A valid entity object of the entity specified in parameter *entity* | |

### Return type

[**ObjectsEntityPost200Response**](ObjectsEntityPost200Response.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="userfieldsEntityObjectIdGet"></a>
# **userfieldsEntityObjectIdGet**
> kotlin.String userfieldsEntityObjectIdGet(entity, objectId)

Returns all userfields with their values of the given object of the given entity

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = GenericEntityInteractionsApi()
val entity : ExposedEntityIncludingUserEntities =  // ExposedEntityIncludingUserEntities | A valid entity name
val objectId : kotlin.String = objectId_example // kotlin.String | A valid object id of the given entity
try {
    val result : kotlin.String = apiInstance.userfieldsEntityObjectIdGet(entity, objectId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling GenericEntityInteractionsApi#userfieldsEntityObjectIdGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling GenericEntityInteractionsApi#userfieldsEntityObjectIdGet")
    e.printStackTrace()
}
```

### Parameters
| **entity** | [**ExposedEntityIncludingUserEntities**](.md)| A valid entity name | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **objectId** | **kotlin.String**| A valid object id of the given entity | |

### Return type

**kotlin.String**

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="userfieldsEntityObjectIdPut"></a>
# **userfieldsEntityObjectIdPut**
> userfieldsEntityObjectIdPut(entity, objectId, body)

Edits the given userfields of the given object of the given entity

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = GenericEntityInteractionsApi()
val entity : ExposedEntityIncludingUserEntitiesNotIncludingNotEditable =  // ExposedEntityIncludingUserEntitiesNotIncludingNotEditable | A valid entity name
val objectId : kotlin.String = objectId_example // kotlin.String | A valid object id of the given entity
val body : kotlin.Any =  // kotlin.Any | A valid entity object of the entity specified in parameter *entity*
try {
    apiInstance.userfieldsEntityObjectIdPut(entity, objectId, body)
} catch (e: ClientException) {
    println("4xx response calling GenericEntityInteractionsApi#userfieldsEntityObjectIdPut")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling GenericEntityInteractionsApi#userfieldsEntityObjectIdPut")
    e.printStackTrace()
}
```

### Parameters
| **entity** | [**ExposedEntityIncludingUserEntitiesNotIncludingNotEditable**](.md)| A valid entity name | |
| **objectId** | **kotlin.String**| A valid object id of the given entity | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | **kotlin.Any**| A valid entity object of the entity specified in parameter *entity* | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

