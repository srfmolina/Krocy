package com.srfmolina.krocy.ui.presentation.common

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.srfmolina.krocy.ui.presentation.common.model.SelectableOptionUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing

private const val NONE_OPTION_ID = Int.MIN_VALUE

/**
 * A read-only Material 3 [ExposedDropdownMenuBox] backed by [SelectableOptionUi]s.
 *
 * The anchor shows the selected option's label (or an empty field with the floating [label]).
 * When [includeNoneOption] is set, a leading "Ninguno" entry is offered that emits `null` through
 * [onSelected] — used for optional selections such as the product group.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun KrocyDropdownField(
    label: String,
    options: List<SelectableOptionUi>,
    selectedId: Int?,
    onSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    includeNoneOption: Boolean = false,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedLabel = options.firstOrNull { it.id == selectedId }?.label.orEmpty()
    val fieldLabel = if (required) "$label *" else label

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            singleLine = true,
            label = { Text(fieldLabel) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            if (includeNoneOption) {
                DropdownMenuItem(
                    text = { Text("Ninguno") },
                    onClick = {
                        onSelected(null)
                        expanded = false
                    },
                )
            }
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelected(option.id)
                        expanded = false
                    },
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun KrocyDropdownFieldPreview() {
    KrocyTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.s4)) {
                KrocyDropdownField(
                    label = "Unidad de stock",
                    required = true,
                    options = listOf(
                        SelectableOptionUi(1, "Unidad"),
                        SelectableOptionUi(2, "Paquete"),
                        SelectableOptionUi(3, "Gramo"),
                    ),
                    selectedId = 2,
                    onSelected = {},
                )
            }
        }
    }
}
