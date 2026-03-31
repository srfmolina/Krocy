# FilesApi

All URIs are relative to *http://xxx*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**filesGroupFileNameDelete**](FilesApi.md#filesGroupFileNameDelete) | **DELETE** /files/{group}/{fileName} | Deletes the given file |
| [**filesGroupFileNameGet**](FilesApi.md#filesGroupFileNameGet) | **GET** /files/{group}/{fileName} | Serves the given file |
| [**filesGroupFileNamePut**](FilesApi.md#filesGroupFileNamePut) | **PUT** /files/{group}/{fileName} | Uploads a single file |


<a id="filesGroupFileNameDelete"></a>
# **filesGroupFileNameDelete**
> filesGroupFileNameDelete(group, fileName)

Deletes the given file

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = FilesApi()
val group : FileGroups =  // FileGroups | The file group
val fileName : kotlin.String = fileName_example // kotlin.String | The file name (including extension)<br>**BASE64 encoded**
try {
    apiInstance.filesGroupFileNameDelete(group, fileName)
} catch (e: ClientException) {
    println("4xx response calling FilesApi#filesGroupFileNameDelete")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling FilesApi#filesGroupFileNameDelete")
    e.printStackTrace()
}
```

### Parameters
| **group** | [**FileGroups**](.md)| The file group | [enum: equipmentmanuals, recipepictures, productpictures, userfiles, userpictures] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **fileName** | **kotlin.String**| The file name (including extension)&lt;br&gt;**BASE64 encoded** | |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a id="filesGroupFileNameGet"></a>
# **filesGroupFileNameGet**
> org.openapitools.client.infrastructure.OctetByteArray filesGroupFileNameGet(group, fileName, forceServeAs, bestFitHeight, bestFitWidth)

Serves the given file

With proper Content-Type header

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = FilesApi()
val group : FileGroups =  // FileGroups | The file group
val fileName : kotlin.String = fileName_example // kotlin.String | The file name (including extension)<br>**BASE64 encoded**
val forceServeAs : kotlin.String = forceServeAs_example // kotlin.String | Force the file to be served as the given type
val bestFitHeight : kotlin.Double = 8.14 // kotlin.Double | Only when using `force_serve_as` = `picture`: Downscale the picture to the given height while maintaining the aspect ratio
val bestFitWidth : kotlin.Double = 8.14 // kotlin.Double | Only when using `force_serve_as` = `picture`: Downscale the picture to the given width while maintaining the aspect ratio
try {
    val result : org.openapitools.client.infrastructure.OctetByteArray = apiInstance.filesGroupFileNameGet(group, fileName, forceServeAs, bestFitHeight, bestFitWidth)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling FilesApi#filesGroupFileNameGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling FilesApi#filesGroupFileNameGet")
    e.printStackTrace()
}
```

### Parameters
| **group** | [**FileGroups**](.md)| The file group | [enum: equipmentmanuals, recipepictures, productpictures, userfiles, userpictures] |
| **fileName** | **kotlin.String**| The file name (including extension)&lt;br&gt;**BASE64 encoded** | |
| **forceServeAs** | **kotlin.String**| Force the file to be served as the given type | [optional] [enum: picture] |
| **bestFitHeight** | **kotlin.Double**| Only when using &#x60;force_serve_as&#x60; &#x3D; &#x60;picture&#x60;: Downscale the picture to the given height while maintaining the aspect ratio | [optional] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **bestFitWidth** | **kotlin.Double**| Only when using &#x60;force_serve_as&#x60; &#x3D; &#x60;picture&#x60;: Downscale the picture to the given width while maintaining the aspect ratio | [optional] |

### Return type

[**org.openapitools.client.infrastructure.OctetByteArray**](org.openapitools.client.infrastructure.OctetByteArray.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/octet-stream, application/json

<a id="filesGroupFileNamePut"></a>
# **filesGroupFileNamePut**
> filesGroupFileNamePut(group, fileName, body)

Uploads a single file

The file will be stored at /data/storage/{group}/{file_name} (you need to remember the group and file name to get or delete it again)

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = FilesApi()
val group : FileGroups =  // FileGroups | The file group
val fileName : kotlin.String = fileName_example // kotlin.String | The file name (including extension)<br>**BASE64 encoded**
val body : org.openapitools.client.infrastructure.OctetByteArray = BINARY_DATA_HERE // org.openapitools.client.infrastructure.OctetByteArray | 
try {
    apiInstance.filesGroupFileNamePut(group, fileName, body)
} catch (e: ClientException) {
    println("4xx response calling FilesApi#filesGroupFileNamePut")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling FilesApi#filesGroupFileNamePut")
    e.printStackTrace()
}
```

### Parameters
| **group** | [**FileGroups**](.md)| The file group | [enum: equipmentmanuals, recipepictures, productpictures, userfiles, userpictures] |
| **fileName** | **kotlin.String**| The file name (including extension)&lt;br&gt;**BASE64 encoded** | |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **body** | **org.openapitools.client.infrastructure.OctetByteArray**|  | [optional] |

### Return type

null (empty response body)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: application/octet-stream
 - **Accept**: application/json

