package com.srfmolina.krocy.presentation.feature.login

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.material3.LoadingIndicator

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun WelcomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.Center)
        ) {

            LogInTitle()

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun LogInTitle() {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LoadingIndicator()

        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
    }

    HorizontalDivider(modifier = Modifier.fillMaxWidth())
}

@PreviewLightDark
@Composable
private fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen()
    }
}