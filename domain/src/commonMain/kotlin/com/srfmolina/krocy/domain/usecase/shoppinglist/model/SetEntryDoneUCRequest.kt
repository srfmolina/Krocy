package com.srfmolina.krocy.domain.usecase.shoppinglist.model

data class SetEntryDoneUCRequest(
    val entryId: Int,
    val done: Boolean
)
