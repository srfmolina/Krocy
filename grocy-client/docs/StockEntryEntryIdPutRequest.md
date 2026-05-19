
# StockEntryEntryIdPutRequest

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **amount** | **kotlin.Double** | The amount to add - please note that when tare weight handling for the product is enabled, this needs to be the amount including the container weight (gross), the amount to be posted will be automatically calculated based on what is in stock and the defined tare weight |  [optional] |
| **bestBeforeDate** | [**kotlinx.datetime.LocalDate**](kotlinx.datetime.LocalDate.md) | The due date of the product to add, when omitted, the current date is used |  [optional] |
| **price** | **kotlin.Double** | The price per stock quantity unit in configured currency |  [optional] |
| **&#x60;open&#x60;** | **kotlin.Int** | If the stock entry was already opened or not |  [optional] |
| **locationId** | **kotlin.Int** | If omitted, the default location of the product is used |  [optional] |
| **shoppingLocationId** | **kotlin.Int** | If omitted, no store will be affected |  [optional] |
| **purchasedDate** | [**kotlinx.datetime.LocalDate**](kotlinx.datetime.LocalDate.md) | The date when this stock entry was purchased |  [optional] |



