package com.srfmolina.krocy.data.mapper

import com.srfmolina.krocy.data.entity.example.KrocyItemEntity
import com.srfmolina.krocy.domain.model.example.KrocyItem

fun KrocyItemEntity.toDomain(): KrocyItem = KrocyItem( // An example
    id = id,
    name = name,
    createdAt = createdAt
)

fun KrocyItem.toEntity(): KrocyItemEntity = KrocyItemEntity( // An example
    id = id,
    name = name,
    createdAt = createdAt
)