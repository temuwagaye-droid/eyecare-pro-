package com.example.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.EyeCareDatabase
import com.example.data.ScreenTimeLog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val screenTimeMinutes: Int = 145,
    val eyeStrainRisk: String = "Moderate",
    val ambientLux: Int = 320,
    val timeToNextBreakSeconds: Int = 1200, // 20 mins
    val isBreakActive: Boolean = false,
    val recentScreenTimes: List<ScreenTimeLog> = emptyList(),
    val isAppIdeaDialogVisible: Boolean = false,
    val appIdeaStatusMessage: String? = null
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = EyeCareDatabase.getDatabase(application).eyeCareDao()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getRecentScreenTime().collect { list ->
                _uiState.update { it.copy(recentScreenTimes = list) }
            }
        }
        // Initialize dummy screen time log if empty
        viewModelScope.launch {
            dao.insertScreenTimeLog(ScreenTimeLog(dateString = "Today", minutesSpent = 145, eyeStrainRisk = "Moderate"))
        }
    }

    fun triggerBreakReminder() {
        _uiState.update { it.copy(isBreakActive = true) }
    }

    fun dismissBreak() {
        _uiState.update { it.copy(isBreakActive = false, timeToNextBreakSeconds = 1200) }
    }

    fun setAppIdeaDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(isAppIdeaDialogVisible = visible, appIdeaStatusMessage = null) }
    }

    fun sendAppIdeaMessage(idea: String) {
        if (idea.isBlank()) return
        viewModelScope.launch {
            // Simulate sending message with app idea
            _uiState.update { it.copy(appIdeaStatusMessage = "App idea sent successfully! Thank you for your feedback.") }
        }
    }
}
