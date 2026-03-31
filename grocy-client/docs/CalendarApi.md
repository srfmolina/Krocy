# CalendarApi

All URIs are relative to *http://xxx*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**calendarIcalGet**](CalendarApi.md#calendarIcalGet) | **GET** /calendar/ical | Returns the calendar in iCal format |
| [**calendarIcalSharingLinkGet**](CalendarApi.md#calendarIcalSharingLinkGet) | **GET** /calendar/ical/sharing-link | Returns a (public) sharing link for the calendar in iCal format |


<a id="calendarIcalGet"></a>
# **calendarIcalGet**
> kotlin.String calendarIcalGet()

Returns the calendar in iCal format

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = CalendarApi()
try {
    val result : kotlin.String = apiInstance.calendarIcalGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CalendarApi#calendarIcalGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CalendarApi#calendarIcalGet")
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
 - **Accept**: text/calendar, application/json

<a id="calendarIcalSharingLinkGet"></a>
# **calendarIcalSharingLinkGet**
> CalendarIcalSharingLinkGet200Response calendarIcalSharingLinkGet()

Returns a (public) sharing link for the calendar in iCal format

### Example
```kotlin
// Import classes:
//import org.openapitools.client.infrastructure.*
//import org.openapitools.client.models.*

val apiInstance = CalendarApi()
try {
    val result : CalendarIcalSharingLinkGet200Response = apiInstance.calendarIcalSharingLinkGet()
    println(result)
} catch (e: ClientException) {
    println("4xx response calling CalendarApi#calendarIcalSharingLinkGet")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling CalendarApi#calendarIcalSharingLinkGet")
    e.printStackTrace()
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**CalendarIcalSharingLinkGet200Response**](CalendarIcalSharingLinkGet200Response.md)

### Authorization


Configure ApiKeyAuth:
    ApiClient.apiKey["GROCY-API-KEY"] = ""
    ApiClient.apiKeyPrefix["GROCY-API-KEY"] = ""

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

