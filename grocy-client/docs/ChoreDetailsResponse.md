
# ChoreDetailsResponse

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **chore** | [**Chore**](Chore.md) |  |  [optional] |
| **lastTracked** | [**kotlin.time.Instant**](kotlin.time.Instant.md) | When this chore was last tracked |  [optional] |
| **trackCount** | **kotlin.Int** | How often this chore was tracked so far |  [optional] |
| **lastDoneBy** | [**UserDto**](UserDto.md) |  |  [optional] |
| **nextEstimatedExecutionTime** | [**kotlin.time.Instant**](kotlin.time.Instant.md) |  |  [optional] |
| **nextExecutionAssignedUser** | [**UserDto**](UserDto.md) |  |  [optional] |
| **averageExecutionFrequencyHours** | **kotlin.Int** | Contains the average past execution frequency in hours or &#x60;null&#x60;, when the chore was never executed before |  [optional] |



