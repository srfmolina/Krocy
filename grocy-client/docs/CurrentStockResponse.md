
# CurrentStockResponse

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **productId** | **kotlin.Int** |  |  [optional] |
| **amount** | **kotlin.Double** |  |  [optional] |
| **amountAggregated** | **kotlin.Double** |  |  [optional] |
| **amountOpened** | **kotlin.Double** |  |  [optional] |
| **amountOpenedAggregated** | **kotlin.Double** |  |  [optional] |
| **bestBeforeDate** | [**kotlinx.datetime.LocalDate**](kotlinx.datetime.LocalDate.md) | The next due date for this product |  [optional] |
| **isAggregatedAmount** | **kotlin.Boolean** | Indicates wheter this product has sub-products or not / if the fields &#x60;amount_aggregated&#x60; and &#x60;amount_opened_aggregated&#x60; are filled |  [optional] |
| **product** | [**ProductWithoutUserfields**](ProductWithoutUserfields.md) |  |  [optional] |



