package cloud.clickfix.stopwatch.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cloud.clickfix.stopwatch.Lap
import cloud.clickfix.stopwatch.StopwatchViewModel
import cloud.clickfix.stopwatch.utils.TimeFormatter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopwatchScreen(viewModel: StopwatchViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        secondaryColor.copy(alpha = 0.2f),
                        primaryColor.copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Stopwatch",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = 2.sp
                                )
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleTheme() }) {
                            Icon(
                                imageVector = if (uiState.darkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.8f))

                // Timer Display with Animated Progress
                Box(contentAlignment = Alignment.Center) {
                    CircularTimerProgress(
                        elapsedTime = uiState.elapsedTime,
                        color = primaryColor
                    )
                    TimerDisplay(uiState.elapsedTime)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Control Buttons
                ControlButtons(
                    isRunning = uiState.isRunning,
                    elapsedTime = uiState.elapsedTime,
                    onStart = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.start()
                    },
                    onPause = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.pause()
                    },
                    onReset = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.reset()
                    },
                    onLap = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.lap()
                    }
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Lap List with Glassmorphism
                LapList(
                    laps = uiState.laps,
                    currentLapTime = uiState.currentLapElapsedTime,
                    totalTime = uiState.elapsedTime,
                    isRunning = uiState.isRunning
                )
            }
        }
    }
}

@Composable
fun CircularTimerProgress(elapsedTime: Long, color: Color) {
    val progress = (elapsedTime % 60000) / 60000f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = linearWithEasing(),
        label = "TimerProgress"
    )

    Canvas(modifier = Modifier.size(320.dp)) {
        // Track
        drawCircle(
            color = color.copy(alpha = 0.05f),
            style = Stroke(width = 12.dp.toPx())
        )
        // Progress Gradient
        val sweepGradient = Brush.sweepGradient(
            0f to color.copy(alpha = 0.3f),
            0.5f to color,
            1f to color.copy(alpha = 0.3f)
        )
        drawArc(
            brush = sweepGradient,
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress,
            useCenter = false,
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun linearWithEasing() = remember {
    tween<Float>(durationMillis = 100, easing = LinearEasing)
}

@Composable
fun TimerDisplay(elapsedTime: Long) {
    val formattedTime = TimeFormatter.formatTime(elapsedTime)
    val parts = formattedTime.split(":")
    
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${parts[0]}:${parts[1]}",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 84.sp,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = ".${parts[2]}",
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Light
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 16.dp, start = 4.dp)
        )
    }
}

@Composable
fun ControlButtons(
    isRunning: Boolean,
    elapsedTime: Long,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onLap: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reset/Lap Button
        OutlinedIconButton(
            onClick = { if (isRunning) onLap() else onReset() },
            enabled = elapsedTime > 0,
            modifier = Modifier
                .size(72.dp),
            shape = CircleShape,
            border = if (elapsedTime > 0) IconButtonDefaults.outlinedIconButtonBorder(true) 
                     else IconButtonDefaults.outlinedIconButtonBorder(false).copy(brush = Brush.linearGradient(listOf(Color.Gray.copy(0.2f), Color.Gray.copy(0.2f))))
        ) {
            AnimatedContent(
                targetState = isRunning,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                }, label = ""
            ) { running ->
                if (running) {
                    Text("Lap", style = MaterialTheme.typography.labelLarge)
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
                }
            }
        }

        Spacer(modifier = Modifier.width(48.dp))

        // Play/Pause Button
        FilledIconButton(
            onClick = { if (isRunning) onPause() else onStart() },
            modifier = Modifier.size(88.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isRunning) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            AnimatedContent(
                targetState = isRunning,
                transitionSpec = {
                    scaleIn() + fadeIn() togetherWith scaleOut() + fadeOut()
                }, label = ""
            ) { running ->
                if (running) {
                    // Pause icon
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp, 28.dp).clip(RoundedCornerShape(4.dp)).background(LocalContentColor.current))
                        Box(modifier = Modifier.size(8.dp, 28.dp).clip(RoundedCornerShape(4.dp)).background(LocalContentColor.current))
                    }
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start", modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}

@Composable
fun LapList(
    laps: List<Lap>,
    currentLapTime: Long,
    totalTime: Long,
    isRunning: Boolean
) {
    val listState = rememberLazyListState()

    // Auto-scroll to top when a new lap is added
    LaunchedEffect(laps.size) {
        if (laps.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        tonalElevation = 2.dp
    ) {
        if (laps.isEmpty() && !isRunning && totalTime == 0L) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No laps yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
                // Active Lap (Live)
                if (totalTime > 0) {
                    item(key = "active_lap") {
                        ActiveLapItem(
                            id = laps.size + 1,
                            lapTime = currentLapTime,
                            totalTime = totalTime
                        )
                    }
                }

                // Recorded Laps
                items(laps, key = { it.id }) { lap ->
                    LapItem(lap)
                }
            }
        }
    }
}

@Composable
fun ActiveLapItem(id: Int, lapTime: Long, totalTime: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Lap $id",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Live Indicator
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "LIVE",
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = "Total: ${TimeFormatter.formatTime(totalTime)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
        Text(
            text = TimeFormatter.formatTime(lapTime),
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
}

@Composable
fun LapItem(lap: Lap) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Lap ${lap.id}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Total: ${TimeFormatter.formatTime(lap.totalTime)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
        Text(
            text = TimeFormatter.formatTime(lap.lapTime),
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
}
