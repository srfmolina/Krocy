package com.srfmolina.krocy.ui.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.srfmolina.krocy.ui.presentation.common.model.LabeledActionUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing

/**
 * Material 3 Expressive Floating Action Button menu.
 *
 * The collapsed FAB shows a `+` icon that morphs into `×` when tapped, revealing a staggered column
 * of [actions]. Each action is described by a [LabeledActionUi]; tapping one closes the menu and
 * invokes its `onClick`.
 *
 * @param actions the menu entries, rendered top-to-bottom above the toggle button.
 * @param visible whether the collapsed FAB should be shown. The button stays visible while the menu
 *   is expanded regardless of this flag (e.g. to keep it on screen while the list is scrolled).
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun KrocyFabMenu(
    actions: List<LabeledActionUi>,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    FloatingActionButtonMenu(
        modifier = modifier,
        expanded = expanded,
        button = {
            ToggleFloatingActionButton(
                modifier = Modifier
                    .semantics {
                        traversalIndex = -1f
                        stateDescription = if (expanded) "Expandido" else "Contraído" // TODO
                        contentDescription = "Acciones" // TODO
                    }
                    .animateFloatingActionButton(
                        visible = visible || expanded,
                        alignment = Alignment.BottomEnd
                    ),
                checked = expanded,
                onCheckedChange = { expanded = !expanded }
            ) {
                val imageVector by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                    }
                }
                Icon(
                    painter = rememberVectorPainter(imageVector),
                    contentDescription = null,
                    modifier = Modifier.animateIcon({ checkedProgress })
                )
            }
        }
    ) {
        actions.forEachIndexed { index, action ->
            FloatingActionButtonMenuItem(
                modifier = Modifier.semantics {
                    isTraversalGroup = true
                    // The toggle button precedes the items in traversal order, so let the last
                    // item expose a custom action to close the menu.
                    if (index == actions.lastIndex) {
                        customActions = listOf(
                            CustomAccessibilityAction(
                                label = "Cerrar menú", // TODO
                                action = {
                                    expanded = false
                                    true
                                }
                            )
                        )
                    }
                },
                onClick = {
                    expanded = false
                    action.onClick()
                },
                icon = {
                    action.icon?.let { icon ->
                        Icon(imageVector = icon, contentDescription = action.contentDescription)
                    }
                },
                text = { Text(action.label) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@PreviewLightDark
@Composable
private fun KrocyFabMenuPreview() {
    KrocyTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxSize()
                .padding(MaterialTheme.spacing.s4)
        ) {
            KrocyFabMenu(
                modifier = Modifier.align(Alignment.BottomEnd),
                actions = listOf(
                    LabeledActionUi(
                        label = "Añadir producto",
                        contentDescription = "Añadir producto",
                        icon = Icons.Filled.AddBox,
                        onClick = {}
                    ),
                    LabeledActionUi(
                        label = "Escanear código",
                        contentDescription = "Escanear código",
                        icon = Icons.Filled.QrCodeScanner,
                        onClick = {}
                    ),
                    LabeledActionUi(
                        label = "Compra rápida",
                        contentDescription = "Compra rápida",
                        icon = Icons.Filled.ShoppingCart,
                        onClick = {}
                    )
                )
            )
        }
    }
}
