package com.srfmolina.krocy.ui.presentation.feature.creation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srfmolina.krocy.ui.presentation.common.FormSection
import com.srfmolina.krocy.ui.presentation.common.model.FabConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.IconActionUi
import com.srfmolina.krocy.ui.presentation.common.model.SelectableOptionUi
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarTypeUi
import com.srfmolina.krocy.ui.presentation.common.selector.OptionSelector
import com.srfmolina.krocy.ui.presentation.common.selector.group.ProductGroupSelectorViewModel
import com.srfmolina.krocy.ui.presentation.common.selector.location.LocationSelectorViewModel
import com.srfmolina.krocy.ui.presentation.common.selector.stock.unit.StockUnitSelectorViewModel
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.creation.CreateProductViewModel.State
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarTypeUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.isCompact
import com.srfmolina.krocy.ui.presentation.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CreateProductScreen(
    onBack: () -> Unit,
    onProductCreated: (String) -> Unit,
    onChangeTopBar: (TopBarConfigurationUi) -> Unit,
    onChangeFab: (FabConfigurationUi) -> Unit,
    onShowSnackbar: (SnackbarConfigurationUi) -> Unit,
) {
    val viewModel: CreateProductViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        onChangeTopBar(
            TopBarConfigurationUi(
                title = "Nuevo producto",
                type = TopBarTypeUi.SMALL,
                leadingAction = IconActionUi(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    onClick = onBack,
                ),
            )
        )
        // The create screen has its own bottom button, so hide the global FAB carried over from Stock.
        onChangeFab(FabConfigurationUi(isVisible = false, actions = emptyList()))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.ProductCreated -> onProductCreated(effect.name)
                is Effect.ShowError -> onShowSnackbar(
                    SnackbarConfigurationUi(message = effect.message, type = SnackbarTypeUi.ERROR)
                )
            }
        }
    }

    CreateProductContent(
        state = state,
        onRetry = { TODO("Not yet implemented") },
        onNameChange = { viewModel.launchEvent(Event.OnNameChange(it)) },
        onDescriptionChange = { viewModel.launchEvent(Event.OnDescriptionChange(it)) },
        onStockUnitSelected = { it?.let { id -> viewModel.launchEvent(Event.OnStockUnitSelected(id)) } },
        onPurchaseUnitSelected = { it?.let { id -> viewModel.launchEvent(Event.OnPurchaseUnitSelected(id)) } },
        onLocationSelected = { it?.let { id -> viewModel.launchEvent(Event.OnLocationSelected(id)) } },
        onProductGroupSelected = { viewModel.launchEvent(Event.OnProductGroupSelected(it)) },
        onMinStockChange = { viewModel.launchEvent(Event.OnMinStockChange(it)) },
        onSubmit = { viewModel.launchEvent(Event.OnSubmit) },
    )
}

