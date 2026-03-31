
# ChoresChoreIdExecutePostRequest

## Properties
| Name | Type | Description | Notes |
| ------------ | ------------- | ------------- | ------------- |
| **trackedTime** | [**kotlin.time.Instant**](kotlin.time.Instant.md) | The time of when the chore was executed, when omitted, the current time is used |  [optional] |
| **doneBy** | **kotlin.Int** | A valid user id of who executed this chore, when omitted, the currently authenticated user will be used |  [optional] |
| **skipped** | **kotlin.Boolean** | &#x60;true&#x60; when the execution should be tracked as skipped, defaults to &#x60;false&#x60; when omitted |  [optional] |



