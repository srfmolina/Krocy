package com.srfmolina.krocy.data.entity.example

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "krocy_items")
data class KrocyItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val createdAt: Long
)