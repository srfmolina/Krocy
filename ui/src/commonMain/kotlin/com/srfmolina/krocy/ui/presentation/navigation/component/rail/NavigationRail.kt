package com.srfmolina.krocy.ui.presentation.navigation.component.rail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRailDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.srfmolina.krocy.ui.presentation.navigation.KrocyRoute
import com.srfmolina.krocy.ui.presentation.navigation.NavigationItemUi
import com.srfmolina.krocy.ui.presentation.navigation.SplashRoute
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme

private val COMPACT_BREAKPOINT = 600.dp
private val RAIL_COLLAPSED_WIDTH = 80.dp
private val RAIL_EXPANDED_WIDTH = 240.dp
private const val ANIM_DURATION_MS = 300

/**
 * Adaptive navigation rail that adjusts its behaviour based on available screen width.
 *
 * - **Compact** (< 600 dp): the rail is hidden. A floating hamburger icon appears at the
 *   top-start corner; tapping it slides the rail in as a modal overlay with a dismissible
 *   scrim. Selecting a destination or tapping the scrim collapses the overlay.
 *
 * - **Wide** (≥ 600 dp): the rail is always visible in its collapsed form (icon-only, 80 dp).
 *   Tapping the header menu icon expands it to full width (240 dp) with destination labels.
 *
 * @param items             Ordered list of navigation destinations.
 * @param selectedRoute     Currently active destination.
 * @param modifier          Modifier applied to the root container.
 * @param content           Main screen content rendered beside or behind the rail.
 */
@Composable
internal fun KrocyNavigationRail(
    items: List<NavigationItemUi>,
    selectedRoute: KrocyRoute,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isCompact = maxWidth < COMPACT_BREAKPOINT
        var expanded by rememberSaveable { mutableStateOf(false) }

        if (isCompact) {
            CompactLayout(
                items = items,
                selectedRoute = selectedRoute,
                expanded = expanded,
                onExpand = { expanded = true },
                onCollapse = { expanded = false },
                onItemSelected = { item ->
                    item.navigateTo()
                    expanded = false
                },
                content = content,
            )
        } else {
            WideLayout(
                items = items,
                selectedRoute = selectedRoute,
                expanded = expanded,
                onToggle = { expanded = !expanded },
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
    onExpand: () -> Unit,
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

        // Floating hamburger button — shown only while the modal is closed
        AnimatedVisibility(
            visible = !expanded,
            enter = fadeIn(tween(ANIM_DURATION_MS)),
            exit = fadeOut(tween(ANIM_DURATION_MS)),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp),
        ) {
            IconButton(onClick = onExpand) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Open navigation menu",
                )
            }
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

    Row(modifier = Modifier.fillMaxSize()) {
        RailPanel(
            modifier = Modifier
                .fillMaxHeight()
                .width(railWidth),
            items = items,
            selectedRoute = selectedRoute,
            showLabels = expanded,
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
@OptIn(ExperimentalMaterial3Api::class)
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
) {
    Surface(
        modifier = modifier,
        color = NavigationRailDefaults.ContainerColor,
        shadowElevation = if (showLabels) 4.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = menuIcon,
                    contentDescription = menuContentDescription,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            items.forEach { item ->
                RailItem(
                    item = item,
                    selected = selectedRoute == item.route,
                    showLabel = showLabels,
                    onClick = { onItemSelected(item) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RailItem(
    item: NavigationItemUi,
    selected: Boolean,
    showLabel: Boolean,
    onClick: () -> Unit,
) {
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
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (showLabel) 16.dp else 0.dp,
                vertical = 16.dp,
            ),
            horizontalArrangement = if (showLabel) Arrangement.Start else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.contentDescription,
            )
            if (showLabel) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

// ─────────────────────── Previews ───────────────────────

private val previewItems = listOf(
    NavigationItemUi(icon = Icons.Filled.Home, label = "Home", navigateTo = {}, route = SplashRoute),
    NavigationItemUi(icon = Icons.Filled.ShoppingCart, label = "Stock", navigateTo = {}, route = SplashRoute),
)

@Composable
private fun PreviewContent() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("Main content area")
        }
    }
}

@PreviewLightDark
@Composable
fun KrocyNavigationRailCompactPreview() {
    KrocyTheme {
        KrocyNavigationRail(
            items = previewItems,
            selectedRoute = SplashRoute,
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
