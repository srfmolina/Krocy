# UserManagementApi

All URIs are relative to *http://xxx*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**usersGet**](UserManagementApi.md#usersGet) | **GET** /users | Returns all users |
| [**usersPost**](UserManagementApi.md#usersPost) | **POST** /users | Creates a new user |
| [**usersUserIdDelete**](UserManagementApi.md#usersUserIdDelete) | **DELETE** /users/{userId} | Deletes the given user |
| [**usersUserIdPermissionsGet**](UserManagementApi.md#usersUserIdPermissionsGet) | **GET** /users/{userId}/permissions | Returns the assigned permissions of the given user |
| [**usersUserIdPermissionsPost**](UserManagementApi.md#usersUserIdPermissionsPost) | **POST** /users/{userId}/permissions | Adds a permission to the given user |
| [**usersUserIdPermissionsPut**](UserManagementApi.md#usersUserIdPermissionsPut) | **PUT** /users/{userId}/permissions | Replaces the assigned permissions of the given user |
| [**usersUserIdPut**](UserManagementApi.md#usersUserIdPut) | **PUT** /users/{userId} | Edits the given user |


<a id="usersGet"></a>
# **usersGet**
> kotlin.collections.List&lt;UserDto&gt; usersGet(query, order, limit, offset)

Returns all users

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = UserManagementApi()
val query : kotlin.collections.List<kotlin.String> =  // kotlin.collections.List<kotlin.String> | An array of filter conditions, each of them is a string in the form of `<field><condition><value>` where<br>`<field>` is a valid field name<br>`<condition>` is a comparison operator, one of<br>&nbsp;&nbsp;`=` equal<br>&nbsp;&nbsp;`!=` not equal<br>&nbsp;&nbsp;`~` LIKE<br>&nbsp;&nbsp;`!~` not LIKE<br>&nbsp;&nbsp;`<` less<br>&nbsp;&nbsp;`>` greater<br>&nbsp;&nbsp;`<=` less or equal<br>&nbsp;&nbsp;`>=` greater or equal<br>&nbsp;&nbsp;`§` regular expression<br>`<value>` is the value to search for
val order : kotlin.String = order_example // kotlin.String | A valid field name by which the response should be ordered, use the separator `:` to specify the sort order (`asc` or `desc`, defaults to `asc` when omitted)
val limit : kotlin.Int = 56 // kotlin.Int | The maximum number of objects to return
val offset : kotlin.Int = 56 // kotlin.Int | The number of objects to skip
try {
    val result : kotlin.collections.List<UserDto> = apiInstance.usersGet(query, order, limit, offset)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling UserManagementApi#usersGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling UserManagementApi#usersGet")
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

[**kotlin.collections.List&lt;UserDto&gt;**](UserDto.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="usersPost"></a>
# **usersPost**
> usersPost(user)

Creates a new user

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = UserManagementApi()
val user : User =  // User | A valid user object
try {
    apiInstance.usersPost(user)
} catch (e: ClientException) {
    println("4xx response calling UserManagementApi#usersPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling UserManagementApi#usersPost")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **user** | [**User**](User.md)| A valid user object | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="usersUserIdDelete"></a>
# **usersUserIdDelete**
> usersUserIdDelete(userId)

Deletes the given user

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = UserManagementApi()
val userId : kotlin.Int = 56 // kotlin.Int | A valid user id
try {
    apiInstance.usersUserIdDelete(userId)
} catch (e: ClientException) {
    println("4xx response calling UserManagementApi#usersUserIdDelete")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling UserManagementApi#usersUserIdDelete")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **userId** | **kotlin.Int**| A valid user id | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="usersUserIdPermissionsGet"></a>
# **usersUserIdPermissionsGet**
> kotlin.collections.List&lt;UsersUserIdPermissionsGet200ResponseInner&gt; usersUserIdPermissionsGet(userId)

Returns the assigned permissions of the given user

See \&quot;GET /objects/permission_hierarchy\&quot; for a permission name / id mapping

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = UserManagementApi()
val userId : kotlin.Int = 56 // kotlin.Int | A valid user id
try {
    val result : kotlin.collections.List<UsersUserIdPermissionsGet200ResponseInner> = apiInstance.usersUserIdPermissionsGet(userId)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling UserManagementApi#usersUserIdPermissionsGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling UserManagementApi#usersUserIdPermissionsGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **userId** | **kotlin.Int**| A valid user id | |

### Return type

[**kotlin.collections.List&lt;UsersUserIdPermissionsGet200ResponseInner&gt;**](UsersUserIdPermissionsGet200ResponseInner.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="usersUserIdPermissionsPost"></a>
# **usersUserIdPermissionsPost**
> usersUserIdPermissionsPost(userId, usersUserIdPermissionsPostRequest)

Adds a permission to the given user

See \&quot;GET /objects/permission_hierarchy\&quot; for a permission name / id mapping

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = UserManagementApi()
val userId : kotlin.Int = 56 // kotlin.Int | A valid user id
val usersUserIdPermissionsPostRequest : UsersUserIdPermissionsPostRequest =  // UsersUserIdPermissionsPostRequest | 
try {
    apiInstance.usersUserIdPermissionsPost(userId, usersUserIdPermissionsPostRequest)
} catch (e: ClientException) {
    println("4xx response calling UserManagementApi#usersUserIdPermissionsPost")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling UserManagementApi#usersUserIdPermissionsPost")
    e.printStackTrace()
}
```

### Parameters
| **userId** | **kotlin.Int**| A valid user id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **usersUserIdPermissionsPostRequest** | [**UsersUserIdPermissionsPostRequest**](UsersUserIdPermissionsPostRequest.md)|  | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="usersUserIdPermissionsPut"></a>
# **usersUserIdPermissionsPut**
> usersUserIdPermissionsPut(userId, usersUserIdPermissionsPutRequest)

Replaces the assigned permissions of the given user

See \&quot;GET /objects/permission_hierarchy\&quot; for a permission name / id mapping

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = UserManagementApi()
val userId : kotlin.Int = 56 // kotlin.Int | A valid user id
val usersUserIdPermissionsPutRequest : UsersUserIdPermissionsPutRequest =  // UsersUserIdPermissionsPutRequest | 
try {
    apiInstance.usersUserIdPermissionsPut(userId, usersUserIdPermissionsPutRequest)
} catch (e: ClientException) {
    println("4xx response calling UserManagementApi#usersUserIdPermissionsPut")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling UserManagementApi#usersUserIdPermissionsPut")
    e.printStackTrace()
}
```

### Parameters
| **userId** | **kotlin.Int**| A valid user id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **usersUserIdPermissionsPutRequest** | [**UsersUserIdPermissionsPutRequest**](UsersUserIdPermissionsPutRequest.md)|  | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a id="usersUserIdPut"></a>
# **usersUserIdPut**
> usersUserIdPut(userId, user)

Edits the given user

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = UserManagementApi()
val userId : kotlin.Int = 56 // kotlin.Int | A valid user id
val user : User =  // User | A valid user object
try {
    apiInstance.usersUserIdPut(userId, user)
} catch (e: ClientException) {
    println("4xx response calling UserManagementApi#usersUserIdPut")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling UserManagementApi#usersUserIdPut")
    e.printStackTrace()
}
```

### Parameters
| **userId** | **kotlin.Int**| A valid user id | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **user** | [**User**](User.md)| A valid user object | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

