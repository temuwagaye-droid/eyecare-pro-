package com.example.ui.fatigue

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FatigueScanState(
    val isScanning: Boolean = false,
    val scanProgress: Float = 0f,
    val blinkCount: Int = 0,
    val perclosPercent: Float = 0f,
    val fatigueScore: String = "Not Scanned",
    val recommendations: List<String> = emptyList()
)

class FatigueScanViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(FatigueScanState())
    val uiState: StateFlow<FatigueScanState> = _uiState.asStateFlow()

    fun startScan() {
        _uiState.update {
            FatigueScanState(
                isScanning = true,
                scanProgress = 0f,
                blinkCount = 0,
                perclosPercent = 0f,
                fatigueScore = "Analyzing...",
                recommendations = emptyList()
            )
        }

        viewModelScope.launch {
            val totalSteps = 30
            for (i in 1..totalSteps) {
                delay(500L) // 15 seconds total scan simulation
                val progress = i / totalSteps.toFloat()
                val blinks = (i * 0.8f).toInt()
                val perclos = (12f + (i % 3) * 1.5f)
                _uiState.update {
                    it.copy(
                        scanProgress = progress,
                        blinkCount = blinks,
                        perclosPercent = perclos
                    )
                }
            }

            // Results calculation
            val score = "Low Strain"
            val recs = listOf(
                "Your blink frequency is stable at 16 blinks/min.",
                "Take a 5-minute palming break to relax ciliary muscles.",
                "Ensure ambient lighting matches your screen brightness."
            )

            _uiState.update {
                it.copy(
                    isScanning = false,
                    fatigueScore = score,
                    recommendations = recs
                )
            }
        }
    }

    fun cancelScan() {
        _uiState.update { FatigueScanState() }
    }
}
