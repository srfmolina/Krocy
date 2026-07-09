package com.srfmolina.krocy.domain.usecase.shoppinglist

import com.srfmolina.krocy.domain.model.shoppinglist.ShoppingListEntry
import com.srfmolina.krocy.domain.repository.ShoppingListRepository
import com.srfmolina.krocy.domain.usecase.base.ResultFlowUseCaseNoParams
import kotlinx.coroutines.flow.Flow

class ObserveShoppingListUseCase(
    private val repo: ShoppingListRepository
) : ResultFlowUseCaseNoParams<List<ShoppingListEntry>>() {

    override fun execute(): Flow<List<ShoppingListEntry>> = repo.getShoppingList()
}
