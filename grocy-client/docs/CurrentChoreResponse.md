
# CurrentChoreResponse

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **choreId** | **kotlin.Int** |  |  [optional] |
| **choreName** | **kotlin.String** |  |  [optional] |
| **lastTrackedTime** | [**kotlin.time.Instant**](kotlin.time.Instant.md) |  |  [optional] |
| **trackDateOnly** | **kotlin.Boolean** |  |  [optional] |
| **nextEstimatedExecutionTime** | [**kotlin.time.Instant**](kotlin.time.Instant.md) | The next estimated execution time of this chore, 2999-12-31 23:59:59 when the given chore has a period_type of manually |  [optional] |
| **nextExecutionAssignedToUserId** | **kotlin.Int** |  |  [optional] |
| **isRescheduled** | **kotlin.Boolean** |  |  [optional] |
| **isReassigned** | **kotlin.Boolean** |  |  [optional] |
| **nextExecutionAssignedUser** | [**UserDto**](UserDto.md) |  |  [optional] |



