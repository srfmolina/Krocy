
# StockShoppinglistAddProductPostRequest

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **productId** | **kotlin.Int** | A valid product id of the product to be added |  [optional] |
| **quId** | **kotlin.Int** | A valid quantity unit id (used only for display; the amount needs to be related to the products stock QU), when omitted, the products stock QU is used |  [optional] |
| **listId** | **kotlin.Int** | A valid shopping list id, when omitted, the default shopping list (with id 1) is used |  [optional] |
| **productAmount** | **kotlin.Double** | The amount (related to the products stock QU) to add, when omitted, the default amount of 1 is used |  [optional] |
| **note** | **kotlin.String** | The note of the shopping list item |  [optional] |



