package com.srfmolina.krocy.ui.presentation.feature.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srfmolina.krocy.ui.presentation.common.model.ConsumptionDateUi
import com.srfmolina.krocy.ui.presentation.common.model.ConsumptionType
import com.srfmolina.krocy.ui.presentation.feature.stock.StockViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.stock.component.StockItemComp
import com.srfmolina.krocy.ui.presentation.feature.stock.model.StockItemUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing
import org.koin.compose.viewmodel.koinViewModel


@Composable
internal fun StockScreen() {
    val viewModel: StockViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.launchEvent(Event.Init)
    }
}

@Composable
private fun StockScreen(
    items: List<StockItemUi>
) {
    LazyColumn (
        modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.s4),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s6)
    ) {
        items(items) { item ->
            StockItemComp(item)
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
                        hints = List((1..3).random()) {
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
                        }
                    )
                }
            )
        }
    }
}