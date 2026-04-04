package com.srfmolina.krocy.ui.presentation.common
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.srfmolina.krocy.ui.presentation.theme.KrocyTheme
import com.srfmolina.krocy.ui.presentation.theme.spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DotsPager(
    modifier: Modifier = Modifier, // <- expón el modifier
    pageCount: Int,
    content: @Composable (page: Int) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth(), // <- usa el modifier externo
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s4)
    ) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = pagerState
        ) { page ->
            content(page)
        }

        PagerControls(
            pageCount = pageCount,
            pagerState = pagerState,
            onPrevious = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            onNext = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PagerControls(
    pageCount: Int,
    pagerState: PagerState,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    val isFirst = pagerState.currentPage == 0
    val isLast = pagerState.currentPage == pageCount - 1

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s4)
    ) {
        // Botón izquierda
        FilledIconButton(
            onClick = onPrevious,
            enabled = !isFirst,
            shapes = IconButtonDefaults.shapes(),
            modifier = Modifier.size(IconButtonDefaults.smallContainerSize()),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Página anterior",
                modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Indicador worm
        WormDotsIndicator(
            pageCount = pageCount,
            pagerState = pagerState
        )

        Spacer(modifier = Modifier.weight(1f))

        // Botón derecha
        FilledIconButton(
            onClick = onNext,
            enabled = !isLast,
            shapes = IconButtonDefaults.shapes(),
            modifier = Modifier.size(IconButtonDefaults.smallContainerSize()),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Página siguiente",
                modifier = Modifier.size(IconButtonDefaults.extraSmallIconSize)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WormDotsIndicator(
    pageCount: Int,
    pagerState: PagerState,
    dotSize: Dp = 10.dp,
    dotSpacing: Dp = MaterialTheme.spacing.s2,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
) {
    val totalWidth: Dp = dotSize * pageCount + dotSpacing * (pageCount - 1)

    Canvas(
        modifier = Modifier
            .width(totalWidth)
            .height(dotSize)
    ) {
        val dotSizePx = dotSize.toPx()
        val spacingPx = dotSpacing.toPx()
        val step = dotSizePx + spacingPx
        val radius = CornerRadius(dotSizePx / 2)

        repeat(pageCount) { index ->
            val x = index * step
            drawRoundRect(
                color = inactiveColor,
                topLeft = Offset(x, 0f),
                size = Size(dotSizePx, dotSizePx),
                cornerRadius = radius
            )
        }

        val scrollPosition = pagerState.currentPage + pagerState.currentPageOffsetFraction
        val wormOffset = (scrollPosition % 1) * 2

        val xBase = scrollPosition.toInt() * step
        val head = xBase + step * 0f.coerceAtLeast(wormOffset - 1)
        val tail = xBase + dotSizePx + 1f.coerceAtMost(wormOffset) * step

        val wormPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = head,
                    top = 0f,
                    right = tail,
                    bottom = dotSizePx,
                    cornerRadius = radius
                )
            )
        }
        drawPath(path = wormPath, color = activeColor)
    }
}

@PreviewLightDark
@Composable
private fun DotsPagerPreview() {
    KrocyTheme {
        Surface {
            DotsPager(pageCount = 3) { page ->
                Text(text = page.toString())
            }
        }
    }
}