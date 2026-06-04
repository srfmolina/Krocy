package com.srfmolina.krocy.ui.presentation.common.skeleton

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

/**
 * Shared tuning for the skeleton system: the shimmer timing/geometry and the placeholder colours.
 */
object SkeletonDefaults {

    /** Default placeholder shape — a pill that reads well for text bars and chips. */
    val Shape: Shape = RoundedCornerShape(percent = 50)

    /** Duration of one full shimmer sweep across the screen. */
    const val SweepDurationMillis: Int = 1100

    /**
     * Distance (px) the highlight band's leading edge travels each sweep, expressed in screen space
     * so placeholders stay in sync regardless of their position. Sized to comfortably cover a phone
     * or a desktop column.
     */
    const val SweepTravelPx: Float = 1600f

    /** Width (px) of the moving highlight band. */
    const val BandWidthPx: Float = 360f

    /** Base (dim) colour of a placeholder. Matches [PhotoHolder]'s fill so it blends in. */
    val baseColor: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surfaceContainerHigh

    /** Highlight colour swept across the placeholder by the shimmer band. */
    val highlightColor: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surfaceBright
}
