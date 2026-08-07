package com.example.ui.bluelight

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BlueLightUiState(
    val isEnabled: Boolean = false,
    val colorTemperatureK: Int = 2800, // Amber warm
    val opacityPercent: Int = 40,
    val isReadingModeEnabled: Boolean = false,
    val isAutoScheduleEnabled: Boolean = true,
    val selectedColorTheme: String = "Amber", // Amber, Red, Yellow, Green, Sepia
    val isFullSystemDim: Boolean = true
)

class BlueLightViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(BlueLightUiState())
    val uiState: StateFlow<BlueLightUiState> = _uiState.asStateFlow()

    fun toggleEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isEnabled = enabled) }
    }

    fun updateTemperature(tempK: Int) {
        _uiState.update { it.copy(colorTemperatureK = tempK) }
    }

    fun updateOpacity(opacity: Int) {
        _uiState.update { it.copy(opacityPercent = opacity) }
    }

    fun toggleReadingMode(enabled: Boolean) {
        _uiState.update { it.copy(isReadingModeEnabled = enabled) }
    }

    fun toggleAutoSchedule(enabled: Boolean) {
        _uiState.update { it.copy(isAutoScheduleEnabled = enabled) }
    }

    fun selectColorTheme(theme: String) {
        _uiState.update { it.copy(selectedColorTheme = theme) }
    }

    fun toggleFullSystemDim(enabled: Boolean) {
        _uiState.update { it.copy(isFullSystemDim = enabled) }
    }
}

