package com.srfmolina.krocy.ui.presentation.feature.stock.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.srfmolina.krocy.ui.presentation.common.skeleton.LocalSkeletonState
import com.srfmolina.krocy.ui.presentation.common.skeleton.ProvideSkeleton
import com.srfmolina.krocy.ui.presentation.common.skeleton.skeleton
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
            contentDescription = "Acción de abrir un producto",
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
            PhotoHolder(
                size = MaterialTheme.spacing.s18,
                imageUrl = item.pictureUrl,
                contentDescription = item.name,
                modifier = Modifier.skeleton(RoundedCornerShape(MaterialTheme.spacing.s3), id = item.id)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s1)
            ) {
                StockItemName(item.name, item.id)

                RowOfHints(item)

            }
            StockItemMenu(actions, id = item.id)
        }
    }
}

@Composable
private fun StockItemName(
    name: String,
    id: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        modifier = modifier.skeleton(id = id),
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * Three-dots overflow menu, or its skeleton placeholder while [LocalSkeletonState] is active.
 * The real button is interactive, so during loading we swap in an inert circular placeholder
 * rather than masking a live button.
 */
@Composable
private fun StockItemMenu(actions: List<LabeledActionUi>, id: Int) {
    if (LocalSkeletonState.current.targets(id)) {
        Box(Modifier.size(MaterialTheme.spacing.s12).skeleton(CircleShape, id = id))
    } else {
        TheeDotsMenuButton(actions = actions)
    }
}

@Composable
private fun RowOfHints(
    item: StockItemUi
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)
    ) {
        item.consumptionDate?.let { item { ExpiryChip(item.consumptionDate, id = item.id) } }

        item {
            HintCard(
                text = item.quantity,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.skeleton(id = item.id)
            )
        }

        items(item.hints) { hint ->
            HintCard(
                text = hint,
                modifier = Modifier.skeleton(id = item.id)
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
        item.consumptionDate?.let { ExpiryChip(it, id = item.id) }

        HintCard(
            text = item.quantity,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.skeleton(id = item.id)
        )

        item.hints.forEach { hint ->
            HintCard(text = hint, modifier = Modifier.skeleton(id = item.id))
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
        PhotoHolder(
            size = MaterialTheme.spacing.s12,
            imageUrl = item.pictureUrl,
            contentDescription = item.name,
            modifier = Modifier.skeleton(RoundedCornerShape(MaterialTheme.spacing.s3), id = item.id)
        )
        StockItemName(modifier = Modifier.weight(1f), name = item.name, id = item.id)

        // Content-sized so the weighted name above pushes it to the right edge.
        // A LazyRow would fill the remaining width and starve the name (see StockItemCompact).
        RowOfHintsStatic(item)

        StockItemMenu(actions, id = item.id)
    }
}

@Composable
private fun ExpiryChip(consumptionDate: ConsumptionDateUi, id: Int) {
    val containerColor = expiryHintColor(consumptionDate)
    HintCard(
        text = consumptionDate.date,
        containerColor = containerColor,
        modifier = Modifier.skeleton(id = id)
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
                    quantity = "3 Packs",
                    pictureUrl = null
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
                        quantity = "2 paquetes",
                        pictureUrl = null
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
                        quantity = "3 uds",
                        pictureUrl = null
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
                        quantity = "2 uds",
                        pictureUrl = null
                    )
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun StockItemSkeletonPreview() {
    KrocyTheme {
        Surface {
            ProvideSkeleton(active = true) {
                Column {
                    StockItemCompact(
                        modifier = Modifier.padding(MaterialTheme.spacing.s4),
                        actions = emptyList(),
                        item = StockItemUi(
                            id = 0,
                            name = "Galletas María",
                            hints = listOf("1 abierto"),
                            consumptionDate = ConsumptionDateUi(
                                ConsumptionType.PREFERENCE,
                                "En 2 días",
                                expired = false
                            ),
                            quantity = "3 Packs",
                            pictureUrl = null
                        )
                    )
                    HorizontalDivider()
                    StockItemRow(
                        modifier = Modifier.padding(MaterialTheme.spacing.s4),
                        actions = emptyList(),
                        item = StockItemUi(
                            id = 1,
                            name = "Yogur natural",
                            hints = listOf("8 unidades", "2 abiertos"),
                            consumptionDate = ConsumptionDateUi(ConsumptionType.EXPIRATION, "En 2 días", expired = false),
                            quantity = "3 uds",
                            pictureUrl = null
                        )
                    )
                }
            }
        }
    }
}
