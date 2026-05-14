@file:OptIn(ExperimentalMaterial3Api::class)

package com.srfmolina.krocy.ui.presentation.navigation.component.rail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.srfmolina.krocy.ui.presentation.navigation.KrocyRoute
import com.srfmolina.krocy.ui.presentation.navigation.NavigationItemUi
import com.srfmolina.krocy.ui.presentation.navigation.SplashRoute
import com.srfmolina.krocy.ui.presentation.navigation.StockRoute
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing

private val COMPACT_BREAKPOINT = 600.dp
private val RAIL_COLLAPSED_WIDTH = 80.dp
private val RAIL_EXPANDED_WIDTH = 240.dp
private const val ANIM_DURATION_MS = 300

/**
 * Adaptive navigation rail that adjusts its behaviour based on available screen width.
 *
 * - **Compact** (< 600 dp): the rail is hidden. The modal is controlled externally via
 *   [compactExpanded] and [onCompactDismiss] — the caller decides when to open it (e.g. a
 *   button in the top bar). Selecting a destination or tapping the scrim calls [onCompactDismiss].
 *
 * - **Wide** (≥ 600 dp): the rail is always visible in its collapsed form (icon-only, 80 dp).
 *   Tapping the header menu icon expands it to full width (240 dp) with destination labels.
 *   This toggle is managed internally.
 *
 * @param items             Ordered list of navigation destinations.
 * @param selectedRoute     Currently active destination.
 * @param compactExpanded   Whether the compact modal rail is open. Ignored on wide screens.
 * @param onCompactDismiss  Called when the compact modal should close (scrim tap or item select).
 * @param modifier          Modifier applied to the root container.
 * @param content           Main screen content rendered beside or behind the rail.
 */
@Composable
internal fun KrocyNavigationRail(
    items: List<NavigationItemUi>,
    selectedRoute: KrocyRoute,
    compactExpanded: Boolean,
    onCompactDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val isCompact = maxWidth < COMPACT_BREAKPOINT
        var wideExpanded by rememberSaveable { mutableStateOf(false) }

        if (isCompact) {
            CompactLayout(
                items = items,
                selectedRoute = selectedRoute,
                expanded = compactExpanded,
                onCollapse = onCompactDismiss,
                onItemSelected = { item ->
                    item.navigateTo()
                    onCompactDismiss()
                },
                content = content,
            )
        } else {
            WideLayout(
                items = items,
                selectedRoute = selectedRoute,
                expanded = wideExpanded,
                onToggle = { wideExpanded = !wideExpanded },
                onItemSelected = { item ->
                    item.navigateTo()
                },
                content = content,
            )
        }
    }
}

// ─────────────────────── Compact layout ───────────────────────

@Composable
private fun CompactLayout(
    items: List<NavigationItemUi>,
    selectedRoute: KrocyRoute,
    expanded: Boolean,
    onCollapse: () -> Unit,
    onItemSelected: (NavigationItemUi) -> Unit,
    content: @Composable () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Main content always fills the screen
        content()

        // Scrim — tap to dismiss the modal rail
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(ANIM_DURATION_MS)),
            exit = fadeOut(tween(ANIM_DURATION_MS)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onCollapse,
                    ),
            )
        }

        // Modal rail — slides in from the left edge
        AnimatedVisibility(
            visible = expanded,
            enter = slideInHorizontally(tween(ANIM_DURATION_MS)) { -it },
            exit = slideOutHorizontally(tween(ANIM_DURATION_MS)) { -it },
        ) {
            RailPanel(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(RAIL_EXPANDED_WIDTH),
                items = items,
                selectedRoute = selectedRoute,
                showLabels = true,
                onItemSelected = onItemSelected,
                menuIcon = Icons.AutoMirrored.Filled.MenuOpen,
                menuContentDescription = "Close navigation menu",
                onMenuClick = onCollapse,
            )
        }
    }
}

// ─────────────────────── Wide layout ───────────────────────

@Composable
private fun WideLayout(
    items: List<NavigationItemUi>,
    selectedRoute: KrocyRoute,
    expanded: Boolean,
    onToggle: () -> Unit,
    onItemSelected: (NavigationItemUi) -> Unit,
    content: @Composable () -> Unit,
) {
    val railWidth by animateDpAsState(
        targetValue = if (expanded) RAIL_EXPANDED_WIDTH else RAIL_COLLAPSED_WIDTH,
        animationSpec = tween(ANIM_DURATION_MS),
        label = "railWidth",
    )
    val labelRevealProgress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(ANIM_DURATION_MS),
        label = "labelRevealProgress",
    )

    Row(modifier = Modifier.fillMaxSize()) {
        RailPanel(
            modifier = Modifier
                .fillMaxHeight()
                .width(railWidth),
            items = items,
            selectedRoute = selectedRoute,
            showLabels = expanded,
            labelRevealProgress = labelRevealProgress,
            onItemSelected = onItemSelected,
            menuIcon = if (expanded) Icons.AutoMirrored.Filled.MenuOpen else Icons.Filled.Menu,
            menuContentDescription = if (expanded) "Collapse navigation" else "Expand navigation",
            onMenuClick = onToggle,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            content()
        }
    }
}

