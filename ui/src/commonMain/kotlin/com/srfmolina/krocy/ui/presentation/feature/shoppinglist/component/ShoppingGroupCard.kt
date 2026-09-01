package com.srfmolina.krocy.ui.presentation.feature.shoppinglist.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.srfmolina.krocy.ui.presentation.common.HintCard
import com.srfmolina.krocy.ui.presentation.common.skeleton.skeleton
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.model.ShoppingGroupUi
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.model.ShoppingListEntryUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing

/**
 * A product group as a "ticket": the group name fixed to the left just above an outlined card,
 * both tinted with the group's [groupAccent]. The card's top-left corner is deliberately tighter
 * than the others so the label reads as anchored to its card. When every entry is crossed off,
 * the counter morphs into a check and the card settles to neutral colours.
 */
@Composable
internal fun ShoppingGroupCard(
    group: ShoppingGroupUi,
    onToggleDone: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = groupAccent(group.name)
    val outlineColor by animateColorAsState(
        targetValue = if (group.allDone) MaterialTheme.colorScheme.outlineVariant else accent.outline,
        label = "groupOutline",
    )
    val containerColor by animateColorAsState(
        targetValue = if (group.allDone) MaterialTheme.colorScheme.surfaceContainerLow else accent.container,
        label = "groupContainer",
    )

    Column(modifier = modifier) {
        GroupHeader(group = group, labelColor = accent.label)
        OutlinedCard(
            shape = RoundedCornerShape(
                topStart = 6.dp,
                topEnd = 18.dp,
                bottomEnd = 18.dp,
                bottomStart = 18.dp,
            ),
            colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, outlineColor),
        ) {
            group.entries.forEachIndexed { index, entry ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.s4),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
                ShoppingEntryRow(entry = entry, onToggleDone = onToggleDone)
            }
        }
    }
}

@Composable
private fun GroupHeader(
    group: ShoppingGroupUi,
    labelColor: Color,
) {
    // Finished groups stop shouting: the label fades while keeping its hue.
    val labelAlpha by animateFloatAsState(
        targetValue = if (group.allDone) 0.62f else 1f,
        label = "groupLabelAlpha",
    )
    val skeletonId = group.entries.firstOrNull()?.id ?: 0

    Row(
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.s2,
            end = MaterialTheme.spacing.s1,
            bottom = MaterialTheme.spacing.s1,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2),
    ) {
        Text(
            text = group.displayName,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = labelColor,
            modifier = Modifier
                .graphicsLayer { alpha = labelAlpha }
                .skeleton(id = skeletonId),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s0),
            modifier = Modifier.skeleton(id = skeletonId),
        ) {
            if (group.allDone) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Grupo completado",
                    tint = labelColor,
                    modifier = Modifier
                        .size(14.dp)
                        .graphicsLayer { alpha = labelAlpha },
                )
            }
            Text(
                text = "${group.doneCount}/${group.entries.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ShoppingEntryRow(
    entry: ShoppingListEntryUi,
    onToggleDone: (Int, Boolean) -> Unit,
) {
    // The strikethrough draws itself left→right; TextDecoration.LineThrough cannot animate.
    val strikeProgress by animateFloatAsState(
        targetValue = if (entry.done) 1f else 0f,
        animationSpec = tween(durationMillis = 320, easing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)),
        label = "strike",
    )
    val nameColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.onSurface.let {
            if (entry.done) it.copy(alpha = 0.38f) else it
        },
        label = "entryName",
    )
    val hintColor by animateColorAsState(
        targetValue = if (entry.done) {
            MaterialTheme.colorScheme.surfaceContainerHigh
        } else {
            MaterialTheme.colorScheme.primaryContainer
        },
        label = "entryHint",
    )
    val strikeColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = if (entry.done) "Desmarcar" else "Marcar como comprado",
            ) { onToggleDone(entry.id, entry.done) }
            .padding(horizontal = MaterialTheme.spacing.s4, vertical = MaterialTheme.spacing.s3)
            .semantics { stateDescription = if (entry.done) "Comprado" else "Pendiente" },
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2),
    ) {
        Text(
            text = entry.name,
            style = MaterialTheme.typography.headlineSmall,
            color = nameColor,
            modifier = Modifier
                .skeleton(id = entry.id)
                .drawWithContent {
                    drawContent()
                    if (strikeProgress > 0f) {
                        val y = size.height * 0.55f
                        drawLine(
                            color = strikeColor,
                            start = Offset(0f, y),
                            end = Offset(size.width * strikeProgress, y),
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                },
        )
        HintCard(
            text = entry.quantity,
            containerColor = hintColor,
            modifier = Modifier.skeleton(id = entry.id),
        )
    }
}

// PREVIEW //

private val previewGroups = listOf(
    ShoppingGroupUi(
        name = "Frutas y verduras",
        entries = listOf(
            ShoppingListEntryUi(1, "Manzanas", "4 uds", done = false),
            ShoppingListEntryUi(2, "Plátanos", "1 racimo", done = true),
            ShoppingListEntryUi(3, "Tomates", "6 uds", done = false),
        ),
    ),
    ShoppingGroupUi(
        name = "Lácteos",
        entries = listOf(
            ShoppingListEntryUi(4, "Leche entera", "3 briks", done = true),
            ShoppingListEntryUi(5, "Yogur natural", "8 uds", done = true),
        ),
    ),
    ShoppingGroupUi(
        name = null,
        entries = listOf(
            ShoppingListEntryUi(6, "Pan de molde", "1 paquete", done = false),
        ),
    ),
)

@PreviewLightDark
@Composable
private fun ShoppingGroupCardPreview() {
    KrocyTheme {
        Surface {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.s4),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s6),
            ) {
                previewGroups.forEach { group ->
                    ShoppingGroupCard(group = group, onToggleDone = { _, _ -> })
                }
            }
        }
    }
}
