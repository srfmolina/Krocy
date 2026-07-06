package com.srfmolina.krocy.ui.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing

/**
 * A [FormSection]-styled group whose content expands/collapses. The whole header row is
 * clickable; the chevron rotates with the state. Stateless — the owner holds [expanded].
 */
@Composable
internal fun CollapsibleFormSection(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(start = MaterialTheme.spacing.s1),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            val rotation by animateFloatAsState(if (expanded) 180f else 0f)
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Contraer $title" else "Expandir $title",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.rotate(rotation),
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s3)) {
                content()
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CollapsibleFormSectionPreview() {
    KrocyTheme {
        Surface {
            var expanded by remember { mutableStateOf(true) }
            Column(modifier = Modifier.padding(MaterialTheme.spacing.s4)) {
                CollapsibleFormSection(
                    title = "Avanzado",
                    expanded = expanded,
                    onToggle = { expanded = !expanded },
                ) {
                    OutlinedTextField(
                        value = "7",
                        onValueChange = {},
                        label = { Text("Caducidad por defecto (días)") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
