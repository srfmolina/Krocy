package com.srfmolina.krocy.ui.presentation.feature.shoppinglist.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Per-group accent roles for the shopping list "ticket" cards. */
internal data class GroupAccent(
    val label: Color,
    val outline: Color,
    val container: Color,
)

/**
 * Deterministic, theme-aware accent derived from the group name: the name's hashCode picks
 * one of 24 hue families (15° steps); saturation and lightness are fixed per theme and role,
 * deliberately mid-chroma so the accent labels the group without fighting the colour scheme.
 * Two groups may share a family — the colour is identity, not meaning.
 *
 * A null [groupName] (ungrouped products) gets a neutral accent from the colour scheme.
 */
@Composable
internal fun groupAccent(groupName: String?): GroupAccent {
    if (groupName == null) {
        return GroupAccent(
            label = MaterialTheme.colorScheme.onSurfaceVariant,
            outline = MaterialTheme.colorScheme.outlineVariant,
            container = MaterialTheme.colorScheme.surfaceContainerLow,
        )
    }
    val hue = groupHue(groupName)
    return if (isSystemInDarkTheme()) {
        GroupAccent(
            label = Color.hsl(hue, 0.45f, 0.78f),
            outline = Color.hsl(hue, 0.25f, 0.40f),
            container = Color.hsl(hue, 0.30f, 0.17f),
        )
    } else {
        GroupAccent(
            label = Color.hsl(hue, 0.48f, 0.33f),
            outline = Color.hsl(hue, 0.35f, 0.68f),
            container = Color.hsl(hue, 0.55f, 0.93f),
        )
    }
}

internal fun groupHue(name: String): Float =
    (((name.trim().lowercase().hashCode() % 360) + 360) % 360) / 15 * 15f
