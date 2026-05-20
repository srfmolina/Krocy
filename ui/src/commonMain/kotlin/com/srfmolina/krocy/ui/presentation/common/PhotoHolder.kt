package com.srfmolina.krocy.ui.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.srfmolina.krocy.ui.presentation.theme.spacing

@Composable
internal fun PhotoHolder(size: Dp, modifier: Modifier = Modifier, cornerRadius: Dp = MaterialTheme.spacing.s3) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .size(size)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, shape)
            .border(MaterialTheme.spacing.s0, MaterialTheme.colorScheme.outlineVariant, shape)
    )
}