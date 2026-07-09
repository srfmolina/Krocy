package com.srfmolina.krocy.domain.usecase.shoppinglist

import com.srfmolina.krocy.domain.repository.ShoppingListRepository
import com.srfmolina.krocy.domain.usecase.base.ResultUseCase
import com.srfmolina.krocy.domain.usecase.shoppinglist.model.AddToShoppingListUCRequest

class AddToShoppingListUseCase(
    private val repo: ShoppingListRepository
) : ResultUseCase<AddToShoppingListUCRequest, Unit>() {

    override suspend fun execute(params: AddToShoppingListUCRequest) =
        repo.addProduct(params.productId, params.amount)
}
