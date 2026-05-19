package com.srfmolina.krocy.ui.presentation.feature.stock

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import com.srfmolina.krocy.ui.presentation.common.model.ActionUi
import com.srfmolina.krocy.ui.presentation.common.model.ConsumptionDateUi
import com.srfmolina.krocy.ui.presentation.common.model.DisplaySizeUi
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.stock.component.StockItemComp
import com.srfmolina.krocy.ui.presentation.feature.stock.model.StockItemUi
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarTypeUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.displaySize
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
            leadingAction = ActionUi(
                icon = Icons.Filled.Menu,
                contentDescription = "Localized description", //TODO
                onClick = onOpenNavRail
            )
        ))
        viewModel.launchEvent(Event.Init)
    }

    StockScreen(items = state.items)
}

@Composable
private fun StockScreen(
    items: List<StockItemUi>
) {
    if (MaterialTheme.displaySize == DisplaySizeUi.S) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = MaterialTheme.spacing.s4),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3)
        ) {
            items(items) { item ->
                StockItemComp(item)
            }
        }
    } else {
        @OptIn(ExperimentalFoundationApi::class)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(items) { index, item ->
                StockItemComp(item)
                if (index < items.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun StockScreenPreview() {
    KrocyTheme {
        Surface {
            StockScreen(
                List(15) {
                    StockItemUi(
                        id = (1..1000).random(),
                        name = listOf("Apple", "Banana", "Milk", "Bread", "Cheese", "Eggs", "Butter").random(),
                        hints = List((2..8).random()) {
                            listOf("fresh", "organic", "frozen", "imported", "local", "gluten-free").random()
                        },
                        consumptionDate = if (listOf(true, false).random()) {
                            ConsumptionDateUi(
                                type = ConsumptionType.entries.random(),
                                date = listOf("En 2 días", "Hace 1 semana", "En 4 días", "Hace 2 días").random(),
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