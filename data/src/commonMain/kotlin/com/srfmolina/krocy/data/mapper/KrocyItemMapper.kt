package com.srfmolina.krocy.data.mapper

import com.srfmolina.krocy.data.entity.KrocyItemEntity
import com.srfmolina.krocy.domain.model.KrocyItem

fun KrocyItemEntity.toDomain(): KrocyItem = KrocyItem(
    id = id,
    name = name,
    createdAt = createdAt
)

fun KrocyItem.toEntity(): KrocyItemEntity = KrocyItemEntity(
    id = id,
    name = name,
    createdAt = createdAt
)