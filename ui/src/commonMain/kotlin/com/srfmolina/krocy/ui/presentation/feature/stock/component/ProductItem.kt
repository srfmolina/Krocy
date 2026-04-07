package com.srfmolina.krocy.ui.presentation.feature.stock.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing


@Composable
internal fun ProductItem(
    name: String,
    chips: List<String>
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@PreviewLightDark
@Composable
private fun ProductItemPreview() {
    KrocyTheme {
        Surface {
            ProductItem(
                name = "Galletas",
                chips = listOf("1 Pack")
            )
        }
    }
}