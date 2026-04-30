package com.habitquest.ui.screen.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitquest.ui.component.PetAnimation
import com.habitquest.ui.theme.Amber
import com.habitquest.ui.theme.AppTypography
import com.habitquest.ui.theme.Background
import com.habitquest.ui.theme.Blue
import com.habitquest.ui.theme.Emerald
import com.habitquest.ui.theme.Orange
import com.habitquest.ui.theme.Purple
import com.habitquest.ui.theme.TextMuted
import com.habitquest.ui.theme.TextPrimary
import com.habitquest.ui.theme.TextSecondary

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = remember {
        onboardingPages(
            calendarIcon = Icons.Rounded.CalendarMonth,
            achievementIcon = Icons.Rounded.EmojiEvents,
            chartIcon = Icons.Rounded.BarChart
        )
    }
    var pageIndex by remember { mutableIntStateOf(0) }
    val isLastPage = pageIndex == pages.lastIndex

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFFBF5),
                        Color(0xFFF1E8F7),
                        Background
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 18.dp)
    ) {
        TextButton(
            onClick = onFinish,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Text(
                text = "Saltar",
                style = AppTypography.labelLarge,
                color = TextMuted
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 52.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = pageIndex,
                transitionSpec = {
                    (fadeIn(tween(260)) + slideInHorizontally { it / 5 })
                        .togetherWith(fadeOut(tween(160)) + slideOutHorizontally { -it / 5 })
                },
                label = "onboardingPage"
            ) { targetPage ->
                OnboardingPageContent(
                    page = pages[targetPage],
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(22.dp))

            PageIndicators(
                pageCount = pages.size,
                selectedIndex = pageIndex
            )

            Spacer(Modifier.weight(1f))

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(320)) + slideInVertically { it / 8 }
            ) {
                Button(
                    onClick = {
                        if (isLastPage) {
                            onFinish()
                        } else {
                            pageIndex += 1
                        }
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = if (isLastPage) "Comenzar" else "Siguiente",
                        style = AppTypography.labelLarge,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.84f),
                            Purple.copy(alpha = 0.12f),
                            Orange.copy(alpha = 0.08f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.78f),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            OnboardingVisualContent(page.visual)
        }

        Spacer(Modifier.height(30.dp))

        Text(
            text = page.title,
            style = AppTypography.headlineLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = page.subtitle,
            style = AppTypography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 23.sp,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
}

@Composable
private fun OnboardingVisualContent(visual: OnboardingVisual) {
    when (visual) {
        is OnboardingVisual.Image -> FloatingAssetImage(visual.resId)
        is OnboardingVisual.Pet -> PetAnimation(
            stage = visual.stage,
            streak = visual.streak,
            modifier = Modifier.size(210.dp)
        )
        is OnboardingVisual.IconCluster -> IconCluster(visual.icons)
    }
}

@Composable
private fun FloatingAssetImage(resId: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "onboardingAssetFloat")
    val translationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "onboardingAssetTranslation"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "onboardingAssetScale"
    )

    Image(
        painter = painterResource(resId),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(210.dp)
            .graphicsLayer {
                this.translationY = translationY
                scaleX = scale
                scaleY = scale
            }
    )
}

@Composable
private fun IconCluster(icons: List<ImageVector>) {
    val colors = listOf(Purple, Amber, Blue)
    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        IconBubble(
            icon = Icons.Rounded.Spa,
            color = Emerald,
            size = 116,
            modifier = Modifier.align(Alignment.Center)
        )
        icons.forEachIndexed { index, icon ->
            val alignment = when (index) {
                0 -> Alignment.TopEnd
                1 -> Alignment.BottomEnd
                else -> Alignment.BottomStart
            }
            IconBubble(
                icon = icon,
                color = colors[index % colors.size],
                size = 76,
                modifier = Modifier.align(alignment)
            )
        }
    }
}

@Composable
private fun IconBubble(
    icon: ImageVector,
    color: Color,
    size: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .background(color.copy(alpha = 0.16f), CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.86f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size((size * 0.46f).dp)
        )
    }
}

@Composable
private fun PageIndicators(
    pageCount: Int,
    selectedIndex: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val selected = index == selectedIndex
            val width by animateFloatAsState(
                targetValue = if (selected) 28f else 8f,
                animationSpec = tween(220),
                label = "onboardingIndicatorWidth"
            )
            val alpha by animateFloatAsState(
                targetValue = if (selected) 1f else 0.34f,
                animationSpec = tween(220),
                label = "onboardingIndicatorAlpha"
            )
            Box(
                modifier = Modifier
                    .width(width.dp)
                    .height(8.dp)
                    .background(
                        color = Purple.copy(alpha = alpha),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
    }
}
