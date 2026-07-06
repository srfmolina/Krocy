package com.srfmolina.krocy.ui.presentation.feature.creation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material3.Switch
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
import com.srfmolina.krocy.ui.presentation.common.CollapsibleFormSection
import com.srfmolina.krocy.ui.presentation.common.FormSection
import com.srfmolina.krocy.ui.presentation.common.UnitEquationCard
import com.srfmolina.krocy.ui.presentation.common.UnitEquationInputCard
import com.srfmolina.krocy.ui.presentation.common.model.FabConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.IconActionUi
import com.srfmolina.krocy.ui.presentation.common.model.OptionsUi
import com.srfmolina.krocy.ui.presentation.common.model.QuConversionUi
import com.srfmolina.krocy.ui.presentation.common.model.SelectableOptionUi
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarTypeUi
import com.srfmolina.krocy.ui.presentation.common.selector.OptionSelector
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
        viewModel.launchEvent(Event.Init)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.ProductCreated -> {
                    if (effect.conversionWarning) {
                        onShowSnackbar(
                            SnackbarConfigurationUi(
                                message = "Producto creado, pero no se pudo crear la conversión de unidades. Añádela en Grocy.",
                                type = SnackbarTypeUi.WARNING,
                            )
                        )
                    }
                    onProductCreated(effect.name)
                }
                is Effect.ShowError -> onShowSnackbar(
                    SnackbarConfigurationUi(message = effect.message, type = SnackbarTypeUi.ERROR)
                )
            }
        }
    }

    CreateProductContent(state = state, onEvent = viewModel::launchEvent)
}

