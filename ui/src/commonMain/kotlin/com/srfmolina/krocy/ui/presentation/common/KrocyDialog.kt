package com.srfmolina.krocy.ui.presentation.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.srfmolina.krocy.ui.presentation.common.model.DialogConfigurationUi

/** Renders the app-level [DialogConfigurationUi]; see that class for the convention. */
@Composable
internal fun KrocyDialog(config: DialogConfigurationUi) {
    AlertDialog(
        onDismissRequest = config.dismiss.onClick,
        title = { Text(config.title) },
        text = { config.content() },
        confirmButton = {
            TextButton(
                onClick = config.confirm.onClick,
                enabled = config.confirmEnabled,
            ) {
                Text(config.confirm.label)
            }
        },
        dismissButton = {
            TextButton(onClick = config.dismiss.onClick) {
                Text(config.dismiss.label)
            }
        },
    )
}
