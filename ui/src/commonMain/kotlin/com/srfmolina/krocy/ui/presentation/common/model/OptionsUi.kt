package com.srfmolina.krocy.ui.presentation.common.model

/**
 * Async-loaded options backing a selection dropdown: the same kind of data
 * regardless of its source (quantity units, locations, product groups...).
 *
 * ViewModels build instances by invoking the use case that fetches the domain
 * models and mapping its result via [toOptionsUi].
 */
internal data class OptionsUi(
    val options: List<SelectableOptionUi> = emptyList(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,
)

internal fun <T> Result<List<T>>.toOptionsUi(toOption: (T) -> SelectableOptionUi): OptionsUi = fold(
    onSuccess = { OptionsUi(options = it.map(toOption), isLoading = false) },
    onFailure = { OptionsUi(isLoading = false, isError = true) },
)
