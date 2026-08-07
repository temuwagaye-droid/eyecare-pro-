package com.example.ui.visiontest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.EyeCareDatabase
import com.example.data.VisionTestRecord
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VisionTestViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = EyeCareDatabase.getDatabase(application).eyeCareDao()

    val testHistory: StateFlow<List<VisionTestRecord>> = dao.getAllVisionTests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveTestResult(testType: String, scoreSummary: String) {
        viewModelScope.launch {
            dao.insertVisionTest(
                VisionTestRecord(
                    testType = testType,
                    scoreSummary = scoreSummary
                )
            )
        }
    }
}