@Composable
private fun CreateProductContent(
    state: State,
    onRetry: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStockUnitSelected: (Int?) -> Unit,
    onPurchaseUnitSelected: (Int?) -> Unit,
    onLocationSelected: (Int?) -> Unit,
    onProductGroupSelected: (Int?) -> Unit,
    onMinStockChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    CreateProductForm(
        state = state,
        onNameChange = onNameChange,
        onDescriptionChange = onDescriptionChange,
        onStockUnitSelected = onStockUnitSelected,
        onPurchaseUnitSelected = onPurchaseUnitSelected,
        onLocationSelected = onLocationSelected,
        onProductGroupSelected = onProductGroupSelected,
        onMinStockChange = onMinStockChange,
        onSubmit = onSubmit,
    )

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadingOptions() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingIndicator(modifier = Modifier.size(MaterialTheme.spacing.s18))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OptionsError(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s4),
            modifier = Modifier.padding(MaterialTheme.spacing.s6),
        ) {
            Text(
                text = "No se pudieron cargar los datos del formulario.",
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CreateProductForm(
    state: State,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStockUnitSelected: (Int?) -> Unit,
    onPurchaseUnitSelected: (Int?) -> Unit,
    onLocationSelected: (Int?) -> Unit,
    onProductGroupSelected: (Int?) -> Unit,
    onMinStockChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {

    val stockUnitSelectorViewModel: StockUnitSelectorViewModel = koinViewModel()
    val locationSelectorViewModel: LocationSelectorViewModel = koinViewModel()
    val productGroupSelectorViewModel: ProductGroupSelectorViewModel = koinViewModel()

    val contentModifier = if (MaterialTheme.isCompact) {
        Modifier.fillMaxWidth()
    } else {
        Modifier.widthIn(max = 600.dp).fillMaxWidth()
    }

    val entranceState = remember { MutableTransitionState(false).apply { targetState = true } }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        AnimatedVisibility(
            visibleState = entranceState,
            enter = fadeIn() + slideInVertically { it / 12 },
        ) {
            Column(
                modifier = contentModifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = MaterialTheme.spacing.s4),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s6),
            ) {
                Spacer(Modifier.height(MaterialTheme.spacing.s2))

                FormSection(title = "Identidad") {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = onNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nombre *") },
                        singleLine = true,
                        isError = state.showNameError,
                        supportingText = if (state.showNameError) {
                            { Text("El nombre es obligatorio") }
                        } else {
                            null
                        },
                    )
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = onDescriptionChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Descripción") },
                        minLines = 3,
                        maxLines = 5,
                    )
                }

                FormSection(title = "Unidades") {
                    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3)) {
                        OptionSelector(
                            viewModel = stockUnitSelectorViewModel,
                            label = "Stock",
                            selectedId = state.selectedStockUnitId,
                            onOptionSelected = onStockUnitSelected,
                            modifier = Modifier.weight(1f),
                        )
                        OptionSelector(
                            viewModel = stockUnitSelectorViewModel,
                            label = "Purchase",
                            selectedId = state.selectedPurchaseUnitId,
                            onOptionSelected = onPurchaseUnitSelected,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                FormSection(title = "Almacenamiento") {
                    OptionSelector(
                        viewModel = locationSelectorViewModel,
                        label = "Ubicación",
                        selectedId = state.selectedLocationId,
                        modifier = Modifier.fillMaxWidth(),
                        onOptionSelected = onLocationSelected,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3)) {
                        OptionSelector(
                            viewModel = productGroupSelectorViewModel,
                            label = "Grupo",
                            selectedId = state.selectedProductGroupId,
                            onOptionSelected = onProductGroupSelected,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = state.minStockAmount,
                            onValueChange = onMinStockChange,
                            modifier = Modifier.weight(1f),
                            label = { Text("Stock mínimo") },
                            singleLine = true,
                            isError = !state.minStockValid,
                            supportingText = if (!state.minStockValid) {
                                { Text("Número no válido") }
                            } else {
                                null
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        )
                    }
                }

                Button(
                    onClick = onSubmit,
                    shapes = ButtonDefaults.shapes(),
                    enabled = state.isValid && !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text("Crear producto")
                    }
                }

                Spacer(Modifier.height(MaterialTheme.spacing.s8))
            }
        }
    }
}

private val previewUnits = listOf(
    SelectableOptionUi(1, "Unidad"),
    SelectableOptionUi(2, "Paquete"),
    SelectableOptionUi(3, "Gramo"),
)
private val previewLocations = listOf(
    SelectableOptionUi(10, "Despensa"),
    SelectableOptionUi(11, "Nevera"),
    SelectableOptionUi(12, "Congelador"),
)
private val previewGroups = listOf(
    SelectableOptionUi(20, "Lácteos"),
    SelectableOptionUi(21, "Bebidas"),
)

@PreviewLightDark
@Composable
private fun CreateProductFormPreview() {
    KrocyTheme {
        Surface {
            CreateProductContent(
                state = State(
                    quantityUnits = previewUnits,
                    locations = previewLocations,
                    productGroups = previewGroups,
                    name = "Leche entera",
                    selectedStockUnitId = 1,
                    selectedPurchaseUnitId = 2,
                    selectedLocationId = 11,
                ),
                onRetry = {},
                onNameChange = {},
                onDescriptionChange = {},
                onStockUnitSelected = {},
                onPurchaseUnitSelected = {},
                onLocationSelected = {},
                onProductGroupSelected = {},
                onMinStockChange = {},
                onSubmit = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CreateProductLoadingPreview() {
    KrocyTheme {
        Surface {
            CreateProductContent(
                state = State(),
                onRetry = {},
                onNameChange = {},
                onDescriptionChange = {},
                onStockUnitSelected = {},
                onPurchaseUnitSelected = {},
                onLocationSelected = {},
                onProductGroupSelected = {},
                onMinStockChange = {},
                onSubmit = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CreateProductErrorPreview() {
    KrocyTheme {
        Surface {
            CreateProductContent(
                state = State(),
                onRetry = {},
                onNameChange = {},
                onDescriptionChange = {},
                onStockUnitSelected = {},
                onPurchaseUnitSelected = {},
                onLocationSelected = {},
                onProductGroupSelected = {},
                onMinStockChange = {},
                onSubmit = {},
            )
        }
    }
}
