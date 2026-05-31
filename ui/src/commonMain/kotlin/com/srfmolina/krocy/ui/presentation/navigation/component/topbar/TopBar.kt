package com.srfmolina.krocy.ui.presentation.navigation.component.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.srfmolina.krocy.ui.presentation.common.model.IconActionUi
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.isCompact

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SmallTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationAction: IconActionUi, //TODO: hide menu icon leading actions in larger screens
    trailingAction: IconActionUi? = null
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(),
        title = {
            Text(
                title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = { NavigationIcon(navigationAction) },
        actions = {
            trailingAction?.let {
                IconButton(
                    onClick = trailingAction.onClick,
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(
                        imageVector = trailingAction.icon,
                        contentDescription = trailingAction.contentDescription
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun MediumTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationAction: IconActionUi,
    action: IconActionUi? = null
) {
    MediumTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(),
        title = {
            Text(
                title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = { NavigationIcon(navigationAction) },
        actions = {
            action?.let {
                IconButton(
                    onClick = action.onClick,
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.contentDescription
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NavigationIcon(navigationAction: IconActionUi) {
    if (MaterialTheme.isCompact || navigationAction.icon != Icons.Filled.Menu) {
        IconButton(
            onClick = navigationAction.onClick,
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                imageVector = navigationAction.icon,
                contentDescription = navigationAction.contentDescription
            )
        }
    }
}

// PREVIEW //

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun SmallTopBarPreview() {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    KrocyTheme {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                SmallTopBar(
                    title = "Small top bar",
                    scrollBehavior = scrollBehavior,
                    navigationAction = IconActionUi(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "example",
                        onClick = {}
                    ),
                    trailingAction = IconActionUi(
                        icon = Icons.Filled.Menu,
                        contentDescription = "example",
                        onClick = {}
                    )
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(30) { index ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Item ${index + 1}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun MediumTopBarPreview() {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    KrocyTheme {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopBar(
                    title = "Medium top bar",
                    scrollBehavior = scrollBehavior,
                    navigationAction = IconActionUi(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "example",
                        onClick = {}
                    ),
                    action = IconActionUi(
                        icon = Icons.Filled.Menu,
                        contentDescription = "example",
                        onClick = {}
                    )
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(30) { index ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Item ${index + 1}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}