
# Chore

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **id** | **kotlin.Int** |  |  [optional] |
| **name** | **kotlin.String** |  |  [optional] |
| **description** | **kotlin.String** |  |  [optional] |
| **periodType** | [**inline**](#PeriodType) |  |  [optional] |
| **periodConfig** | **kotlin.String** |  |  [optional] |
| **periodDays** | **kotlin.Int** |  |  [optional] |
| **trackDateOnly** | **kotlin.Boolean** |  |  [optional] |
| **rollover** | **kotlin.Boolean** |  |  [optional] |
| **assignmentType** | [**inline**](#AssignmentType) |  |  [optional] |
| **assignmentConfig** | **kotlin.String** |  |  [optional] |
| **nextExecutionAssignedToUserId** | **kotlin.Int** |  |  [optional] |
| **startDate** | [**kotlin.time.Instant**](kotlin.time.Instant.md) |  |  [optional] |
| **rescheduledDate** | [**kotlin.time.Instant**](kotlin.time.Instant.md) |  |  [optional] |
| **rescheduledNextExecutionAssignedToUserId** | **kotlin.Int** |  |  [optional] |
| **rowCreatedTimestamp** | [**kotlin.time.Instant**](kotlin.time.Instant.md) |  |  [optional] |
| **userfields** | **kotlin.String** | Key/value pairs of userfields |  [optional] |


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



