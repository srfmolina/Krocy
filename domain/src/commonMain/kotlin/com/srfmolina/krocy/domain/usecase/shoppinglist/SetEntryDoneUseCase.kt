package com.srfmolina.krocy.domain.usecase.shoppinglist

import com.srfmolina.krocy.domain.repository.ShoppingListRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.model.SetEntryDoneUCRequest

class SetEntryDoneUseCase(
    private val repo: ShoppingListRepository
) : ResultUseCase<SetEntryDoneUCRequest, Unit>() {

    override suspend fun execute(params: SetEntryDoneUCRequest) =
        repo.setDone(params.entryId, params.done)
}
