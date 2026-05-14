
# ObjectsEntityGet200ResponseInner

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int** |  |  [optional] |
| **name** | **kotlin.String** |  |  [optional] |
| **description** | **kotlin.String** |  |  [optional] |
| **locationId** | **kotlin.Int** |  |  [optional] |
| **quIdPurchase** | **kotlin.Int** |  |  [optional] |
| **quIdStock** | **kotlin.Int** |  |  [optional] |
| **enableTareWeightHandling** | **kotlin.Int** |  |  [optional] |
| **notCheckStockFulfillmentForRecipes** | **kotlin.Int** |  |  [optional] |
| **productGroupId** | **kotlin.Int** |  |  [optional] |
| **tareWeight** | **kotlin.Double** |  |  [optional] |
| **minStockAmount** | **kotlin.Double** |  |  [optional] |
| **defaultBestBeforeDays** | **kotlin.Int** |  |  [optional] |
| **defaultBestBeforeDaysAfterOpen** | **kotlin.Int** |  |  [optional] |
| **pictureFileName** | **kotlin.String** |  |  [optional] |
| **rowCreatedTimestamp** | **kotlin.String** |  |  [optional] |
| **shoppingLocationId** | **kotlin.Int** |  |  [optional] |
| **treatOpenedAsOutOfStock** | **kotlin.Int** |  |  [optional] |
| **autoReprintStockLabel** | **kotlin.Int** |  |  [optional] |
| **noOwnStock** | **kotlin.Int** |  |  [optional] |
| **userfields** | **kotlin.String** | Key/value pairs of userfields |  [optional] |
| **shouldNotBeFrozen** | **kotlin.Int** |  |  [optional] |
| **defaultConsumeLocationId** | **kotlin.Int** |  |  [optional] |
| **moveOnOpen** | **kotlin.Int** |  |  [optional] |
| **periodType** | [**inline**](#PeriodType) |  |  [optional] |
| **periodConfig** | **kotlin.String** |  |  [optional] |
| **periodDays** | **kotlin.Int** |  |  [optional] |
| **trackDateOnly** | **kotlin.Int** |  |  [optional] |
| **rollover** | **kotlin.Int** |  |  [optional] |
| **assignmentType** | [**inline**](#AssignmentType) |  |  [optional] |
| **assignmentConfig** | **kotlin.String** |  |  [optional] |
| **nextExecutionAssignedToUserId** | **kotlin.Int** |  |  [optional] |
| **startDate** | **kotlin.String** |  |  [optional] |
| **rescheduledDate** | **kotlin.String** |  |  [optional] |
| **rescheduledNextExecutionAssignedToUserId** | **kotlin.Int** |  |  [optional] |
| **usedIn** | **kotlin.String** |  |  [optional] |
| **chargeIntervalDays** | **kotlin.Int** |  |  [optional] |
| **namePlural** | **kotlin.String** |  |  [optional] |
| **pluralForms** | **kotlin.String** |  |  [optional] |
| **shoppingListId** | **kotlin.Int** |  |  [optional] |
| **productId** | **kotlin.Int** |  |  [optional] |
| **note** | **kotlin.String** |  |  [optional] |
| **amount** | **kotlin.Double** |  |  [optional] |
| **bestBeforeDate** | [**kotlinx.datetime.LocalDate**](kotlinx.datetime.LocalDate.md) |  |  [optional] |
| **purchasedDate** | [**kotlinx.datetime.LocalDate**](kotlinx.datetime.LocalDate.md) |  |  [optional] |
| **stockId** | **kotlin.String** | A unique id which references this stock entry during its lifetime |  [optional] |
| **price** | **kotlin.Double** |  |  [optional] |
| **&#x60;open&#x60;** | **kotlin.Int** |  |  [optional] |
| **openedDate** | [**kotlinx.datetime.LocalDate**](kotlinx.datetime.LocalDate.md) |  |  [optional] |
| **barcode** | **kotlin.String** |  |  [optional] |
| **quId** | **kotlin.Int** |  |  [optional] |
| **lastPrice** | **kotlin.Double** |  |  [optional] |


<a id="PeriodType"></a>
## Enum: period_type
| Name | Value |
| ---- | ----- |
| periodType | manually, hourly, daily, weekly, monthly |


<a id="AssignmentType"></a>
## Enum: assignment_type
| Name | Value |
| ---- | ----- |
| assignmentType | no-assignment, who-least-did-first, random, in-alphabetical-order |



