package org.openapitools.client.models

// The Grocy OpenAPI spec references these schema names in path parameters but never defines them
// in components/schemas, so the generator emits imports without ever producing model files.
// These typealiases bridge the generated API code to the actual enum classes that exist.
typealias ExposedEntityIncludingUserEntities = ExposedEntity
typealias ExposedEntityIncludingUserEntitiesNotIncludingNotEditable = ExposedEntityNoEdit
typealias ExposedEntityNotIncludingNotDeletable = ExposedEntityNoDelete
// The generated ExposedEntityNoEdit enum is inverted: it lists the *non-editable* entities
// (stock, api_keys, views, ...) and has no `products`, so objectsEntityPost/Put couldn't create
// or edit editable entities. Point this alias at the full ExposedEntity enum (which has every
// entity value, including products/locations/product_groups/quantity_units) so editable objects
// can be created and updated.
typealias ExposedEntityNotIncludingNotEditable = ExposedEntity
// The generated ExposedEntityNoListing enum is broken: the Grocy spec only lists `api_keys`
// under it, so it can't represent listable entities like quantity_units. Point this alias at
// the full ExposedEntity enum (which has every entity value) so objectsEntityGet can list them.
typealias ExposedEntityNotIncludingNotListable = ExposedEntity
