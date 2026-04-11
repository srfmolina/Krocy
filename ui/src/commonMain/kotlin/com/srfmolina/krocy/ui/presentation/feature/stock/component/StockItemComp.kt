package com.srfmolina.krocy.ui.presentation.feature.stock.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.srfmolina.krocy.domain.model.common.ConsumptionType
import com.srfmolina.krocy.ui.presentation.common.HintCard
import com.srfmolina.krocy.ui.presentation.common.model.ConsumptionDateUi
import com.srfmolina.krocy.ui.presentation.feature.stock.model.StockItemUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.extendedColorScheme
import com.srfmolina.krocy.ui.presentation.theme.spacing


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun StockItemComp(
    item: StockItemUi,
    modifier: Modifier = Modifier
) {

    val showRow = (item.consumptionDate != null) || item.hints.isNotEmpty()

    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s1)
    ) {
        Text(
            text = item.name,
            style = MaterialTheme.typography.titleMedium
        )

        if (showRow) {
            LazyRow (
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)
            ) {
                item.consumptionDate?.let {
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

                items(item.hints) { hint ->
                    HintCard(
                        text = hint
                    )
                }
            }
        }
    }
}

// PREVIEW //

private data class StockItemCompPPPI(
    val hints: List<String> = emptyList(),
    val consumptionDate: ConsumptionDateUi? = null
)

private class StockItemCompPPP : PreviewParameterProvider<StockItemCompPPPI> {
    override val values: Sequence<StockItemCompPPPI> = sequenceOf(
        StockItemCompPPPI(),
        StockItemCompPPPI(
            hints = listOf("1 Pack")
        ),
        StockItemCompPPPI(
            hints = listOf("2 Paquetes", "1 abierto"),
            ConsumptionDateUi(
                type = ConsumptionType.PREFERENCE,
                date = "En 2 días",
                expired = false
            )
        ),
        StockItemCompPPPI(
            hints = listOf("2 Paquetes", "1 abierto"),
            ConsumptionDateUi(
                type = ConsumptionType.EXPIRATION,
                date = "En 2 días",
                expired = false
            )
        ),
        StockItemCompPPPI(
            hints = listOf("2 Paquetes", "1 abierto"),
            ConsumptionDateUi(
                type = ConsumptionType.EXPIRATION,
                date = "Hace 1 día",
                expired = true
            )
        )
    )
}

@PreviewLightDark
@Composable
private fun StockItemCompPreview(
    @PreviewParameter(StockItemCompPPP::class)
    item: StockItemCompPPPI
) {
    KrocyTheme {
        Surface {
            StockItemComp(
                StockItemUi(
                    id = 0,
                    name = "Galletas",
                    hints = item.hints,
                    consumptionDate = item.consumptionDate
                )
            )
        }
    }
}