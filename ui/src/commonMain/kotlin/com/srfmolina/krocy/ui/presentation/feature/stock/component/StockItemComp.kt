package com.srfmolina.krocy.ui.presentation.feature.stock.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.srfmolina.krocy.domain.model.common.ConsumptionType
import com.srfmolina.krocy.ui.presentation.common.HintCard
import com.srfmolina.krocy.ui.presentation.common.PhotoHolder
import com.srfmolina.krocy.ui.presentation.common.model.ConsumptionDateUi
import com.srfmolina.krocy.ui.presentation.common.model.DisplaySizeUi
import com.srfmolina.krocy.ui.presentation.feature.stock.model.StockItemUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.displaySize
import com.srfmolina.krocy.ui.presentation.theme.extendedColorScheme
import com.srfmolina.krocy.ui.presentation.theme.spacing

@Composable
internal fun StockItemComp(
    item: StockItemUi,
    modifier: Modifier = Modifier
) {
    if (MaterialTheme.displaySize == DisplaySizeUi.S) {
        StockItemCompact(item, modifier)
    } else {
        StockItemDenseRow(item, modifier)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StockItemCompact(item: StockItemUi, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(MaterialTheme.spacing.s4)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = MaterialTheme.spacing.s18)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(MaterialTheme.spacing.s3),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s4)
    ) {
        PhotoHolder(size = MaterialTheme.spacing.s18) //TODO
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s1)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val hasExpiry = item.consumptionDate != null
            if (hasExpiry) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)
                ) {
                    ExpiryChip(item.consumptionDate, dense = false)

                    Text(
                        text = "·",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )


                    Text(
                        text = item.quantity,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                }
            }
        }
        IconButton(
            onClick = {},
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StockItemDenseRow(item: StockItemUi, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(MaterialTheme.spacing.s14)
            .padding(horizontal = MaterialTheme.spacing.s4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s4)
    ) {
        PhotoHolder(size = MaterialTheme.spacing.s10) //TODO
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box(modifier = Modifier.width(MaterialTheme.spacing.s32)) {
            item.consumptionDate?.let { ExpiryChip(it, dense = true) }
        }
        Text(
            text = item.quantity,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(MaterialTheme.spacing.s28),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = item.hints.getOrNull(1) ?: "—",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(MaterialTheme.spacing.s28),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(
            onClick = {},
            shapes = IconButtonDefaults.shapes(),
            modifier = Modifier.size(MaterialTheme.spacing.s12)
        ) { //TODO
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExpiryChip(consumptionDate: ConsumptionDateUi, dense: Boolean) {
    val containerColor = expiryHintColor(consumptionDate)
    HintCard(
        text = consumptionDate.date,
        containerColor = containerColor,
        textStyle = if (dense) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium
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
                StockItemUi(
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
private fun StockItemDensePreview() {
    KrocyTheme {
        Surface {
            Column {
                StockItemDenseRow(
                    StockItemUi(
                        id = 1, name = "Galletas María",
                        hints = listOf("2 paquetes", "1 abierto"),
                        consumptionDate = ConsumptionDateUi(ConsumptionType.PREFERENCE, "En 12 días", expired = false),
                        quantity = "2 paquetes"
                    )
                )
                HorizontalDivider()
                StockItemDenseRow(
                    StockItemUi(
                        id = 2, name = "Yogur natural",
                        hints = listOf("8 unidades", "2 abiertos"),
                        consumptionDate = ConsumptionDateUi(ConsumptionType.EXPIRATION, "En 2 días", expired = false),
                        quantity = "3 uds"
                    )
                )
                HorizontalDivider()
                StockItemDenseRow(
                    StockItemUi(
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
