package com.srfmolina.krocy.ui.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import com.srfmolina.krocy.ui.presentation.theme.spacing

@Composable
internal fun PhotoHolder(
    size: Dp,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    contentDescription: String? = null,
    cornerRadius: Dp = MaterialTheme.spacing.s3
) {
    val shape = RoundedCornerShape(cornerRadius)
    val holderModifier = modifier
        .size(size)
        .clip(shape)
        .background(MaterialTheme.colorScheme.surfaceContainerHigh, shape)
        .border(MaterialTheme.spacing.s0, MaterialTheme.colorScheme.outlineVariant, shape)

    if (imageUrl == null) {
        Box(holderModifier)
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = holderModifier
        )
    }
}
