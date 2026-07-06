package com.srfmolina.krocy.ui.presentation.common.selector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.srfmolina.krocy.ui.presentation.common.KrocyDropdownField
import com.srfmolina.krocy.ui.presentation.common.model.OptionsUi
import com.srfmolina.krocy.ui.presentation.common.model.SelectableOptionUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing

/**
 * A dropdown selector backed by async-loaded [OptionsUi]. Stateless: the owning
 * screen's ViewModel loads the options (see [OptionsUi.load]) and holds the
 * selection, so the same component works with any data source.
 *
 * The field is disabled while the options are loading or failed to load.
 */
@Composable
internal fun OptionSelector(
    label: String,
    options: OptionsUi,
    selectedId: Int?,
    onOptionSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    includeNoneOption: Boolean = false,
) {
    KrocyDropdownField(
        modifier = modifier,
        label = label,
        options = options.options,
        selectedId = selectedId,
        onSelected = onOptionSelected,
        required = required,
        includeNoneOption = includeNoneOption,
        enabled = !options.isLoading && !options.isError,
    )
}

@PreviewLightDark
@Composable
private fun OptionSelectorPreview() {
    KrocyTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.s4)) {
                OptionSelector(
                    label = "Ubicación",
                    options = OptionsUi(
                        options = listOf(
                            SelectableOptionUi(10, "Despensa"),
                            SelectableOptionUi(11, "Nevera"),
                            SelectableOptionUi(12, "Congelador"),
                        ),
                        isLoading = false,
                    ),
                    selectedId = 11,
                    onOptionSelected = {},
                    required = true,
                )
            }
        }
    }
}
