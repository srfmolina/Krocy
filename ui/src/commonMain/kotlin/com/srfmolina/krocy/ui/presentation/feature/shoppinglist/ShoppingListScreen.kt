package com.srfmolina.krocy.ui.presentation.feature.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srfmolina.krocy.ui.presentation.common.model.DialogConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.FabConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.IconActionUi
import com.srfmolina.krocy.ui.presentation.common.model.LabeledActionUi
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarTypeUi
import com.srfmolina.krocy.ui.presentation.common.selector.SearchableOptionSelector
import com.srfmolina.krocy.ui.presentation.common.skeleton.ProvideSkeleton
import com.srfmolina.krocy.ui.presentation.common.skeleton.SkeletonTransitionAnimation
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.ShoppingListViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.ShoppingListViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.component.ShoppingGroupCard
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.model.ShoppingGroupUi
import com.srfmolina.krocy.ui.presentation.feature.shoppinglist.model.ShoppingListEntryUi
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarTypeUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.isCompact
import com.srfmolina.krocy.ui.presentation.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ShoppingListScreen(
    onChangeTopBar: (TopBarConfigurationUi) -> Unit,
    onChangeFab: (FabConfigurationUi) -> Unit,
    onChangeDialog: (DialogConfigurationUi?) -> Unit,
    onOpenNavRail: () -> Unit,
    onShowSnackbar: (SnackbarConfigurationUi) -> Unit,
) {
    val viewModel: ShoppingListViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val gridState = rememberLazyStaggeredGridState()
    val isCompact = MaterialTheme.isCompact
    val fabVisible by remember(isCompact) {
        derivedStateOf {
            if (isCompact) {
                listState.firstVisibleItemIndex == 0 || !listState.canScrollForward
            } else {
                gridState.firstVisibleItemIndex == 0 || !gridState.canScrollForward
            }
        }
    }

    LaunchedEffect(Unit) {
        onChangeTopBar(TopBarConfigurationUi(
            title = "Lista de la compra",
            type = TopBarTypeUi.SMALL,
            leadingAction = IconActionUi(
                icon = Icons.Filled.Menu,
                contentDescription = "Abrir menú de navegación",
                onClick = onOpenNavRail
            ),
            trailingAction = IconActionUi(
                icon = Icons.Filled.Refresh,
                contentDescription = "Actualizar la lista",
                onClick = { viewModel.launchEvent(Event.OnRefresh) }
            )
        ))
        viewModel.launchEvent(Event.Init)
    }

    LaunchedEffect(state.isLoading, state.loadError, fabVisible) {
        onChangeFab(FabConfigurationUi(
            isVisible = !state.isLoading && !state.loadError && fabVisible,
            actions = listOf(
                LabeledActionUi(
                    label = "Añadir a la lista",
                    contentDescription = "Añadir un producto a la lista",
                    icon = Icons.Filled.Add,
                    onClick = { viewModel.launchEvent(Event.OnOpenAddDialog) }
                )
            )
        ))
    }

    // The dialog content reads the ViewModel itself, so the configuration only needs to be
    // re-submitted when visibility or the confirm button's enabled state changes.
    val dialogVisible = state.addDialog != null
    val dialogConfirmEnabled = state.addDialog?.isValid == true
    LaunchedEffect(dialogVisible, dialogConfirmEnabled) {
        onChangeDialog(
            if (!dialogVisible) {
                null
            } else {
                DialogConfigurationUi(
                    title = "Añadir a la lista",
                    confirmEnabled = dialogConfirmEnabled,
                    confirm = LabeledActionUi(
                        label = "Añadir",
                        contentDescription = "Añadir el producto a la lista",
                        onClick = { viewModel.launchEvent(Event.OnAddSubmit) }
                    ),
                    dismiss = LabeledActionUi(
                        label = "Cancelar",
                        contentDescription = "Cerrar sin añadir",
                        onClick = { viewModel.launchEvent(Event.OnDismissAddDialog) }
                    ),
                    content = { AddToShoppingListDialogContent(viewModel) },
                )
            }
        )
    }

    // The dialog belongs to this screen: clear it when navigating away, since unlike the
    // top bar and FAB no later screen overwrites it.
    DisposableEffect(Unit) {
        onDispose { onChangeDialog(null) }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.ProductAdded -> onShowSnackbar(
                    SnackbarConfigurationUi(message = "Añadido a la lista: «${effect.productName}»")
                )
                is Effect.ShowError -> onShowSnackbar(
                    SnackbarConfigurationUi(message = effect.message, type = SnackbarTypeUi.ERROR)
                )
            }
        }
    }

    ShoppingListScreen(
        isLoading = state.isLoading,
        loadError = state.loadError,
        groups = state.groups,
        listState = listState,
        gridState = gridState,
        onToggleDone = { entryId, done -> viewModel.launchEvent(Event.OnToggleDone(entryId, done)) },
        onRetry = { viewModel.launchEvent(Event.OnRefresh) },
    )
}

@Composable
private fun ShoppingListScreen(
    isLoading: Boolean,
    loadError: Boolean,
    groups: List<ShoppingGroupUi>,
    listState: LazyListState,
    gridState: LazyStaggeredGridState,
    onToggleDone: (Int, Boolean) -> Unit,
    onRetry: () -> Unit,
) {
    SkeletonTransitionAnimation(
        isLoading = isLoading
    ) { loading ->
        when {
            loading -> ProvideSkeleton(active = true) {
                ShoppingListGroups(groups = skeletonPlaceholders, onToggleDone = { _, _ -> })
            }
            loadError -> LoadError(onRetry = onRetry)
            groups.isEmpty() -> EmptyShoppingList()
            else -> ShoppingListGroups(
                groups = groups,
                onToggleDone = onToggleDone,
                listState = listState,
                gridState = gridState,
                contentPadding = PaddingValues(
                    top = MaterialTheme.spacing.s2,
                    bottom = MaterialTheme.spacing.s28,
                ),
            )
        }
    }
}

