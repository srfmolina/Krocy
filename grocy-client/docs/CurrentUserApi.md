# CurrentUserApi

All URIs are relative to *http://xxx*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**userGet**](CurrentUserApi.md#userGet) | **GET** /user | Returns the currently authenticated user |
| [**userSettingsGet**](CurrentUserApi.md#userSettingsGet) | **GET** /user/settings | Returns all settings of the currently logged in user |
| [**userSettingsSettingKeyDelete**](CurrentUserApi.md#userSettingsSettingKeyDelete) | **DELETE** /user/settings/{settingKey} | Deletes the given setting of the currently logged in user |
| [**userSettingsSettingKeyGet**](CurrentUserApi.md#userSettingsSettingKeyGet) | **GET** /user/settings/{settingKey} | Returns the given setting of the currently logged in user |
| [**userSettingsSettingKeyPut**](CurrentUserApi.md#userSettingsSettingKeyPut) | **PUT** /user/settings/{settingKey} | Sets the given setting of the currently logged in user |


<a id="userGet"></a>
# **userGet**
> kotlin.String userGet()

Returns the currently authenticated user

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = CurrentUserApi()
try {
    val result : kotlin.String = apiInstance.userGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CurrentUserApi#userGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CurrentUserApi#userGet")
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

<a id="userSettingsGet"></a>
# **userSettingsGet**
> kotlin.String userSettingsGet()

Returns all settings of the currently logged in user

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = CurrentUserApi()
try {
    val result : kotlin.String = apiInstance.userSettingsGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CurrentUserApi#userSettingsGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CurrentUserApi#userSettingsGet")
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

<a id="userSettingsSettingKeyDelete"></a>
# **userSettingsSettingKeyDelete**
> userSettingsSettingKeyDelete(settingKey)

Deletes the given setting of the currently logged in user

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = CurrentUserApi()
val settingKey : kotlin.String = settingKey_example // kotlin.String | The key of the user setting
try {
    apiInstance.userSettingsSettingKeyDelete(settingKey)
} catch (e: ClientException) {
    println("4xx response calling CurrentUserApi#userSettingsSettingKeyDelete")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CurrentUserApi#userSettingsSettingKeyDelete")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **settingKey** | **kotlin.String**| The key of the user setting | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="userSettingsSettingKeyGet"></a>
# **userSettingsSettingKeyGet**
> UserSetting userSettingsSettingKeyGet(settingKey)

Returns the given setting of the currently logged in user

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = CurrentUserApi()
val settingKey : kotlin.String = settingKey_example // kotlin.String | The key of the user setting
try {
    val result : UserSetting = apiInstance.userSettingsSettingKeyGet(settingKey)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CurrentUserApi#userSettingsSettingKeyGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CurrentUserApi#userSettingsSettingKeyGet")
    e.printStackTrace()
}
```

### Parameters
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **settingKey** | **kotlin.String**| The key of the user setting | |

### Return type

[**UserSetting**](UserSetting.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="userSettingsSettingKeyPut"></a>
# **userSettingsSettingKeyPut**
> userSettingsSettingKeyPut(settingKey, userSetting)

Sets the given setting of the currently logged in user

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = CurrentUserApi()
val settingKey : kotlin.String = settingKey_example // kotlin.String | The key of the user setting
val userSetting : UserSetting =  // UserSetting | A valid UserSetting object
try {
    apiInstance.userSettingsSettingKeyPut(settingKey, userSetting)
} catch (e: ClientException) {
    println("4xx response calling CurrentUserApi#userSettingsSettingKeyPut")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CurrentUserApi#userSettingsSettingKeyPut")
    e.printStackTrace()
}
```

### Parameters
| **settingKey** | **kotlin.String**| The key of the user setting | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **userSetting** | [**UserSetting**](UserSetting.md)| A valid UserSetting object | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

