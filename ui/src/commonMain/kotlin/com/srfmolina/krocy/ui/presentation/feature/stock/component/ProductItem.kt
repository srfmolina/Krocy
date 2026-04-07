package com.srfmolina.krocy.ui.presentation.feature.stock.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.srfmolina.krocy.ui.presentation.common.HintCard
import com.srfmolina.krocy.ui.presentation.common.model.ConsumptionDate
import com.srfmolina.krocy.ui.presentation.common.model.ConsumptionType
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.extendedColorScheme
import com.srfmolina.krocy.ui.presentation.theme.spacing


@Composable
internal fun ProductItem(
    name: String,
    hints: List<String>,
    consumptionDate: ConsumptionDate?,
    modifier: Modifier = Modifier
) {

    val showRow = (consumptionDate != null) || hints.isNotEmpty()

    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium
        )

        if (showRow) {
            LazyRow (
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)
            ) {
                consumptionDate?.let {
                    item {
                        val containerColor = when {
                            it.expired -> MaterialTheme.colorScheme.errorContainer
                            it.type == ConsumptionType.EXPIRATION -> MaterialTheme.extendedColorScheme.danger.colorContainer
                            it.type == ConsumptionType.PREFERENCE -> MaterialTheme.extendedColorScheme.warning.colorContainer
                            else -> MaterialTheme.colorScheme.error
                        }
                        HintCard(
                            text = it.date,
                            containerColor = containerColor
                        )

                    }
                }

                items(hints) { hint ->
                    HintCard(
                        text = hint
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ProductItemPreview() {
    KrocyTheme {
        Surface {
            ProductItem(
                name = "Galletas",
                hints = listOf("1 Pack"),
                consumptionDate = ConsumptionDate(
                    type = ConsumptionType.PREFERENCE,
                    date = "2 days ago",
                    expired = false
                )
            )
        }
    }
}