@Composable
private fun ShoppingListGroups(
    groups: List<ShoppingGroupUi>,
    onToggleDone: (Int, Boolean) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (MaterialTheme.isCompact) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(horizontal = MaterialTheme.spacing.s4),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s6)
        ) {
            items(groups, key = { it.key }) { group ->
                ShoppingGroupCard(
                    group = group,
                    onToggleDone = onToggleDone,
                    modifier = Modifier.animateItem(),
                )
            }
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(288.dp),
            state = gridState,
            modifier = Modifier.fillMaxSize().padding(horizontal = MaterialTheme.spacing.s4),
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s4),
            verticalItemSpacing = MaterialTheme.spacing.s4,
        ) {
            items(groups, key = { it.key }) { group ->
                ShoppingGroupCard(
                    group = group,
                    onToggleDone = onToggleDone,
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

/** An empty shopping list is success, not absence: everything is at its minimum stock. */
@Composable
private fun EmptyShoppingList() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2),
            modifier = Modifier.padding(MaterialTheme.spacing.s6),
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(MaterialTheme.spacing.s12),
            )
            Text(
                text = "Todo en stock",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "Pulsa + para añadir algo extra a la lista",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadError(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s4),
            modifier = Modifier.padding(MaterialTheme.spacing.s6),
        ) {
            Text(
                text = "No se pudo cargar la lista de la compra.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            OutlinedButton(shapes = ButtonDefaults.shapes(), onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

/** Body of the app-hosted add dialog; reads the ViewModel directly so the form stays live. */
@Composable
private fun AddToShoppingListDialogContent(viewModel: ShoppingListViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dialog = state.addDialog ?: return

    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s4)) {
        SearchableOptionSelector(
            label = "Producto",
            options = dialog.products,
            selectedId = dialog.selectedProductId,
            onOptionSelected = { viewModel.launchEvent(Event.OnAddProductSelected(it)) },
            required = true,
            modifier = Modifier.fillMaxWidth(),
        )
        if (dialog.products.isError) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3),
            ) {
                Text(
                    text = "No se pudieron cargar los productos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = { viewModel.launchEvent(Event.OnRetryLoadProducts) }) {
                    Text("Reintentar")
                }
            }
        }
        val showAmountError = dialog.amount.isNotBlank() && !dialog.amountValid
        OutlinedTextField(
            value = dialog.amount,
            onValueChange = { viewModel.launchEvent(Event.OnAddAmountChange(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Cantidad *") },
            singleLine = true,
            isError = showAmountError,
            supportingText = if (showAmountError) {
                { Text("Número no válido") }
            } else {
                null
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )
    }
}

/**
 * Representative dummy groups used only to drive the loading skeleton's layout (varied group
 * sizes and name lengths), so the placeholder matches the real list's shape.
 */
private val skeletonPlaceholders: List<ShoppingGroupUi> = listOf(
    ShoppingGroupUi(
        name = "Frutas y verduras",
        entries = listOf(
            ShoppingListEntryUi(0, "Manzanas", "4 uds", done = false),
            ShoppingListEntryUi(1, "Plátanos", "1 racimo", done = false),
            ShoppingListEntryUi(2, "Tomates", "6 uds", done = false),
        ),
    ),
    ShoppingGroupUi(
        name = "Lácteos",
        entries = listOf(
            ShoppingListEntryUi(3, "Leche entera", "3 briks", done = false),
            ShoppingListEntryUi(4, "Yogur natural", "8 uds", done = false),
        ),
    ),
    ShoppingGroupUi(
        name = "Despensa",
        entries = listOf(
            ShoppingListEntryUi(5, "Arroz", "1 paquete", done = false),
            ShoppingListEntryUi(6, "Aceite de oliva", "1 botella", done = false),
        ),
    ),
)

// PREVIEW //

@PreviewLightDark
@Composable
private fun ShoppingListScreenPreview() {
    KrocyTheme {
        Surface {
            ShoppingListScreen(
                isLoading = false,
                loadError = false,
                groups = listOf(
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
                            ShoppingListEntryUi(4, "Leche entera", "3 briks", done = false),
                            ShoppingListEntryUi(5, "Yogur natural", "8 uds", done = false),
                        ),
                    ),
                    ShoppingGroupUi(
                        name = null,
                        entries = listOf(
                            ShoppingListEntryUi(6, "Pan de molde", "1 paquete", done = false),
                        ),
                    ),
                ),
                listState = rememberLazyListState(),
                gridState = rememberLazyStaggeredGridState(),
                onToggleDone = { _, _ -> },
                onRetry = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ShoppingListScreenEmptyPreview() {
    KrocyTheme {
        Surface {
            ShoppingListScreen(
                isLoading = false,
                loadError = false,
                groups = emptyList(),
                listState = rememberLazyListState(),
                gridState = rememberLazyStaggeredGridState(),
                onToggleDone = { _, _ -> },
                onRetry = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ShoppingListScreenSkeletonPreview() {
    KrocyTheme {
        Surface {
            ShoppingListScreen(
                isLoading = true,
                loadError = false,
                groups = emptyList(),
                listState = rememberLazyListState(),
                gridState = rememberLazyStaggeredGridState(),
                onToggleDone = { _, _ -> },
                onRetry = {},
            )
        }
    }
}
