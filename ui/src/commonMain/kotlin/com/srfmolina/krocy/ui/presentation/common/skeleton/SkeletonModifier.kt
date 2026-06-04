package com.srfmolina.krocy.ui.presentation.common.skeleton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow

/**
 * Replaces this composable's drawn content with a shimmering skeleton placeholder while the
 * surrounding [ProvideSkeleton] is active. When inactive, the modifier is a no-op and the real
 * content draws normally.
 *
 * The placeholder is painted over the node's measured bounds using [shape], so the skeleton always
 * matches the real layout — feed the composable representative dummy data to get believable sizes.
 * The highlight band is positioned in **screen space**, so every placeholder shares one coherent
 * diagonal sweep.
 *
 * @param shape outline of the placeholder block (defaults to a pill, see [SkeletonDefaults.Shape]).
 */
@Composable
fun Modifier.skeleton(shape: Shape = SkeletonDefaults.Shape, id: Int? = null): Modifier {
    val state = LocalSkeletonState.current
    val progress = state.progress
    if (!state.targets(id) || progress == null) return this

    val base = SkeletonDefaults.baseColor
    val highlight = SkeletonDefaults.highlightColor
    var windowOffset by remember { mutableStateOf(Offset.Zero) }

    return this
        .onGloballyPositioned { windowOffset = it.positionInWindow() }
        .drawWithContent {
            // Leading edge of the highlight band, in screen space, swept by the shared phase.
            val head = progress.value * (SkeletonDefaults.SweepTravelPx + SkeletonDefaults.BandWidthPx) -
                SkeletonDefaults.BandWidthPx
            // Translate screen space -> this node's local space so all placeholders align.
            val localX = head - windowOffset.x
            val localY = -windowOffset.y
            val brush = Brush.linearGradient(
                colorStops = arrayOf(0f to base, 0.5f to highlight, 1f to base),
                // Diagonal band: runs down-right across BandWidthPx in both axes.
                start = Offset(localX, localY),
                end = Offset(localX + SkeletonDefaults.BandWidthPx, localY + SkeletonDefaults.BandWidthPx)
            )
            val outline = shape.createOutline(size, layoutDirection, this)
            // Mask the real content (don't call drawContent) and paint the placeholder instead.
            drawOutline(outline = outline, brush = brush)
        }
}
