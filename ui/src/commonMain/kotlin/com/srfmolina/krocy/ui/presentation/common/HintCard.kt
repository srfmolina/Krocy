package com.srfmolina.krocy.ui.presentation.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing

@Composable
internal fun HintCard(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
        modifier = modifier
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(MaterialTheme.spacing.s2),
            style = textStyle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@PreviewLightDark
@Composable
private fun HintCardPreview() {
    KrocyTheme {
        HintCard(
            text = "5 Packs",
        )
    }
}