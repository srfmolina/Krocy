package com.srfmolina.krocy.ui.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing
import kotlin.math.round

/**
 * A tonal "1 X = n Y" equation card for quantity-unit conversions. Read-only variant:
 * shows an existing [factor] with emphasized typography.
 */
@Composable
internal fun UnitEquationCard(
    fromUnitName: String,
    toUnitName: String,
    factor: Double,
    caption: String,
    modifier: Modifier = Modifier,
) {
    EquationSurface(caption = caption, modifier = modifier) {
        EquationTerm("1 $fromUnitName")
        EquationOperator()
        Text(
            text = formatFactor(factor),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        EquationTerm(toUnitName)
    }
}

/**
 * Editable variant of [UnitEquationCard]: the factor is an embedded text field so the
 * input itself reads as "1 X = ___ Y".
 */
@Composable
internal fun UnitEquationInputCard(
    fromUnitName: String,
    toUnitName: String,
    value: String,
    onValueChange: (String) -> Unit,
    caption: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    EquationSurface(caption = caption, modifier = modifier) {
        EquationTerm("1 $fromUnitName")
        EquationOperator()
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.width(96.dp),
            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )
        EquationTerm(toUnitName)
    }
}

@Composable
private fun EquationSurface(
    caption: String,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.s4),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s2),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3),
            ) {
                content()
            }
            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EquationTerm(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun EquationOperator() {
    Text(
        text = "=",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

// Inverted reverse conversions (1.0 / factor) produce repeating decimals; cap the display.
private fun formatFactor(value: Double): String {
    val rounded = round(value * 1000) / 1000
    return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
}

@PreviewLightDark
@Composable
private fun UnitEquationCardPreview() {
    KrocyTheme {
        Surface {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.s4),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s4),
            ) {
                UnitEquationCard(
                    fromUnitName = "Paquete",
                    toUnitName = "Unidad",
                    factor = 6.0,
                    caption = "Conversión global existente",
                )
                UnitEquationInputCard(
                    fromUnitName = "Paquete",
                    toUnitName = "Unidad",
                    value = "",
                    onValueChange = {},
                    caption = "Se creará una conversión para este producto",
                    isError = false,
                )
            }
        }
    }
}
