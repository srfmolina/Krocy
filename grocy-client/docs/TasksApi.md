# TasksApi

All URIs are relative to *http://xxx*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**tasksGet**](TasksApi.md#tasksGet) | **GET** /tasks | Returns all tasks which are not done yet |
| [**tasksTaskIdCompletePost**](TasksApi.md#tasksTaskIdCompletePost) | **POST** /tasks/{taskId}/complete | Marks the given task as completed |
| [**tasksTaskIdUndoPost**](TasksApi.md#tasksTaskIdUndoPost) | **POST** /tasks/{taskId}/undo | Marks the given task as not completed |


<a id="tasksGet"></a>
# **tasksGet**
> kotlin.collections.List&lt;CurrentTaskResponse&gt; tasksGet(query, order, limit, offset)

Returns all tasks which are not done yet

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = TasksApi()
val query : kotlin.collections.List<kotlin.String> =  // kotlin.collections.List<kotlin.String> | An array of filter conditions, each of them is a string in the form of `<field><condition><value>` where<br>`<field>` is a valid field name<br>`<condition>` is a comparison operator, one of<br>&nbsp;&nbsp;`=` equal<br>&nbsp;&nbsp;`!=` not equal<br>&nbsp;&nbsp;`~` LIKE<br>&nbsp;&nbsp;`!~` not LIKE<br>&nbsp;&nbsp;`<` less<br>&nbsp;&nbsp;`>` greater<br>&nbsp;&nbsp;`<=` less or equal<br>&nbsp;&nbsp;`>=` greater or equal<br>&nbsp;&nbsp;`§` regular expression<br>`<value>` is the value to search for
val order : kotlin.String = order_example // kotlin.String | A valid field name by which the response should be ordered, use the separator `:` to specify the sort order (`asc` or `desc`, defaults to `asc` when omitted)
val limit : kotlin.Int = 56 // kotlin.Int | The maximum number of objects to return
val offset : kotlin.Int = 56 // kotlin.Int | The number of objects to skip
try {
    val result : kotlin.collections.List<CurrentTaskResponse> = apiInstance.tasksGet(query, order, limit, offset)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling TasksApi#tasksGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling TasksApi#tasksGet")
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

[**kotlin.collections.List&lt;CurrentTaskResponse&gt;**](CurrentTaskResponse.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="tasksTaskIdCompletePost"></a>
# **tasksTaskIdCompletePost**
> tasksTaskIdCompletePost(taskId, tasksTaskIdCompletePostRequest)

Marks the given task as completed

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = TasksApi()
val taskId : kotlin.Int = 56 // kotlin.Int | A valid task id
val tasksTaskIdCompletePostRequest : TasksTaskIdCompletePostRequest =  // TasksTaskIdCompletePostRequest | 
try {
    apiInstance.tasksTaskIdCompletePost(taskId, tasksTaskIdCompletePostRequest)
} catch (e: ClientException) {
    println("4xx response calling TasksApi#tasksTaskIdCompletePost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling TasksApi#tasksTaskIdCompletePost")
    e.printStackTrace()
}
```

### Parameters
| **taskId** | **kotlin.Int**| A valid task id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **tasksTaskIdCompletePostRequest** | [**TasksTaskIdCompletePostRequest**](TasksTaskIdCompletePostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="tasksTaskIdUndoPost"></a>
# **tasksTaskIdUndoPost**
> tasksTaskIdUndoPost(taskId)

Marks the given task as not completed

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = TasksApi()
val taskId : kotlin.Int = 56 // kotlin.Int | A valid task id
try {
    apiInstance.tasksTaskIdUndoPost(taskId)
} catch (e: ClientException) {
    println("4xx response calling TasksApi#tasksTaskIdUndoPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling TasksApi#tasksTaskIdUndoPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **taskId** | **kotlin.Int**| A valid task id | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

