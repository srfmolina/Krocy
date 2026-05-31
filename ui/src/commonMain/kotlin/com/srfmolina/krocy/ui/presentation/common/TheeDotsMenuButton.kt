package com.srfmolina.krocy.ui.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.srfmolina.krocy.ui.presentation.common.model.LabeledActionUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
internal fun TheeDotsMenuButton(
    actions: List<LabeledActionUi>
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (actions.isNotEmpty()) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                actions.forEach { action ->
                    DropdownMenuItem(
                        text = { Text(action.label) },
                        leadingIcon = action.icon?.let { icon ->
                            {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = action.contentDescription
                                )
                            }
                        },
                        onClick = {
                            expanded = false
                            action.onClick()
                        }
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TheeDotsMenuButtonPreview() {
    KrocyTheme {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .height(320.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "Item label",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp)
            )
            TheeDotsMenuButton(
                actions = listOf(
                LabeledActionUi(
                    label = "Edit",
                    contentDescription = "Edit item",
                    icon = Icons.Default.Edit,
                    onClick = {}
                ),
                LabeledActionUi(
                    label = "Share",
                    contentDescription = "Share item",
                    icon = Icons.Default.Share,
                    onClick = {}
                ),
                LabeledActionUi(
                    label = "No icon",
                    contentDescription = "Action without icon",
                    onClick = {}
                ),
                LabeledActionUi(
                    label = "Delete",
                    contentDescription = "Delete item",
                    icon = Icons.Default.Delete,
                    onClick = {}
                )
                )
            )
        }
    }
}
