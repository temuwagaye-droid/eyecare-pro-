package com.example.ui.exercises

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.EyeCareDatabase
import com.example.data.ExerciseRecord
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ExerciseItem(
    val id: String,
    val title: String,
    val durationSeconds: Int,
    val description: String,
    val benefit: String
)

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = EyeCareDatabase.getDatabase(application).eyeCareDao()

    val exerciseList = listOf(
        ExerciseItem("1", "Palming Warmth", 60, "Rub palms together until warm, then cup gently over closed eyes.", "Relieves eye strain and relaxes optic nerve"),
        ExerciseItem("2", "Blinking Refresh", 30, "Blink rapidly for 10 seconds, close eyes and rest for 10 seconds. Repeat.", "Prevents dry eyes and lubricates cornea"),
        ExerciseItem("3", "Focus Shifting", 45, "Hold thumb 10 inches away, focus on it for 5s, then focus on an object 20 feet away.", "Improves accommodation flexibility"),
        ExerciseItem("4", "Near-Far Accommodation", 60, "Alternate focus between near pencil tip and distant wall clock.", "Strengthens ciliary muscles"),
        ExerciseItem("5", "Gentle Eye Rolling", 45, "Slowly roll eyes in clockwise circle 5 times, then counter-clockwise 5 times.", "Relieves extraocular muscle tension")
    )

    private val _selectedExercise = MutableStateFlow<ExerciseItem?>(null)
    val selectedExercise: StateFlow<ExerciseItem?> = _selectedExercise.asStateFlow()

    private val _isSessionRunning = MutableStateFlow(false)
    val isSessionRunning: StateFlow<Boolean> = _isSessionRunning.asStateFlow()

    val recentRecords: StateFlow<List<ExerciseRecord>> = dao.getAllExerciseRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun startSession(exercise: ExerciseItem) {
        _selectedExercise.value = exercise
        _isSessionRunning.value = true
    }

    fun finishSession(duration: Int) {
        val exercise = _selectedExercise.value ?: return
        viewModelScope.launch {
            dao.insertExerciseRecord(
                ExerciseRecord(
                    exerciseName = exercise.title,
                    durationSeconds = duration
                )
            )
        }
        _isSessionRunning.value = false
        _selectedExercise.value = null
    }

    fun cancelSession() {
        _isSessionRunning.value = false
        _selectedExercise.value = null
    }
}
