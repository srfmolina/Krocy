package com.srfmolina.data.mapper

import com.srfmolina.data.entity.KrocyItemEntity
import com.srfmolina.domain.model.KrocyItem

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