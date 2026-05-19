package org.openapitools.client.models

// The Grocy OpenAPI spec references these schema names in path parameters but never defines them
// in components/schemas, so the generator emits imports without ever producing model files.
// These typealiases bridge the generated API code to the actual enum classes that exist.
typealias ExposedEntityIncludingUserEntities = ExposedEntity
typealias ExposedEntityIncludingUserEntitiesNotIncludingNotEditable = ExposedEntityNoEdit
typealias ExposedEntityNotIncludingNotDeletable = ExposedEntityNoDelete
typealias ExposedEntityNotIncludingNotEditable = ExposedEntityNoEdit
typealias ExposedEntityNotIncludingNotListable = ExposedEntityNoListing
