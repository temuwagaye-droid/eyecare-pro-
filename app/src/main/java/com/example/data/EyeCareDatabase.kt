package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "exercise_records")
data class ExerciseRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseName: String,
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "vision_test_records")
data class VisionTestRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testType: String, // Snellen, Amsler, ColorBlindness, Astigmatism
    val scoreSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "screen_time_logs")
data class ScreenTimeLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String,
    val minutesSpent: Int,
    val eyeStrainRisk: String // Low, Moderate, High
)

@Dao
interface EyeCareDao {
    @Query("SELECT * FROM exercise_records ORDER BY timestamp DESC")
    fun getAllExerciseRecords(): Flow<List<ExerciseRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseRecord(record: ExerciseRecord)

    @Query("SELECT * FROM vision_test_records ORDER BY timestamp DESC")
    fun getAllVisionTests(): Flow<List<VisionTestRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisionTest(record: VisionTestRecord)

    @Query("SELECT * FROM screen_time_logs ORDER BY id DESC LIMIT 7")
    fun getRecentScreenTime(): Flow<List<ScreenTimeLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreenTimeLog(log: ScreenTimeLog)
}

@Database(entities = [ExerciseRecord::class, VisionTestRecord::class, ScreenTimeLog::class], version = 1, exportSchema = false)
abstract class EyeCareDatabase : RoomDatabase() {
    abstract fun eyeCareDao(): EyeCareDao

    companion object {
        @Volatile
        private var INSTANCE: EyeCareDatabase? = null

        fun getDatabase(context: Context): EyeCareDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EyeCareDatabase::class.java,
                    "eyecare_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
