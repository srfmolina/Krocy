# ChoresApi

All URIs are relative to *http://xxx*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**choresChoreIdExecutePost**](ChoresApi.md#choresChoreIdExecutePost) | **POST** /chores/{choreId}/execute | Tracks an execution of the given chore |
| [**choresChoreIdGet**](ChoresApi.md#choresChoreIdGet) | **GET** /chores/{choreId} | Returns details of the given chore |
| [**choresChoreIdPrintlabelGet**](ChoresApi.md#choresChoreIdPrintlabelGet) | **GET** /chores/{choreId}/printlabel | Prints the Grocycode label of the given chore on the configured label printer |
| [**choresChoreIdToKeepMergeChoreIdToRemovePost**](ChoresApi.md#choresChoreIdToKeepMergeChoreIdToRemovePost) | **POST** /chores/{choreIdToKeep}/merge/{choreIdToRemove} | Merges two chores into one |
| [**choresExecutionsCalculateNextAssignmentsPost**](ChoresApi.md#choresExecutionsCalculateNextAssignmentsPost) | **POST** /chores/executions/calculate-next-assignments | (Re)calculates all next user assignments of all chores |
| [**choresExecutionsExecutionIdUndoPost**](ChoresApi.md#choresExecutionsExecutionIdUndoPost) | **POST** /chores/executions/{executionId}/undo | Undoes a chore execution |
| [**choresGet**](ChoresApi.md#choresGet) | **GET** /chores | Returns all chores incl. the next estimated execution time per chore |


<a id="choresChoreIdExecutePost"></a>
# **choresChoreIdExecutePost**
> ChoreLogEntry choresChoreIdExecutePost(choreId, choresChoreIdExecutePostRequest)

Tracks an execution of the given chore

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = ChoresApi()
val choreId : kotlin.Int = 56 // kotlin.Int | A valid chore id
val choresChoreIdExecutePostRequest : ChoresChoreIdExecutePostRequest =  // ChoresChoreIdExecutePostRequest | 
try {
    val result : ChoreLogEntry = apiInstance.choresChoreIdExecutePost(choreId, choresChoreIdExecutePostRequest)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ChoresApi#choresChoreIdExecutePost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ChoresApi#choresChoreIdExecutePost")
    e.printStackTrace()
}
```

### Parameters
| **choreId** | **kotlin.Int**| A valid chore id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **choresChoreIdExecutePostRequest** | [**ChoresChoreIdExecutePostRequest**](ChoresChoreIdExecutePostRequest.md)|  | |

### Return type

[**ChoreLogEntry**](ChoreLogEntry.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="choresChoreIdGet"></a>
# **choresChoreIdGet**
> ChoreDetailsResponse choresChoreIdGet(choreId)

Returns details of the given chore

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = ChoresApi()
val choreId : kotlin.Int = 56 // kotlin.Int | A valid chore id
try {
    val result : ChoreDetailsResponse = apiInstance.choresChoreIdGet(choreId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ChoresApi#choresChoreIdGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ChoresApi#choresChoreIdGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **choreId** | **kotlin.Int**| A valid chore id | |

### Return type

[**ChoreDetailsResponse**](ChoreDetailsResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="choresChoreIdPrintlabelGet"></a>
# **choresChoreIdPrintlabelGet**
> kotlin.String choresChoreIdPrintlabelGet(choreId)

Prints the Grocycode label of the given chore on the configured label printer

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = ChoresApi()
val choreId : kotlin.Int = 56 // kotlin.Int | A valid chore id
try {
    val result : kotlin.String = apiInstance.choresChoreIdPrintlabelGet(choreId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ChoresApi#choresChoreIdPrintlabelGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ChoresApi#choresChoreIdPrintlabelGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **choreId** | **kotlin.Int**| A valid chore id | |

### Return type

**kotlin.String**

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="choresChoreIdToKeepMergeChoreIdToRemovePost"></a>
# **choresChoreIdToKeepMergeChoreIdToRemovePost**
> choresChoreIdToKeepMergeChoreIdToRemovePost(choreIdToKeep, choreIdToRemove)

Merges two chores into one

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = ChoresApi()
val choreIdToKeep : kotlin.Int = 56 // kotlin.Int | A valid chore id of the chore to keep
val choreIdToRemove : kotlin.Int = 56 // kotlin.Int | A valid chore id of the chore to remove
try {
    apiInstance.choresChoreIdToKeepMergeChoreIdToRemovePost(choreIdToKeep, choreIdToRemove)
} catch (e: ClientException) {
    println("4xx response calling ChoresApi#choresChoreIdToKeepMergeChoreIdToRemovePost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ChoresApi#choresChoreIdToKeepMergeChoreIdToRemovePost")
    e.printStackTrace()
}
```

### Parameters
| **choreIdToKeep** | **kotlin.Int**| A valid chore id of the chore to keep | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **choreIdToRemove** | **kotlin.Int**| A valid chore id of the chore to remove | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="choresExecutionsCalculateNextAssignmentsPost"></a>
# **choresExecutionsCalculateNextAssignmentsPost**
> choresExecutionsCalculateNextAssignmentsPost(choresExecutionsCalculateNextAssignmentsPostRequest)

(Re)calculates all next user assignments of all chores

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = ChoresApi()
val choresExecutionsCalculateNextAssignmentsPostRequest : ChoresExecutionsCalculateNextAssignmentsPostRequest =  // ChoresExecutionsCalculateNextAssignmentsPostRequest | 
try {
    apiInstance.choresExecutionsCalculateNextAssignmentsPost(choresExecutionsCalculateNextAssignmentsPostRequest)
} catch (e: ClientException) {
    println("4xx response calling ChoresApi#choresExecutionsCalculateNextAssignmentsPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ChoresApi#choresExecutionsCalculateNextAssignmentsPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **choresExecutionsCalculateNextAssignmentsPostRequest** | [**ChoresExecutionsCalculateNextAssignmentsPostRequest**](ChoresExecutionsCalculateNextAssignmentsPostRequest.md)|  | [optional] |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="choresExecutionsExecutionIdUndoPost"></a>
# **choresExecutionsExecutionIdUndoPost**
> choresExecutionsExecutionIdUndoPost(executionId)

Undoes a chore execution

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = ChoresApi()
val executionId : kotlin.Int = 56 // kotlin.Int | A valid chore execution id
try {
    apiInstance.choresExecutionsExecutionIdUndoPost(executionId)
} catch (e: ClientException) {
    println("4xx response calling ChoresApi#choresExecutionsExecutionIdUndoPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ChoresApi#choresExecutionsExecutionIdUndoPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **executionId** | **kotlin.Int**| A valid chore execution id | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="choresGet"></a>
# **choresGet**
> kotlin.collections.List&lt;CurrentChoreResponse&gt; choresGet(query, order, limit, offset)

Returns all chores incl. the next estimated execution time per chore

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = ChoresApi()
val query : kotlin.collections.List<kotlin.String> =  // kotlin.collections.List<kotlin.String> | An array of filter conditions, each of them is a string in the form of `<field><condition><value>` where<br>`<field>` is a valid field name<br>`<condition>` is a comparison operator, one of<br>&nbsp;&nbsp;`=` equal<br>&nbsp;&nbsp;`!=` not equal<br>&nbsp;&nbsp;`~` LIKE<br>&nbsp;&nbsp;`!~` not LIKE<br>&nbsp;&nbsp;`<` less<br>&nbsp;&nbsp;`>` greater<br>&nbsp;&nbsp;`<=` less or equal<br>&nbsp;&nbsp;`>=` greater or equal<br>&nbsp;&nbsp;`§` regular expression<br>`<value>` is the value to search for
val order : kotlin.String = order_example // kotlin.String | A valid field name by which the response should be ordered, use the separator `:` to specify the sort order (`asc` or `desc`, defaults to `asc` when omitted)
val limit : kotlin.Int = 56 // kotlin.Int | The maximum number of objects to return
val offset : kotlin.Int = 56 // kotlin.Int | The number of objects to skip
try {
    val result : kotlin.collections.List<CurrentChoreResponse> = apiInstance.choresGet(query, order, limit, offset)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling ChoresApi#choresGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling ChoresApi#choresGet")
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

[**kotlin.collections.List&lt;CurrentChoreResponse&gt;**](CurrentChoreResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

