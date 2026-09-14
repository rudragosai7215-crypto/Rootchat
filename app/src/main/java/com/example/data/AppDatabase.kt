package com.example.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserProfile::class,
        RubricEntity::class,
        RemedyNameEntity::class,
        CaseEntity::class,
        MateriaMedicaEntity::class,
        ClinicianWorkspaceEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun rubricDao(): RubricDao
    abstract fun remedyDao(): RemedyDao
    abstract fun caseDao(): CaseDao
    abstract fun materiaMedicaDao(): MateriaMedicaDao
    abstract fun workspaceDao(): WorkspaceDao

    companion object {
        private const val TAG = "AppDatabase"
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private fun hasColumn(db: SupportSQLiteDatabase, tableName: String, columnName: String): Boolean {
            return try {
                db.query("PRAGMA table_info($tableName)").use { cursor ->
                    val nameIndex = cursor.getColumnIndex("name")
                    if (nameIndex != -1) {
                        while (cursor.moveToNext()) {
                            if (cursor.getString(nameIndex).equals(columnName, ignoreCase = true)) {
                                return true
                            }
                        }
                    }
                    false
                }
            } catch (e: Exception) {
                false
            }
        }

        private fun ensureColumn(db: SupportSQLiteDatabase, tableName: String, columnName: String, columnDef: String) {
            try {
                if (!hasColumn(db, tableName, columnName)) {
                    db.execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $columnDef")
                }
            } catch (e: Exception) {
                Log.w(TAG, "ensureColumn $tableName.$columnName skipped: ${e.message}")
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                ensureColumn(db, "clinical_cases", "workspaceId", "TEXT NOT NULL DEFAULT 'default_workspace'")
                ensureColumn(db, "user_profile", "workspaceId", "TEXT NOT NULL DEFAULT 'default_workspace'")
                try {
                    db.execSQL("""
                        CREATE TABLE IF NOT EXISTS clinician_workspaces (
                            clinicianName TEXT PRIMARY KEY NOT NULL,
                            workspaceId TEXT NOT NULL,
                            role TEXT NOT NULL DEFAULT 'Doctor',
                            lastLogin INTEGER NOT NULL DEFAULT 0
                        )
                    """.trimIndent())
                } catch (e: Exception) {
                    Log.w(TAG, "MIGRATION_1_2: clinician_workspaces table creation: ${e.message}")
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val dbName = "rootchart_clinical_v3.db"
                val newDbFile = context.getDatabasePath(dbName)
                val prevDbFiles = listOf(
                    context.getDatabasePath("rootchart_clinical_v2.db"),
                    context.getDatabasePath("rootchart_clinical.db")
                )

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    dbName
                )
                    .createFromAsset("database/rootchart_clinical.db")
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            try {
                                // Guarantee required schema columns exist without redundant ALTER TABLE calls
                                ensureColumn(db, "clinical_cases", "workspaceId", "TEXT NOT NULL DEFAULT 'default_workspace'")
                                ensureColumn(db, "user_profile", "workspaceId", "TEXT NOT NULL DEFAULT 'default_workspace'")
                                try {
                                    db.execSQL("""
                                        CREATE TABLE IF NOT EXISTS clinician_workspaces (
                                            clinicianName TEXT PRIMARY KEY NOT NULL,
                                            workspaceId TEXT NOT NULL,
                                            role TEXT NOT NULL DEFAULT 'Doctor',
                                            lastLogin INTEGER NOT NULL DEFAULT 0
                                        )
                                    """.trimIndent())
                                } catch (_: Exception) {}

                                var profileCount = 0
                                val c = db.query("SELECT COUNT(*) FROM user_profile")
                                if (c.moveToFirst()) {
                                    profileCount = c.getInt(0)
                                }
                                c.close()

                                for (prevFile in prevDbFiles) {
                                    if (prevFile.exists() && prevFile.length() > 0) {
                                        try {
                                            val oldDb = SQLiteDatabase.openDatabase(prevFile.path, null, SQLiteDatabase.OPEN_READONLY)
                                            
                                            // Migrate user profile if not yet migrated
                                            if (profileCount == 0) {
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
                                            }

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
                                            Log.d(TAG, "Migrated profile and cases from ${prevFile.name}")
                                        } catch (e: Exception) {
                                            Log.w(TAG, "Migration from ${prevFile.name} skipped: ${e.message}")
                                        }
                                    }
                                }

                                // Verify and ensure Materia Medica is populated
                                var mmCount = 0
                                val mmCursor = db.query("SELECT COUNT(*) FROM materia_medica")
                                if (mmCursor.moveToFirst()) {
                                    mmCount = mmCursor.getInt(0)
                                }
                                mmCursor.close()

                                if (mmCount == 0) {
                                    try {
                                        val tempAssetDb = java.io.File(context.cacheDir, "temp_asset_bundle.db")
                                        context.assets.open("database/rootchart_clinical.db").use { input ->
                                            tempAssetDb.outputStream().use { output ->
                                                input.copyTo(output)
                                            }
                                        }
                                        val assetDb = SQLiteDatabase.openDatabase(tempAssetDb.path, null, SQLiteDatabase.OPEN_READONLY)
                                        val secCursor = assetDb.rawQuery("SELECT remedyAbbreviation, remedyFullName, section, symptomText FROM materia_medica", null)
                                        db.beginTransaction()
                                        try {
                                            while (secCursor.moveToNext()) {
                                                db.execSQL(
                                                    "INSERT OR IGNORE INTO materia_medica (remedyAbbreviation, remedyFullName, section, symptomText) VALUES (?, ?, ?, ?)",
                                                    arrayOf(secCursor.getString(0), secCursor.getString(1), secCursor.getString(2), secCursor.getString(3))
                                                )
                                            }
                                            db.setTransactionSuccessful()
                                        } finally {
                                            db.endTransaction()
                                            secCursor.close()
                                            assetDb.close()
                                            tempAssetDb.delete()
                                        }
                                        Log.d(TAG, "Pre-bundled Materia Medica populated successfully in onOpen")
                                    } catch (e: Exception) {
                                        Log.w(TAG, "Materia medica sync fallback skipped: ${e.message}")
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
