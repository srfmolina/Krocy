package com.srfmolina.krocy.ui.presentation.feature.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srfmolina.krocy.domain.model.common.ConsumptionType
import com.srfmolina.krocy.ui.presentation.common.model.ConsumptionDateUi
import com.srfmolina.krocy.ui.presentation.common.model.IconActionUi
import com.srfmolina.krocy.ui.presentation.common.skeleton.ProvideSkeleton
import com.srfmolina.krocy.ui.presentation.common.skeleton.SkeletonTransitionAnimation
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.stock.component.StockItemComp
import com.srfmolina.krocy.ui.presentation.feature.stock.model.StockItemUi
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarTypeUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.isCompact
import com.srfmolina.krocy.ui.presentation.theme.spacing
import org.koin.compose.viewmodel.koinViewModel


@Composable
internal fun StockScreen(
    onChangeTopBar: (TopBarConfigurationUi) -> Unit,
    onOpenNavRail: () -> Unit
) {
    val viewModel: StockViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        onChangeTopBar(TopBarConfigurationUi(
            title = "Resumen del inventario",
            type = TopBarTypeUi.SMALL,
            leadingAction = IconActionUi(
                icon = Icons.Filled.Menu,
                contentDescription = "Localized description", //TODO
                onClick = onOpenNavRail
            )
        ))
        viewModel.launchEvent(Event.Init)
    }

    StockScreen(
        isLoading = state.isLoading,
        isLoadingItem = state.isLoadingItem,
        items = state.items,
        onConsume = { productId -> viewModel.launchEvent(Event.OnConsumeOne(productId)) },
        onAdd = { productId -> viewModel.launchEvent(Event.OnAddOne(productId)) },
        onOpen = { productId -> viewModel.launchEvent(Event.OnOpenOne(productId)) }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StockScreen(
    isLoading: Boolean,
    isLoadingItem: Int?,
    items: List<StockItemUi>,
    onConsume: (Int) -> Unit,
    onAdd: (Int) -> Unit,
    onOpen: (Int) -> Unit
) {
    SkeletonTransitionAnimation(
        isLoading = isLoading
    ) { loading ->
        if (loading) {
            ProvideSkeleton(active = true) {
                StockList(items = skeletonPlaceholders, onConsume = {}, onAdd = {}, onOpen = {})
            }
        } else {
            ProvideSkeleton(active = isLoadingItem != null, targetIds = isLoadingItem?.let {listOf(isLoadingItem)}) {
                StockList(items = items, onConsume = onConsume, onAdd = onAdd, onOpen = onOpen)
            }
        }
    }
}

@Composable
private fun StockList(
    items: List<StockItemUi>,
    onConsume: (Int) -> Unit,
    onAdd: (Int) -> Unit,
    onOpen: (Int) -> Unit
) {
    if (MaterialTheme.isCompact) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = MaterialTheme.spacing.s4),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3)
        ) {
            items(items) { item ->
                StockItemComp(
                    modifier = Modifier.padding(MaterialTheme.spacing.s3),
                    item = item,
                    onConsume = onConsume,
                    onAdd = onAdd,
                    onOpen = onOpen
                )
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(items) { index, item ->
                StockItemComp(
                    modifier = Modifier.padding(MaterialTheme.spacing.s4),
                    item = item,
                    onConsume = onConsume,
                    onAdd = onAdd,
                    onOpen = onOpen
                )
                if (index < items.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

/**
 * Representative dummy items used only to drive the loading skeleton's layout (varied name lengths,
 * hint counts and an expiry chip), so the placeholder matches the real list's shape.
 */
private val skeletonPlaceholders: List<StockItemUi> = listOf(
    StockItemUi(0, "Galletas María", listOf("1 abierto"), ConsumptionDateUi(ConsumptionType.PREFERENCE, "En 2 días", false), "3 Packs"),
    StockItemUi(1, "Yogur natural", listOf("8 unidades", "2 abiertos"), ConsumptionDateUi(ConsumptionType.EXPIRATION, "En 2 días", false), "3 uds"),
    StockItemUi(2, "Leche entera", listOf("3 briks"), ConsumptionDateUi(ConsumptionType.EXPIRATION, "Hace 1 día", true), "2 uds"),
    StockItemUi(3, "Pan de molde", emptyList(), null, "1 paquete"),
    StockItemUi(4, "Queso curado", listOf("1 abierto"), ConsumptionDateUi(ConsumptionType.PREFERENCE, "En 12 días", false), "250 g"),
    StockItemUi(5, "Huevos camperos", listOf("6 unidades"), null, "1 docena"),
    StockItemUi(6, "Mantequilla", emptyList(), ConsumptionDateUi(ConsumptionType.EXPIRATION, "En 5 días", false), "1 ud"),
)

@PreviewLightDark
@Composable
private fun StockScreenPreview() {
    KrocyTheme {
        Surface {
            StockScreen(
                isLoading = false,
                isLoadingItem = null,
                onConsume = {},
                onAdd = {},
                onOpen = {},
                items = List(15) {
                    StockItemUi(
                        id = (1..1000).random(),
                        name = listOf(
                            "Apple",
                            "Banana",
                            "Milk",
                            "Bread",
                            "Cheese",
                            "Eggs",
                            "Butter"
                        ).random(),
                        hints = List((2..8).random()) {
                            listOf(
                                "fresh",
                                "organic",
                                "frozen",
                                "imported",
                                "local",
                                "gluten-free"
                            ).random()
                        },
                        consumptionDate = if (listOf(true, false).random()) {
                            ConsumptionDateUi(
                                type = ConsumptionType.entries.random(),
                                date = listOf(
                                    "En 2 días",
                                    "Hace 1 semana",
                                    "En 4 días",
                                    "Hace 2 días"
                                ).random(),
                                expired = listOf(true, false).random()
                            )
                        } else {
                            null
                        },
                        quantity = "3 Packs"
                    )
                }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StockScreenSkeletonPreview() {
    KrocyTheme {
        Surface {
            StockScreen(
                isLoading = true,
                isLoadingItem = null,
                onConsume = {},
                onAdd = {},
                onOpen = {},
                items = emptyList()
            )
        }
    }
}