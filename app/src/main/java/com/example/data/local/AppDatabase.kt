package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [SavedCaseEntity::class, KentRubricEntity::class],
  version = 2,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun savedCaseDao(): SavedCaseDao
  abstract fun kentRubricDao(): KentRubricDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        ensureDatabaseSeeded(context.applicationContext)
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "bhms_repertory_database"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }

    private fun ensureDatabaseSeeded(context: Context) {
      val dbFile = context.getDatabasePath("bhms_repertory_database")
      if (!dbFile.exists() || dbFile.length() < 1000) {
        try {
          dbFile.parentFile?.mkdirs()
          context.assets.open("databases/kent_db.gz").use { input ->
            java.util.zip.GZIPInputStream(input).use { gzipInput ->
              java.io.FileOutputStream(dbFile).use { output ->
                gzipInput.copyTo(output)
              }
            }
          }
        } catch (e: Exception) {
          android.util.Log.e("AppDatabase", "Seeding error: ${e.message}")
        }
      }
    }
  }
}
