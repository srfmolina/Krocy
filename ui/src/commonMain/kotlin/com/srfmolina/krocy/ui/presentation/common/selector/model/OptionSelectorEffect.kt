package com.srfmolina.krocy.ui.presentation.common.selector.model

import com.srfmolina.krocy.ui.base.UiEffect

internal sealed interface OptionSelectorEffect : UiEffect {
    data class SelectOption(val id: Int) : OptionSelectorEffect
}