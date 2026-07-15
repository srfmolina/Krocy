package com.srfmolina.krocy.domain.usecase.shoppinglist

import com.srfmolina.krocy.domain.repository.ShoppingListRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCaseNoParams

class RefreshShoppingListUseCase(
    private val repo: ShoppingListRepository
) : ResultUseCaseNoParams<Unit>() {
    override suspend fun execute() = repo.forceRefresh()
}
