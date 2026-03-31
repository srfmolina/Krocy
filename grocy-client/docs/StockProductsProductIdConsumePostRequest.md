
# StockProductsProductIdConsumePostRequest

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **amount** | **kotlin.Double** | The amount to remove - please note that when tare weight handling for the product is enabled, this needs to be the amount including the container weight (gross), the amount to be posted will be automatically calculated based on what is in stock and the defined tare weight |  [optional] |
| **transactionType** | [**StockTransactionType**](StockTransactionType.md) |  |  [optional] |
| **spoiled** | **kotlin.Boolean** | True when the given product was spoiled, defaults to false |  [optional] |
| **stockEntryId** | **kotlin.String** | A specific stock entry id to consume, if used, the amount has to be 1 |  [optional] |
| **recipeId** | **kotlin.Int** | A valid recipe id for which this product was used (for statistical purposes only) |  [optional] |
| **locationId** | **kotlin.Int** | A valid location id (if supplied, only stock at the given location is considered, if ommitted, stock of any location is considered) |  [optional] |
| **exactAmount** | **kotlin.Boolean** | For tare weight handling enabled products, &#x60;true&#x60; when the given is the absolute amount to be consumed, not the amount including the container weight |  [optional] |
| **allowSubproductSubstitution** | **kotlin.Boolean** | &#x60;true&#x60; when any in stock sub product should be used when the given product is a parent product and currently not in stock |  [optional] |