// ─────────────────────── Shared panel ───────────────────────

/**
 * The rail surface used by both layouts. It fills its parent's constrained size, so the
 * animated [width] from [WideLayout] (or the fixed width from [CompactLayout]) drives the
 * panel dimensions.
 *
 * Items display icon-only when [showLabels] is false (collapsed), or icon + label side-by-side
 * when [showLabels] is true (expanded).
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RailPanel(
    items: List<NavigationItemUi>,
    selectedRoute: KrocyRoute,
    showLabels: Boolean,
    onItemSelected: (NavigationItemUi) -> Unit,
    menuIcon: ImageVector,
    menuContentDescription: String,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    labelRevealProgress: Float = if (showLabels) 1f else 0f,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
        shadowElevation = if (showLabels) MaterialTheme.spacing.s1 else 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = MaterialTheme.spacing.s2),
        ) {
            // Button pinned to top-left of the panel (= screen top-left corner)
            // so it never moves as the rail animates its width
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = MaterialTheme.spacing.s4),
                onClick = onMenuClick,
                shapes = IconButtonDefaults.shapes()
            ) {
                Icon(
                    imageVector = menuIcon,
                    contentDescription = menuContentDescription,
                )
            }

            // Nav items below the 40dp button + 8dp gap
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(top = MaterialTheme.spacing.s12),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items.forEach { item ->
                    RailItem(
                        item = item,
                        selected = selectedRoute == item.route,
                        labelRevealProgress = labelRevealProgress,
                        onClick = { onItemSelected(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RailItem(
    item: NavigationItemUi,
    selected: Boolean,
    labelRevealProgress: Float,
    onClick: () -> Unit,
) {
    // Icon shifts smoothly from centered (20dp) to MD3-inset (16dp) as labels reveal
    val paddingStart = lerp(20.dp, MaterialTheme.spacing.s4, labelRevealProgress)
    val paddingEnd = lerp(0.dp, MaterialTheme.spacing.s4, labelRevealProgress)
    // Text fades in after 30% of the expansion; fully opaque at 80%
    val textAlpha = ((labelRevealProgress - 0.3f) / 0.5f).coerceIn(0f, 1f)

    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
        contentColor = if (selected) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.s2, vertical = MaterialTheme.spacing.s1),
    ) {
        Row(
            modifier = Modifier.padding(
                start = paddingStart,
                end = paddingEnd,
                top = MaterialTheme.spacing.s4,
                bottom = MaterialTheme.spacing.s4,
            ),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.contentDescription,
            )
            // Clip-grow container: layout width animates from 0 → intrinsic width,
            // revealing the label as the rail expands (no pixel compression)
            Row(
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val fullWidth = measurable.minIntrinsicWidth(constraints.maxHeight)
                        val animatedWidth = (fullWidth * labelRevealProgress).toInt()
                        val placeable = measurable.measure(
                            constraints.copy(maxWidth = fullWidth.coerceAtLeast(1)),
                        )
                        layout(animatedWidth, placeable.height) {
                            placeable.place(0, 0)
                        }
                    }
                    .clipToBounds(),
            ) {
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.s3))
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.graphicsLayer { alpha = textAlpha },
                )
            }
        }
    }
}

// ─────────────────────── Previews ───────────────────────

private val previewItems = listOf(
    NavigationItemUi(icon = Icons.Filled.Home, label = "Home", navigateTo = {}, route = SplashRoute),
    NavigationItemUi(icon = Icons.Filled.ShoppingCart, label = "Stock", navigateTo = {}, route = StockRoute),
)

@Composable
private fun PreviewContent() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            Text("Main content area")
        }
    }
}

@PreviewLightDark
@Composable
fun KrocyNavigationRailCompactClosedPreview() {
    KrocyTheme {
        KrocyNavigationRail(
            items = previewItems,
            selectedRoute = SplashRoute,
            compactExpanded = false,
            onCompactDismiss = {},
            content = { PreviewContent() },
        )
    }
}

@PreviewLightDark
@Composable
fun KrocyNavigationRailCompactOpenPreview() {
    KrocyTheme {
        KrocyNavigationRail(
            items = previewItems,
            selectedRoute = SplashRoute,
            compactExpanded = true,
            onCompactDismiss = {},
            content = { PreviewContent() },
        )
    }
}

@Preview(widthDp = 840, heightDp = 600, name = "Wide – collapsed")
@Composable
fun KrocyNavigationRailWideCollapsedPreview() {
    KrocyTheme {
        KrocyNavigationRail(
            items = previewItems,
            selectedRoute = SplashRoute,
            compactExpanded = false,
            onCompactDismiss = {},
            content = { PreviewContent() },
        )
    }
}

@Preview(widthDp = 840, heightDp = 600, name = "Wide – expanded")
@Composable
fun KrocyNavigationRailWideExpandedPreview() {
    // Start with expanded = true by default for the preview
    KrocyTheme {
        WideLayout(
            items = previewItems,
            selectedRoute = SplashRoute,
            expanded = true,
            onToggle = {},
            onItemSelected = {},
            content = { PreviewContent() },
        )
    }
}
