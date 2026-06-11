package com.srfmolina.krocy.ui.presentation.common.selector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srfmolina.krocy.ui.base.BaseViewModel
import com.srfmolina.krocy.ui.presentation.common.KrocyDropdownField
import com.srfmolina.krocy.ui.presentation.common.model.SelectableOptionUi
import com.srfmolina.krocy.ui.presentation.common.selector.model.OptionSelectorEffect
import com.srfmolina.krocy.ui.presentation.common.selector.model.OptionSelectorEvent
import com.srfmolina.krocy.ui.presentation.common.selector.model.OptionSelectorState


@Composable
internal fun OptionSelector(
    viewModel: BaseViewModel<OptionSelectorEvent, OptionSelectorState, OptionSelectorEffect>,
    label: String,
    onOptionSelected: (Int?) -> Unit,
    selectedId: Int?,
    modifier: Modifier = Modifier
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.launchEvent(OptionSelectorEvent.Init)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OptionSelectorEffect.SelectOption -> onOptionSelected(effect.id)
            }
        }
    }

    OptionSelector(
        label = label,
        modifier = modifier,
        selectedId = selectedId,
        options = state.options,
        onOptionSelected = onOptionSelected
    )
}


@Composable
private fun OptionSelector(
    label: String,
    options: List<SelectableOptionUi>,
    selectedId: Int?,
    onOptionSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    KrocyDropdownField(
        modifier = modifier,
        label = label,
        options = options,
        selectedId = selectedId,
        onSelected = onOptionSelected,
        required = true,
    )
}