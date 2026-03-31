
# StockProductsProductIdTransferPostRequest

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **amount** | **kotlin.Double** | The amount to transfer - please note that when tare weight handling for the product is enabled, this needs to be the amount including the container weight (gross), the amount to be posted will be automatically calculated based on what is in stock and the defined tare weight |  [optional] |
| **locationIdFrom** | **kotlin.Int** | A valid location id, the location from where the product should be transfered |  [optional] |
| **locationIdTo** | **kotlin.Int** | A valid location id, the location to where the product should be transfered |  [optional] |
| **stockEntryId** | **kotlin.String** | A specific stock entry id to transfer, if used, the amount has to be 1 |  [optional] |