@Composable
private fun CreateProductContent(
    state: State,
    onEvent: (Event) -> Unit,
) {
    when {
        state.isLoadingOptions -> LoadingOptions()
        state.optionsError -> OptionsError(onRetry = { onEvent(Event.OnRetryLoadOptions) })
        else -> CreateProductForm(state = state, onEvent = onEvent)
    }
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
    onEvent: (Event) -> Unit,
) {
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
                        onValueChange = { onEvent(Event.OnNameChange(it)) },
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
                        onValueChange = { onEvent(Event.OnDescriptionChange(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Descripción") },
                        minLines = 3,
                        maxLines = 5,
                    )
                }

                FormSection(title = "Unidades") {
                    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3)) {
                        OptionSelector(
                            label = "Stock",
                            options = state.quantityUnits,
                            selectedId = state.stockUnitId,
                            onOptionSelected = { it?.let { id -> onEvent(Event.OnStockUnitSelected(id)) } },
                            required = true,
                            modifier = Modifier.weight(1f),
                        )
                        OptionSelector(
                            label = "Compra",
                            options = state.quantityUnits,
                            selectedId = state.purchaseUnitId,
                            onOptionSelected = { it?.let { id -> onEvent(Event.OnPurchaseUnitSelected(id)) } },
                            required = true,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    AnimatedVisibility(
                        visible = state.unitsDiffer,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        ConversionCard(state = state, onEvent = onEvent)
                    }
                }

                FormSection(title = "Almacenamiento") {
                    OptionSelector(
                        label = "Ubicación",
                        options = state.locations,
                        selectedId = state.locationId,
                        onOptionSelected = { it?.let { id -> onEvent(Event.OnLocationSelected(id)) } },
                        required = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3)) {
                        OptionSelector(
                            label = "Grupo",
                            options = state.productGroups,
                            selectedId = state.productGroupId,
                            onOptionSelected = { onEvent(Event.OnProductGroupSelected(it)) },
                            includeNoneOption = true,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = state.minStockAmount,
                            onValueChange = { onEvent(Event.OnMinStockChange(it)) },
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

                CollapsibleFormSection(
                    title = "Avanzado",
                    expanded = state.advancedExpanded,
                    onToggle = { onEvent(Event.OnToggleAdvanced) },
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3)) {
                        OutlinedTextField(
                            value = state.defaultBestBeforeDays,
                            onValueChange = { onEvent(Event.OnBestBeforeDaysChange(it)) },
                            modifier = Modifier.weight(1f),
                            label = { Text("Caducidad por defecto (días)") },
                            singleLine = true,
                            isError = !state.bestBeforeDaysValid,
                            supportingText = if (!state.bestBeforeDaysValid) {
                                { Text("Número no válido") }
                            } else {
                                null
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        OutlinedTextField(
                            value = state.defaultBestBeforeDaysAfterOpen,
                            onValueChange = { onEvent(Event.OnBestBeforeDaysAfterOpenChange(it)) },
                            modifier = Modifier.weight(1f),
                            label = { Text("Caducidad tras abrir (días)") },
                            singleLine = true,
                            isError = !state.bestBeforeDaysAfterOpenValid,
                            supportingText = if (!state.bestBeforeDaysAfterOpenValid) {
                                { Text("Número no válido") }
                            } else {
                                null
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "No congelar",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = "Avisar si este producto se mueve a un congelador",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = state.shouldNotBeFrozen,
                            onCheckedChange = { onEvent(Event.OnShouldNotBeFrozenChange(it)) },
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)) {
                    Button(
                        onClick = { onEvent(Event.OnSubmit) },
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
                    if (!state.isValid) {
                        Text(
                            text = "Falta: ${state.missingFields.joinToString()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.s1),
                        )
                    }
                }

                Spacer(Modifier.height(MaterialTheme.spacing.s8))
            }
        }
    }
}

@Composable
private fun ConversionCard(
    state: State,
    onEvent: (Event) -> Unit,
) {
    val from = state.purchaseUnitName.orEmpty()
    val to = state.stockUnitName.orEmpty()
    val existing = state.existingConversionFactor
    if (existing != null) {
        UnitEquationCard(
            fromUnitName = from,
            toUnitName = to,
            factor = existing,
            caption = "Conversión global existente",
        )
    } else {
        UnitEquationInputCard(
            fromUnitName = from,
            toUnitName = to,
            value = state.conversionFactor,
            onValueChange = { onEvent(Event.OnConversionFactorChange(it)) },
            caption = "Se creará una conversión para este producto",
            isError = state.conversionFactor.isNotBlank() && !state.conversionFactorValid,
        )
    }
}

private val previewUnits = OptionsUi(
    options = listOf(
        SelectableOptionUi(1, "Unidad"),
        SelectableOptionUi(2, "Paquete"),
        SelectableOptionUi(3, "Gramo"),
    ),
    isLoading = false,
)
private val previewLocations = OptionsUi(
    options = listOf(
        SelectableOptionUi(10, "Despensa"),
        SelectableOptionUi(11, "Nevera"),
        SelectableOptionUi(12, "Congelador"),
    ),
    isLoading = false,
)
private val previewGroups = OptionsUi(
    options = listOf(
        SelectableOptionUi(20, "Lácteos"),
        SelectableOptionUi(21, "Bebidas"),
    ),
    isLoading = false,
)

private fun previewState() = State(
    quantityUnits = previewUnits,
    locations = previewLocations,
    productGroups = previewGroups,
    conversionsLoading = false,
    name = "Leche entera",
    stockUnitId = 1,
    purchaseUnitId = 1,
    locationId = 11,
)

@PreviewLightDark
@Composable
private fun CreateProductFormPreview() {
    KrocyTheme {
        Surface {
            CreateProductContent(state = previewState(), onEvent = {})
        }
    }
}

@PreviewLightDark
@Composable
private fun CreateProductFormNewConversionPreview() {
    KrocyTheme {
        Surface {
            CreateProductContent(
                state = previewState().copy(purchaseUnitId = 2, conversionFactor = "6"),
                onEvent = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CreateProductFormExistingConversionPreview() {
    KrocyTheme {
        Surface {
            CreateProductContent(
                state = previewState().copy(
                    purchaseUnitId = 2,
                    conversions = listOf(QuConversionUi(fromQuId = 2, toQuId = 1, factor = 6.0)),
                ),
                onEvent = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CreateProductFormAdvancedPreview() {
    KrocyTheme {
        Surface {
            CreateProductContent(
                state = previewState().copy(
                    advancedExpanded = true,
                    defaultBestBeforeDays = "7",
                    shouldNotBeFrozen = true,
                ),
                onEvent = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CreateProductLoadingPreview() {
    KrocyTheme {
        Surface {
            CreateProductContent(state = State(), onEvent = {})
        }
    }
}

@PreviewLightDark
@Composable
private fun CreateProductErrorPreview() {
    KrocyTheme {
        Surface {
            CreateProductContent(
                state = State(
                    quantityUnits = OptionsUi(isLoading = false, isError = true),
                    locations = OptionsUi(isLoading = false, isError = true),
                    productGroups = OptionsUi(isLoading = false, isError = true),
                    conversionsLoading = false,
                    conversionsError = true,
                ),
                onEvent = {},
            )
        }
    }
}
