package com.srfmolina.krocy.ui.presentation.feature.purchase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.srfmolina.krocy.ui.presentation.common.CollapsibleFormSection
import com.srfmolina.krocy.ui.presentation.common.FormSection
import com.srfmolina.krocy.ui.presentation.common.KrocyDropdownField
import com.srfmolina.krocy.ui.presentation.common.model.FabConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.IconActionUi
import com.srfmolina.krocy.ui.presentation.common.model.OptionsUi
import com.srfmolina.krocy.ui.presentation.common.model.SelectableOptionUi
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarConfigurationUi
import com.srfmolina.krocy.ui.presentation.common.model.SnackbarTypeUi
import com.srfmolina.krocy.ui.presentation.common.selector.OptionSelector
import com.srfmolina.krocy.ui.presentation.common.selector.SearchableOptionSelector
import com.srfmolina.krocy.ui.presentation.feature.purchase.PurchaseViewModel.Effect
import com.srfmolina.krocy.ui.presentation.feature.purchase.PurchaseViewModel.Event
import com.srfmolina.krocy.ui.presentation.feature.purchase.PurchaseViewModel.State
import com.srfmolina.krocy.ui.presentation.feature.purchase.model.ProductPurchaseInfoUi
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarConfigurationUi
import com.srfmolina.krocy.ui.presentation.navigation.component.topbar.model.TopBarTypeUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.isCompact
import com.srfmolina.krocy.ui.presentation.theme.spacing
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun PurchaseScreen(
    onBack: () -> Unit,
    onPurchaseRegistered: (String) -> Unit,
    onChangeTopBar: (TopBarConfigurationUi) -> Unit,
    onChangeFab: (FabConfigurationUi) -> Unit,
    onShowSnackbar: (SnackbarConfigurationUi) -> Unit,
) {
    val viewModel: PurchaseViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        onChangeTopBar(
            TopBarConfigurationUi(
                title = "Registrar compra",
                type = TopBarTypeUi.SMALL,
                leadingAction = IconActionUi(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    onClick = onBack,
                ),
            )
        )
        // The purchase screen has its own bottom button, so hide the global FAB carried over from Stock.
        onChangeFab(FabConfigurationUi(isVisible = false, actions = emptyList()))
        viewModel.launchEvent(Event.Init)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is Effect.PurchaseRegistered -> onPurchaseRegistered(effect.productName)
                is Effect.ShowError -> onShowSnackbar(
                    SnackbarConfigurationUi(message = effect.message, type = SnackbarTypeUi.ERROR)
                )
            }
        }
    }

    PurchaseContent(state = state, onEvent = viewModel::launchEvent)
}

