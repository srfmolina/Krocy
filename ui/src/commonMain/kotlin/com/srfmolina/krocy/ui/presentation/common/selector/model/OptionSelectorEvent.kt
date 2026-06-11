package com.srfmolina.krocy.ui.presentation.common.selector.model

import com.srfmolina.krocy.ui.base.UiEvent

internal sealed interface OptionSelectorEvent : UiEvent {
    data object Init : OptionSelectorEvent
    data class OnStockUnitSelected(val id: Int) : OptionSelectorEvent
}