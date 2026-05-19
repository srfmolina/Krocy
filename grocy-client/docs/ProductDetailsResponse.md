
# ProductDetailsResponse

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **product** | [**Product**](Product.md) |  |  [optional] |
| **productBarcodes** | [**kotlin.collections.List&lt;ProductBarcode&gt;**](ProductBarcode.md) |  |  [optional] |
| **quantityUnitStock** | [**QuantityUnit**](QuantityUnit.md) |  |  [optional] |
| **defaultQuantityUnitPurchase** | [**QuantityUnit**](QuantityUnit.md) |  |  [optional] |
| **defaultQuantityUnitConsume** | [**QuantityUnit**](QuantityUnit.md) |  |  [optional] |
| **quantityUnitPrice** | [**QuantityUnit**](QuantityUnit.md) |  |  [optional] |
| **lastPurchased** | [**kotlinx.datetime.LocalDate**](kotlinx.datetime.LocalDate.md) |  |  [optional] |
| **lastUsed** | [**kotlinx.datetime.LocalDate**](kotlinx.datetime.LocalDate.md) |  |  [optional] |
| **stockAmount** | **kotlin.Double** |  |  [optional] |
| **stockAmountOpened** | **kotlin.Double** |  |  [optional] |
| **nextDueDate** | [**kotlinx.datetime.LocalDate**](kotlinx.datetime.LocalDate.md) |  |  [optional] |
| **lastPrice** | **kotlin.Double** | The price of the last purchase of the corresponding product |  [optional] |
| **avgPrice** | **kotlin.Double** | The average price af all stock entries currently in stock of the corresponding product |  [optional] |
| **currentPrice** | **kotlin.Double** | The current price of the corresponding product, based on the stock entry to use next (defined by the default consume rule \&quot;Opened first, then first due first, then first in first out\&quot;) or on the last price if the product is currently not in stock |  [optional] |
| **oldestPrice** | **kotlin.Double** | This field is deprecated and will be removed in a future version (currently returns the same as &#x60;current_price&#x60;) |  [optional] |
| **lastShoppingLocationId** | **kotlin.Int** |  |  [optional] |
| **location** | [**Location**](Location.md) |  |  [optional] |
| **averageShelfLifeDays** | **kotlin.Double** |  |  [optional] |
| **spoilRatePercent** | **kotlin.Double** |  |  [optional] |
| **hasChilds** | **kotlin.Int** | True when the product is a parent product of others |  [optional] |
| **defaultLocation** | [**Location**](Location.md) |  |  [optional] |
| **quConversionFactorPurchaseToStock** | **kotlin.Double** | The conversion factor of the corresponding QU conversion from the product&#39;s qu_id_purchase to qu_id_stock |  [optional] |
| **quConversionFactorPriceToStock** | **kotlin.Double** | The conversion factor of the corresponding QU conversion from the product&#39;s qu_id_price to qu_id_stock |  [optional] |



