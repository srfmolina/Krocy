
# StockProductsProductIdAddPostRequest

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **amount** | **kotlin.Double** | The amount to add - please note that when tare weight handling for the product is enabled, this needs to be the amount including the container weight (gross), the amount to be posted will be automatically calculated based on what is in stock and the defined tare weight |  [optional] |
| **bestBeforeDate** | [**kotlinx.datetime.LocalDate**](kotlinx.datetime.LocalDate.md) | The due date of the product to add, when omitted, the current date is used |  [optional] |
| **transactionType** | [**StockTransactionType**](StockTransactionType.md) |  |  [optional] |
| **price** | **kotlin.Double** | The price per stock quantity unit in configured currency |  [optional] |
| **locationId** | **kotlin.Int** | If omitted, the default location of the product is used |  [optional] |
| **shoppingLocationId** | **kotlin.Int** | If omitted, no store will be affected |  [optional] |
| **stockLabelType** | **kotlin.Int** | &#x60;1&#x60; &#x3D; No label, &#x60;2&#x60; &#x3D; Single label, &#x60;3&#x60; &#x3D; Label per unit |  [optional] |
| **note** | **kotlin.String** | An optional note for the corresponding stock entry |  [optional] |



