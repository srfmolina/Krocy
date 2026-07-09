package com.srfmolina.krocy.ui.presentation.common.selector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.srfmolina.krocy.ui.presentation.common.model.OptionsUi
import com.srfmolina.krocy.ui.presentation.common.model.SelectableOptionUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing

/**
 * An editable [ExposedDropdownMenuBox] that filters its options as the user
 * types (Grocy-style autocomplete). Stateless like [OptionSelector]: the
 * ViewModel owns the options and the selection; the query text is local UI
 * state. Editing the text clears the current selection.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchableOptionSelector(
    label: String,
    options: OptionsUi,
    selectedId: Int?,
    onOptionSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.options.firstOrNull { it.id == selectedId }?.label.orEmpty()
    var query by remember { mutableStateOf(selectedLabel) }

    // Follow selection changes coming from outside (e.g. state restoration).
    LaunchedEffect(selectedLabel) {
        if (selectedLabel.isNotEmpty()) query = selectedLabel
    }

    val enabled = !options.isLoading && !options.isError
    val fieldLabel = if (required) "$label *" else label
    // With a confirmed selection the full list is shown, otherwise filter by the query.
    val filtered = if (query.isBlank() || query == selectedLabel) {
        options.options
    } else {
        options.options.filter { it.label.contains(query, ignoreCase = true) }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable)
                .fillMaxWidth(),
            value = query,
            onValueChange = { text ->
                query = text
                expanded = true
                if (selectedId != null) onOptionSelected(null)
            },
            enabled = enabled,
            singleLine = true,
            label = { Text(fieldLabel) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            if (filtered.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Sin resultados") },
                    onClick = { expanded = false },
                    enabled = false,
                )
            }
            filtered.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onOptionSelected(option.id)
                        query = option.label
                        expanded = false
                    },
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchableOptionSelectorPreview() {
    KrocyTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.s4)) {
                SearchableOptionSelector(
                    label = "Producto",
                    options = OptionsUi(
                        options = listOf(
                            SelectableOptionUi(1, "Leche entera"),
                            SelectableOptionUi(2, "Leche desnatada"),
                            SelectableOptionUi(3, "Pan de molde"),
                        ),
                        isLoading = false,
                    ),
                    selectedId = 1,
                    onOptionSelected = {},
                    required = true,
                )
            }
        }
    }
}
