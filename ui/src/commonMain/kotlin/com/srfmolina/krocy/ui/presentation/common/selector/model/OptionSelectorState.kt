package com.srfmolina.krocy.ui.presentation.common.selector.model

import com.srfmolina.krocy.ui.base.UiState
import com.srfmolina.krocy.ui.presentation.common.model.SelectableOptionUi

internal data class OptionSelectorState(
    val isLoadingOptions: Boolean = true,
    val optionsError: Boolean = false,
    val options: List<SelectableOptionUi> = emptyList(),
) : UiState