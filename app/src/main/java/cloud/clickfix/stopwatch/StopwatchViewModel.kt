package cloud.clickfix.stopwatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class StopwatchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StopwatchState())
    val uiState: StateFlow<StopwatchState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var lastTimestamp: Long = 0L

    fun start() {
        if (_uiState.value.isRunning) return

        _uiState.update { it.copy(isRunning = true) }
        lastTimestamp = System.currentTimeMillis()
        
        timerJob = viewModelScope.launch {
            while (isActive) {
                val currentTimestamp = System.currentTimeMillis()
                val diff = currentTimestamp - lastTimestamp
                lastTimestamp = currentTimestamp
                
                _uiState.update { state ->
                    state.copy(
                        elapsedTime = state.elapsedTime + diff,
                        currentLapElapsedTime = state.currentLapElapsedTime + diff
                    )
                }
                // Update every ~10ms for high precision display without over-taxing recomposition
                delay(10)
            }
        }
    }

    fun pause() {
        if (!_uiState.value.isRunning) return
        
        _uiState.update { it.copy(isRunning = false) }
        timerJob?.cancel()
        timerJob = null
    }

    fun resume() {
        start()
    }

    fun reset() {
        pause()
        _uiState.update { state ->
            StopwatchState(darkMode = state.darkMode)
        }
    }

    fun lap() {
        val currentState = _uiState.value
        if (!currentState.isRunning) return

        val newLap = Lap(
            id = currentState.laps.size + 1,
            lapTime = currentState.currentLapElapsedTime,
            totalTime = currentState.elapsedTime
        )
        
        _uiState.update { state ->
            state.copy(
                laps = listOf(newLap) + state.laps, // Add to top
                currentLapElapsedTime = 0L
            )
        }
    }

    fun toggleTheme() {
        _uiState.update { state ->
            state.copy(darkMode = !state.darkMode)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
