package com.habitquest.ui.screen.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitquest.ui.component.AchievementCard
import com.habitquest.ui.component.StatCard
import com.habitquest.ui.theme.*

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val state    by viewModel.uiState.collectAsStateWithLifecycle()
    val maxStreak = state.habits.maxOfOrNull { it.streakCount } ?: 0

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "TU PROGRESO",
                    style = AppTypography.labelSmall,
                    color = TextDim,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Estadísticas",
                    style = AppTypography.headlineLarge,
                    color = TextPrimary
                )
            }
        }

        // ── Top stat cards ──────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    icon = "⚡", value = state.totalXP.toString(),
                    label = "XP Total", valueColor = Amber,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "🔥", value = "${maxStreak}d",
                    label = "Racha Max", valueColor = Red,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "✅", value = state.habits.size.toString(),
                    label = "Hábitos", valueColor = Emerald,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // ── Weekly compliance + daily activity ──────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.dp, Divider, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SEMANA ACTUAL",
                        style = AppTypography.labelSmall,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                    val compliancePct = (state.weeklyCompliance * 100).toInt()
                    Text(
                        text = "$compliancePct% cumplimiento",
                        style = AppTypography.labelSmall,
                        color = when {
                            compliancePct >= 80 -> Emerald
                            compliancePct >= 50 -> Amber
                            else                -> Red
                        },
                        fontSize = 11.sp
                    )
                }

                // Compliance bar
                val animCompliance by animateFloatAsState(
                    targetValue = state.weeklyCompliance,
                    animationSpec = tween(1000),
                    label = "compliance"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(CardBackground2)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animCompliance.coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    when {
                                        state.weeklyCompliance >= 0.8f -> listOf(Emerald, Emerald)
                                        state.weeklyCompliance >= 0.5f -> listOf(Amber, Amber)
                                        else                           -> listOf(Red, Red)
                                    }
                                )
                            )
                    )
                }

                // Daily activity bar chart (last 7 days)
                DailyActivityChart(
                    data      = state.dailyActivity,
                    maxHabits = state.maxHabits
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // ── Most consistent habit ──────────────────────────────
        state.mostConsistentHabit?.let { best ->
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Amber.copy(alpha = 0.07f))
                        .border(1.dp, Amber.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("🏆", fontSize = 28.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HÁBITO MÁS CONSTANTE",
                            style = AppTypography.labelSmall,
                            color = Amber,
                            letterSpacing = 1.sp,
                            fontSize = 9.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "${best.icon} ${best.name}",
                            style = AppTypography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "${best.totalDays} días en total · 🔥 racha ${best.streakCount}",
                            style = AppTypography.labelSmall,
                            color = TextDim,
                            fontSize = 11.sp
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        // ── Per-habit performance bars ─────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.dp, Divider, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "RENDIMIENTO",
                    style = AppTypography.labelSmall,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(2.dp))

                state.habits.forEach { habit ->
                    val pct = (habit.totalDays / 30f).coerceIn(0f, 1f)
                    val animPct by animateFloatAsState(
                        targetValue = pct,
                        animationSpec = tween(1000),
                        label = "bar_${habit.id}"
                    )

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${habit.icon} ${habit.name}",
                                style = AppTypography.bodyMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = "${habit.totalDays}d",
                                style = AppTypography.labelSmall,
                                color = TextDim
                            )
                        }
                        Spacer(Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(CardBackground2)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animPct)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.horizontalGradient(listOf(Amber, Red))
                                    )
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // ── Achievements ──────────────────────────────────────
        item {
            Text(
                text = "LOGROS",
                style = AppTypography.labelSmall,
                color = TextMuted,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))
        }

        items(state.achievements.chunked(2)) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { achievement ->
                    AchievementCard(achievement = achievement, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun DailyActivityChart(
    data: List<Int>,
    maxHabits: Int
) {
    val dayLabels = listOf("L", "M", "X", "J", "V", "S", "D")
    val maxBar = maxHabits.coerceAtLeast(1)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { index, count ->
            val fraction = count.toFloat() / maxBar
            val animFraction by animateFloatAsState(
                targetValue = fraction,
                animationSpec = tween(800, delayMillis = index * 60),
                label = "bar_day_$index"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((52.dp * animFraction.coerceAtLeast(0.04f)))
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(
                            if (count > 0)
                                Brush.verticalGradient(listOf(Amber, Red))
                            else
                                Brush.verticalGradient(listOf(CardBackground2, CardBackground2))
                        )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = dayLabels.getOrElse(index) { "" },
                    style = AppTypography.labelSmall,
                    color = TextDimmer,
                    fontSize = 9.sp
                )
            }
        }
    }
}
