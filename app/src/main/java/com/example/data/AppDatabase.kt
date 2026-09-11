package com.example.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserProfile::class,
        RubricEntity::class,
        RemedyNameEntity::class,
        CaseEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun rubricDao(): RubricDao
    abstract fun remedyDao(): RemedyDao
    abstract fun caseDao(): CaseDao

    companion object {
        private const val TAG = "AppDatabase"
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val dbName = "rootchart_clinical_v2.db"
                val newDbFile = context.getDatabasePath(dbName)
                val oldDbFile = context.getDatabasePath("rootchart_clinical.db")
                val isFirstCreate = !newDbFile.exists()

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    dbName
                )
                    .createFromAsset("database/rootchart_clinical.db")
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            try {
                                var profileCount = 0
                                val c = db.query("SELECT COUNT(*) FROM user_profile")
                                if (c.moveToFirst()) {
                                    profileCount = c.getInt(0)
                                }
                                c.close()

                                if (profileCount == 0 && oldDbFile.exists()) {
                                    try {
                                        val oldDb = SQLiteDatabase.openDatabase(oldDbFile.path, null, SQLiteDatabase.OPEN_READONLY)
                                        // Migrate user profile
                                        val profileCursor = oldDb.rawQuery("SELECT id, clinicianName, role, isDarkMode, createdAt FROM user_profile LIMIT 1", null)
                                        if (profileCursor.moveToFirst()) {
                                            val id = profileCursor.getLong(0)
                                            val name = profileCursor.getString(1)
                                            val role = profileCursor.getString(2)
                                            val isDark = profileCursor.getInt(3)
                                            val createdAt = profileCursor.getLong(4)
                                            db.execSQL(
                                                "INSERT OR IGNORE INTO user_profile (id, clinicianName, role, isDarkMode, createdAt) VALUES (?, ?, ?, ?, ?)",
                                                arrayOf(id, name, role, isDark, createdAt)
                                            )
                                            profileCount++
                                        }
                                        profileCursor.close()
                                        oldDb.close()
                                    } catch (e: Exception) {
                                        Log.w(TAG, "Profile migration skipped: ${e.message}")
                                    }
                                }

                                if (oldDbFile.exists()) {
                                    try {
                                        val oldDb = SQLiteDatabase.openDatabase(oldDbFile.path, null, SQLiteDatabase.OPEN_READONLY)
                                        // Migrate existing clinical cases if present
                                        val casesCursor = oldDb.rawQuery("SELECT * FROM clinical_cases", null)
                                        val colNames = casesCursor.columnNames
                                        while (casesCursor.moveToNext()) {
                                            val cols = colNames.joinToString(", ")
                                            val placeholders = colNames.map { "?" }.joinToString(", ")
                                            val values = Array<Any?>(colNames.size) { i ->
                                                when (casesCursor.getType(i)) {
                                                    android.database.Cursor.FIELD_TYPE_INTEGER -> casesCursor.getLong(i)
                                                    android.database.Cursor.FIELD_TYPE_FLOAT -> casesCursor.getDouble(i)
                                                    android.database.Cursor.FIELD_TYPE_STRING -> casesCursor.getString(i)
                                                    android.database.Cursor.FIELD_TYPE_BLOB -> casesCursor.getBlob(i)
                                                    else -> null
                                                }
                                            }
                                            db.execSQL("INSERT OR IGNORE INTO clinical_cases ($cols) VALUES ($placeholders)", values)
                                        }
                                        casesCursor.close()
                                        oldDb.close()
                                        Log.d(TAG, "Checked and migrated any legacy cases into v2 database")
                                    } catch (e: Exception) {
                                        Log.w(TAG, "Old db cases check skipped: ${e.message}")
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error in onOpen database initialization", e)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
