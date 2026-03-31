
# StockProductsProductIdInventoryPostRequest

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **newAmount** | **kotlin.Double** | The new current amount for the given product - please note that when tare weight handling for the product is enabled, this needs to be the amount including the container weight (gross), the amount to be posted will be automatically calculated based on what is in stock and the defined tare weight |  [optional] |
| **bestBeforeDate** | [**kotlinx.datetime.LocalDate**](kotlinx.datetime.LocalDate.md) | The due date which applies to added products |  [optional] |
| **shoppingLocationId** | **kotlin.Int** | If omitted, no store will be affected |  [optional] |
| **locationId** | **kotlin.Int** | If omitted, the default location of the product is used (only applies to added products) |  [optional] |
| **price** | **kotlin.Double** | If omitted, the last price of the product is used (only applies to added products) |  [optional] |
| **stockLabelType** | **kotlin.Int** | &#x60;1&#x60; &#x3D; No label, &#x60;2&#x60; &#x3D; Single label, &#x60;3&#x60; &#x3D; Label per unit (only applies to added products) |  [optional] |
| **note** | **kotlin.String** | An optional note for the corresponding stock entry (only applies to added products) |  [optional] |



