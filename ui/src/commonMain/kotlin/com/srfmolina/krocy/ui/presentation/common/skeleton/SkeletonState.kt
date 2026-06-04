package com.srfmolina.krocy.ui.presentation.common.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Skeleton (loading placeholder) context shared down the composition tree.
 *
 * While [isActive] is true, any composable tagged with [Modifier.skeleton][skeleton] paints a
 * shimmering placeholder instead of its real content. [progress] is the shared shimmer phase
 * (0f..1f, looping) so every placeholder on screen sweeps in sync — it is read in the draw phase,
 * never in composition, to keep the animation off the recomposition path.
 */
@Immutable
class SkeletonState(
    val isActive: Boolean,
    val progress: State<Float>?,
    val targetIds: Set<Int>? = null
) {
    /**
     * True when a node identified by [id] should render its skeleton placeholder: the skeleton must
     * be active, and either untargeted ([targetIds] null = every node) or this [id] is targeted.
     */
    fun targets(id: Int?): Boolean = isActive && (targetIds == null || id in targetIds)
}

val LocalSkeletonState = staticCompositionLocalOf { SkeletonState(isActive = false, progress = null) }

/**
 * Provides a [SkeletonState] to [content]. When [active], starts a single looping shimmer animation
 * shared by every descendant placeholder; when inactive, no animation is running.
 */
@Composable
fun ProvideSkeleton(
    active: Boolean,
    targetIds: Set<Int>? = null,
    content: @Composable () -> Unit
) {
    val skeletonState = if (active) {
        val transition = rememberInfiniteTransition(label = "skeleton")
        val progress = transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = SkeletonDefaults.SweepDurationMillis,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer"
        )
        remember(progress, targetIds) {
            SkeletonState(isActive = true, progress = progress, targetIds = targetIds)
        }
    } else {
        SkeletonState(isActive = false, progress = null)
    }

    CompositionLocalProvider(LocalSkeletonState provides skeletonState) {
        content()
    }
}
