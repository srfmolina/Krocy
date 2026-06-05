package com.srfmolina.krocy.ui.presentation.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import com.srfmolina.krocy.ui.presentation.common.model.LabeledActionUi
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarTypeUi

/**
 * Carries the snackbar [type] and optional [action] through [androidx.compose.material3.SnackbarHostState.showSnackbar]
 * so [KrocySnackbar] can render the right colors and the action's click can be resolved at the call site.
 */
internal class KrocySnackbarVisuals(
    override val message: String,
    val type: SnackbarTypeUi,
    val action: LabeledActionUi?,
) : SnackbarVisuals {
    override val actionLabel: String? = action?.label
    override val withDismissAction: Boolean = false
    override val duration: SnackbarDuration =
        if (action != null) SnackbarDuration.Long else SnackbarDuration.Short
}

/**
 * Renders a snackbar whose colors depend on the [SnackbarTypeUi] carried by [KrocySnackbarVisuals]:
 * ERROR uses the soft error container, INFO falls back to the Material3 defaults.
 */
@Composable
internal fun KrocySnackbar(snackbarData: SnackbarData) {
    val type = (snackbarData.visuals as? KrocySnackbarVisuals)?.type ?: SnackbarTypeUi.INFO
    when (type) {
        SnackbarTypeUi.ERROR -> Snackbar(
            snackbarData = snackbarData,
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            actionColor = MaterialTheme.colorScheme.error,
        )

        SnackbarTypeUi.INFO -> Snackbar(snackbarData = snackbarData)
    }
}
