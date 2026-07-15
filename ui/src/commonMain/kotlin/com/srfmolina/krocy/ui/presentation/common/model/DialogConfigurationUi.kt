package com.srfmolina.krocy.ui.presentation.common.model

import androidx.compose.runtime.Composable

/**
 * App-hosted modal dialog, following the top bar / FAB / snackbar convention: the feature
 * screen submits a configuration (and `null` to dismiss); the app scaffold renders it.
 *
 * [content] is the dialog body. Screens that need live form state should read their own
 * ViewModel inside the slot instead of capturing snapshots, and re-submit the configuration
 * only when [confirmEnabled] changes.
 */
internal data class DialogConfigurationUi(
    val title: String,
    val confirm: LabeledActionUi,
    val dismiss: LabeledActionUi,
    val confirmEnabled: Boolean = true,
    val content: @Composable () -> Unit,
)
