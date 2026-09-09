package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCaseDao {
  @Query("SELECT * FROM saved_cases ORDER BY createdAtTimestamp DESC")
  fun getAllCases(): Flow<List<SavedCaseEntity>>

  @Query("SELECT * FROM saved_cases WHERE id = :caseId")
  suspend fun getCaseById(caseId: Long): SavedCaseEntity?

  @Query("SELECT * FROM saved_cases WHERE patientName LIKE '%' || :query || '%' OR chiefComplaint LIKE '%' || :query || '%' OR prescribedRemedy LIKE '%' || :query || '%' ORDER BY createdAtTimestamp DESC")
  fun searchCases(query: String): Flow<List<SavedCaseEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCase(caseEntity: SavedCaseEntity): Long

  @Update
  suspend fun updateCase(caseEntity: SavedCaseEntity)

  @Delete
  suspend fun deleteCase(caseEntity: SavedCaseEntity)

  @Query("DELETE FROM saved_cases WHERE id = :caseId")
  suspend fun deleteCaseById(caseId: Long)
}
