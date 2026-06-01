package com.srfmolina.krocy.ui.presentation.feature.stock.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.srfmolina.krocy.domain.model.common.ConsumptionType
import com.srfmolina.krocy.ui.presentation.common.HintCard
import com.srfmolina.krocy.ui.presentation.common.PhotoHolder
import com.srfmolina.krocy.ui.presentation.common.TheeDotsMenuButton
import com.srfmolina.krocy.ui.presentation.common.model.ConsumptionDateUi
import com.srfmolina.krocy.ui.presentation.common.model.LabeledActionUi
import com.srfmolina.krocy.ui.presentation.feature.stock.model.StockItemUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.extendedColorScheme
import com.srfmolina.krocy.ui.presentation.theme.isCompact
import com.srfmolina.krocy.ui.presentation.theme.spacing

@Composable
internal fun StockItemComp(
    item: StockItemUi,
    onConsume: (Int) -> Unit,
    onAdd: (Int) -> Unit,
    onOpen: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val actions = listOf(
        LabeledActionUi(
            label = "Consumir", //TODO
            contentDescription = "Acción de consumir un producto",
            icon = Icons.Default.Restaurant,
            onClick = { onConsume(item.id) }
        ),
        LabeledActionUi(
            label = "Abrir", //TODO
            contentDescription = "Acción de consumir un producto",
            icon = Icons.Default.Drafts,
            onClick = { onOpen(item.id) }
        ),
        LabeledActionUi(
            label = "Aumentar", //TODO
            contentDescription = "Acción de aumentar el stock de un producto",
            icon = Icons.Default.AddBox,
            onClick = { onAdd(item.id) }
        )
    )

    if (MaterialTheme.isCompact) {
        StockItemCompact(item, actions, modifier)
    } else {
        StockItemRow(item, actions, modifier)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StockItemCompact(
    item: StockItemUi,
    actions: List<LabeledActionUi>,
    modifier: Modifier = Modifier
) {

    Surface {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s4)
        ) {
            PhotoHolder(size = MaterialTheme.spacing.s18) //TODO
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s1)
            ) {
                StockItemName(item.name)

                RowOfHints(item)

            }
            TheeDotsMenuButton(actions = actions)
        }
    }
}

@Composable
private fun StockItemName(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun RowOfHints(
    item: StockItemUi
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)
    ) {
        item.consumptionDate?.let { item { ExpiryChip(item.consumptionDate) } }

        item {
            HintCard(
                text = item.quantity,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        }

        items(item.hints) { hint ->
            HintCard(
                text = hint,
            )
        }
    }
}

/**
 * Content-sized (non-lazy) variant of [RowOfHints] for wide layouts, where the chips sit
 * to the right of a weighted name and must not consume the row's remaining width.
 */
@Composable
private fun RowOfHintsStatic(
    item: StockItemUi
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)
    ) {
        item.consumptionDate?.let { ExpiryChip(it) }

        HintCard(
            text = item.quantity,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )

        item.hints.forEach { hint ->
            HintCard(text = hint)
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StockItemRow(
    item: StockItemUi,
    actions: List<LabeledActionUi>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s4)
    ) {
        PhotoHolder(size = MaterialTheme.spacing.s12) //TODO
        StockItemName(item.name, modifier = Modifier.weight(1f))

        // Content-sized so the weighted name above pushes it to the right edge.
        // A LazyRow would fill the remaining width and starve the name (see StockItemCompact).
        RowOfHintsStatic(item)

        TheeDotsMenuButton(actions = actions)
    }
}

@Composable
private fun ExpiryChip(consumptionDate: ConsumptionDateUi) {
    val containerColor = expiryHintColor(consumptionDate)
    HintCard(
        text = consumptionDate.date,
        containerColor = containerColor,
    )

}

@Composable
private fun expiryHintColor(consumptionDate: ConsumptionDateUi): Color {
    val cs = MaterialTheme.colorScheme
    val ext = MaterialTheme.extendedColorScheme
    return when {
        consumptionDate.expired ->
            cs.errorContainer
        consumptionDate.type == ConsumptionType.EXPIRATION ->
            ext.danger.colorContainer
        consumptionDate.type == ConsumptionType.PREFERENCE ->
            ext.warning.colorContainer
        else ->
            cs.secondaryContainer
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
        StockItemCompPPPI(hints = emptyList()),
        StockItemCompPPPI(
            hints = listOf("1 abierto"),
            consumptionDate = ConsumptionDateUi(
                type = ConsumptionType.PREFERENCE,
                date = "En 2 días",
                expired = false
            )
        ),
        StockItemCompPPPI(
            hints = listOf("1 abierto"),
            consumptionDate = ConsumptionDateUi(
                type = ConsumptionType.EXPIRATION,
                date = "En 2 días",
                expired = false
            )
        ),
        StockItemCompPPPI(
            hints = listOf("2 abiertos"),
            consumptionDate = ConsumptionDateUi(
                type = ConsumptionType.EXPIRATION,
                date = "Hace 1 día",
                expired = true
            )
        )
    )
}

@PreviewLightDark
@Composable
private fun StockItemCompactPreview(
    @PreviewParameter(StockItemCompPPP::class)
    item: StockItemCompPPPI
) {
    KrocyTheme {
        Surface {
            StockItemCompact(
                modifier = Modifier.padding(MaterialTheme.spacing.s4),
                actions = emptyList(),
                item = StockItemUi(
                    id = 0,
                    name = "Galletas María",
                    hints = item.hints,
                    consumptionDate = item.consumptionDate,
                    quantity = "3 Packs"
                )
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StockItemRowPreview() {
    KrocyTheme {
        Surface {
            Column {
                StockItemRow(
                    modifier = Modifier.padding(MaterialTheme.spacing.s4),
                    actions = emptyList(),
                    item = StockItemUi(
                        id = 1, name = "Galletas María",
                        hints = listOf("2 paquetes", "1 abierto"),
                        consumptionDate = ConsumptionDateUi(ConsumptionType.PREFERENCE, "En 12 días", expired = false),
                        quantity = "2 paquetes"
                    )
                )
                HorizontalDivider()
                StockItemRow(
                    modifier = Modifier.padding(MaterialTheme.spacing.s4),
                    actions = emptyList(),
                    item = StockItemUi(
                        id = 2, name = "Yogur natural",
                        hints = listOf("8 unidades", "2 abiertos"),
                        consumptionDate = ConsumptionDateUi(ConsumptionType.EXPIRATION, "En 2 días", expired = false),
                        quantity = "3 uds"
                    )
                )
                HorizontalDivider()
                StockItemRow(
                    modifier = Modifier.padding(MaterialTheme.spacing.s4),
                    actions = emptyList(),
                    item = StockItemUi(
                        id = 3, name = "Leche entera",
                        hints = listOf("3 briks", "1 abierto"),
                        consumptionDate = ConsumptionDateUi(ConsumptionType.EXPIRATION, "Hace 1 día", expired = true),
                        quantity = "2 uds"
                    )
                )
            }
        }
    }
}