@Composable
private fun PurchaseContent(
    state: State,
    onEvent: (Event) -> Unit,
) {
    when {
        state.isLoadingOptions -> LoadingOptions()
        state.optionsError -> OptionsError(onRetry = { onEvent(Event.OnRetryLoadOptions) })
        else -> PurchaseForm(state = state, onEvent = onEvent)
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
private fun PurchaseForm(
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

                FormSection(title = "Producto") {
                    SearchableOptionSelector(
                        label = "Producto",
                        options = state.products,
                        selectedId = state.selectedProductId,
                        onOptionSelected = { onEvent(Event.OnProductSelected(it)) },
                        required = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    ProductInfoStatus(state = state, onEvent = onEvent)
                }

                FormSection(title = "Compra") {
                    AmountRow(state = state, onEvent = onEvent)
                    DueDateField(
                        dueDate = state.dueDate,
                        neverOverdue = state.neverOverdue,
                        onDateChange = { onEvent(Event.OnDueDateChange(it)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Nunca caduca",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = "El producto no tiene fecha de caducidad",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = state.neverOverdue,
                            onCheckedChange = { onEvent(Event.OnNeverOverdueChange(it)) },
                        )
                    }
                }

                CollapsibleFormSection(
                    title = "Más detalles",
                    expanded = state.advancedExpanded,
                    onToggle = { onEvent(Event.OnToggleAdvanced) },
                ) {
                    PriceFields(state = state, onEvent = onEvent)
                    OptionSelector(
                        label = "Tienda",
                        options = state.shoppingLocations,
                        selectedId = state.storeId,
                        onOptionSelected = { onEvent(Event.OnStoreSelected(it)) },
                        includeNoneOption = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OptionSelector(
                        label = "Ubicación",
                        options = state.locations,
                        selectedId = state.locationId,
                        onOptionSelected = { onEvent(Event.OnLocationSelected(it)) },
                        includeNoneOption = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = state.note,
                        onValueChange = { onEvent(Event.OnNoteChange(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nota") },
                        minLines = 2,
                        maxLines = 4,
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2)) {
                    Button(
                        onClick = { onEvent(Event.OnSubmit(state)) },
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
                            Text("Registrar compra")
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

/** Loading / error feedback for the product-details fetch, under the product selector. */
@Composable
private fun ProductInfoStatus(
    state: State,
    onEvent: (Event) -> Unit,
) {
    AnimatedVisibility(
        visible = state.infoLoading || state.infoError,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        if (state.infoError) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3),
            ) {
                Text(
                    text = "No se pudieron cargar los datos del producto.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f),
                )
                state.selectedProductId?.let { productId ->
                    TextButton(onClick = { onEvent(Event.OnRetryLoadInfo(productId)) }) {
                        Text("Reintentar")
                    }
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Text(
                    text = "Cargando datos del producto…",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AmountRow(
    state: State,
    onEvent: (Event) -> Unit,
) {
    val showAmountError = state.amount.isNotBlank() && !state.amountValid
    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3)) {
        OutlinedTextField(
            value = state.amount,
            onValueChange = { onEvent(Event.OnAmountChange(it)) },
            modifier = Modifier.weight(1f),
            label = { Text("Cantidad *") },
            singleLine = true,
            isError = showAmountError,
            supportingText = if (showAmountError) {
                { Text("Número no válido") }
            } else {
                null
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            suffix = if (state.sameUnits) {
                { Text(state.info?.purchaseUnitName.orEmpty()) }
            } else {
                null
            },
        )
        if (state.info != null && !state.sameUnits) {
            KrocyDropdownField(
                label = "Unidad",
                options = state.amountUnitOptions,
                selectedId = state.amountUnit.ordinal,
                onSelected = { ordinal ->
                    ordinal?.let { onEvent(Event.OnAmountUnitChange(AmountUnitUi.entries[it])) }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PriceFields(
    state: State,
    onEvent: (Event) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        SegmentedButton(
            selected = state.priceMode == PriceModeUi.UNIT,
            onClick = { onEvent(Event.OnPriceModeChange(PriceModeUi.UNIT)) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
        ) { Text("Precio unitario") }
        SegmentedButton(
            selected = state.priceMode == PriceModeUi.TOTAL,
            onClick = { onEvent(Event.OnPriceModeChange(PriceModeUi.TOTAL)) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
        ) { Text("Precio total") }
    }
    val showPriceError = !state.priceValid
    OutlinedTextField(
        value = state.price,
        onValueChange = { onEvent(Event.OnPriceChange(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(if (state.priceMode == PriceModeUi.UNIT) "Precio por unidad" else "Precio total")
        },
        singleLine = true,
        isError = showPriceError,
        supportingText = when {
            showPriceError -> {
                { Text("Número no válido") }
            }
            state.lastPriceHint != null -> {
                { Text(state.lastPriceHint!!) }
            }
            else -> null
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DueDateField(
    dueDate: LocalDate?,
    neverOverdue: Boolean,
    onDateChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) showPicker = true
        }
    }

    OutlinedTextField(
        value = when {
            neverOverdue -> "Nunca caduca"
            dueDate != null -> dueDate.format(DueDateFormat)
            else -> ""
        },
        onValueChange = {},
        readOnly = true,
        enabled = !neverOverdue,
        singleLine = true,
        label = { Text("Fecha de caducidad *") },
        trailingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = "Elegir fecha") },
        interactionSource = interactionSource,
        modifier = modifier,
    )

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate?.let { it.toEpochDays() * MILLIS_PER_DAY },
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateChange(
                            pickerState.selectedDateMillis?.let {
                                LocalDate.fromEpochDays(it / MILLIS_PER_DAY)
                            }
                        )
                        showPicker = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

private const val MILLIS_PER_DAY = 86_400_000L

private val DueDateFormat = LocalDate.Format {
    day()
    char('/')
    monthNumber()
    char('/')
    year()
}

private val previewProducts = OptionsUi(
    options = listOf(
        SelectableOptionUi(7, "Leche entera"),
        SelectableOptionUi(8, "Pan de molde"),
    ),
    isLoading = false,
)
private val previewLocations = OptionsUi(
    options = listOf(
        SelectableOptionUi(10, "Despensa"),
        SelectableOptionUi(11, "Nevera"),
    ),
    isLoading = false,
)
private val previewStores = OptionsUi(
    options = listOf(SelectableOptionUi(5, "Mercadona")),
    isLoading = false,
)

private fun previewState() = State(
    products = previewProducts,
    locations = previewLocations,
    shoppingLocations = previewStores,
    selectedProductId = 7,
    info = ProductPurchaseInfoUi(
        purchaseUnitName = "Paquete",
        purchaseUnitNamePlural = "Paquetes",
        stockUnitName = "Unidad",
        stockUnitNamePlural = "Unidades",
        conversionFactorPurchaseToStock = 6.0,
        lastPrice = 0.5,
    ),
    dueDate = LocalDate(2026, 7, 15),
    locationId = 11,
)

@PreviewLightDark
@Composable
private fun PurchaseFormPreview() {
    KrocyTheme {
        Surface {
            PurchaseContent(state = previewState(), onEvent = {})
        }
    }
}

@PreviewLightDark
@Composable
private fun PurchaseFormAdvancedPreview() {
    KrocyTheme {
        Surface {
            PurchaseContent(
                state = previewState().copy(advancedExpanded = true, price = "3"),
                onEvent = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PurchaseFormLoadingPreview() {
    KrocyTheme {
        Surface {
            PurchaseContent(state = State(), onEvent = {})
        }
    }
}
