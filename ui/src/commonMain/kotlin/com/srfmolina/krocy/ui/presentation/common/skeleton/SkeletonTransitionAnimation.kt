package com.srfmolina.krocy.ui.presentation.common.skeleton

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SkeletonTransitionAnimation(
    isLoading: Boolean,
    content: @Composable (Boolean) -> Unit
) {
    val motionScheme = MaterialTheme.motionScheme
    AnimatedContent(
        targetState = isLoading,
        transitionSpec = {
            (fadeIn(motionScheme.defaultEffectsSpec()) togetherWith
                    fadeOut(motionScheme.defaultEffectsSpec()))
                .using(SizeTransform(clip = false) { _, _ -> motionScheme.defaultSpatialSpec() })
        },
        label = "loading"
    ) { loading ->
        content(loading)
    }
}