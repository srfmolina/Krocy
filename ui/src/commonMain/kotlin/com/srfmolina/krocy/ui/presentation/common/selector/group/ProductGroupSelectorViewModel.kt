package com.srfmolina.krocy.ui.presentation.common.selector.group

import com.srfmolina.krocy.domain.usecase.masterdata.GetProductGroupsUseCase
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.presentation.common.selector.model.OptionSelectorEffect
import com.srfmolina.krocy.ui.presentation.common.selector.model.OptionSelectorEvent
import com.srfmolina.krocy.ui.presentation.common.selector.model.OptionSelectorState
import com.srfmolina.krocy.ui.presentation.feature.creation.mapper.toOption

internal class ProductGroupSelectorViewModel(
    private val getProductGroupsUseCase: GetProductGroupsUseCase,
) : BaseViewModel<OptionSelectorEvent, OptionSelectorState, OptionSelectorEffect>() {


    override fun createInitialState() = OptionSelectorState()

    override suspend fun handleEvent(event: OptionSelectorEvent) {
        when(event){
            is OptionSelectorEvent.Init -> loadOptions()
            is OptionSelectorEvent.OnStockUnitSelected -> launchEffect(OptionSelectorEffect.SelectOption(event.id))
        }
    }

    private suspend fun loadOptions() = getProductGroupsUseCase().onSuccess { quantityUnits ->
        setState {
            OptionSelectorState(
                options = quantityUnits.map { it.toOption() },
                isLoadingOptions = false
            )
        }
    }.onFailure {
        setState { OptionSelectorState(isLoadingOptions = false, optionsError = true) }
    }


}
