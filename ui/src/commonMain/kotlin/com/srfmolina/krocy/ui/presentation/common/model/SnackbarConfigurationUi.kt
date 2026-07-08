package com.srfmolina.krocy.ui.presentation.common.model

internal data class SnackbarConfigurationUi(
    val message: String,
    val type: SnackbarTypeUi = SnackbarTypeUi.INFO,
    val action: LabeledActionUi? = null,
)
