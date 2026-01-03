package cloud.clickfix.stopwatch

import androidx.compose.runtime.Immutable

@Immutable
data class StopwatchState(
    val elapsedTime: Long = 0L,
    val currentLapElapsedTime: Long = 0L,
    val isRunning: Boolean = false,
    val laps: List<Lap> = emptyList(),
    val darkMode: Boolean = false
)
