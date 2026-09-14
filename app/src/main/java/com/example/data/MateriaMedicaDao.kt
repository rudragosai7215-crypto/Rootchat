package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MateriaMedicaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<MateriaMedicaEntity>)

    @Query("""
        SELECT * FROM materia_medica 
        WHERE remedyAbbreviation = :abbrev COLLATE NOCASE 
        ORDER BY id ASC
    """)
    fun getSectionsForRemedy(abbrev: String): Flow<List<MateriaMedicaEntity>>

    @Query("""
        SELECT * FROM materia_medica 
        WHERE remedyAbbreviation = :abbrev COLLATE NOCASE 
        ORDER BY id ASC
    """)
    suspend fun getSectionsForRemedySync(abbrev: String): List<MateriaMedicaEntity>

    @Query("""
        SELECT * FROM materia_medica 
        WHERE remedyFullName = :name COLLATE NOCASE 
        OR remedyFullName LIKE :name || '%' 
        ORDER BY id ASC
    """)
    suspend fun getSectionsForRemedyByName(name: String): List<MateriaMedicaEntity>

    @Query("""
        SELECT remedyAbbreviation, remedyFullName, COUNT(*) as sectionCount 
        FROM materia_medica 
        GROUP BY remedyAbbreviation, remedyFullName 
        ORDER BY remedyFullName ASC
    """)
    fun getAllRemediesSummary(): Flow<List<MateriaMedicaRemedySummary>>

    @Query("""
        SELECT remedyAbbreviation, remedyFullName, COUNT(*) as sectionCount 
        FROM materia_medica 
        WHERE remedyAbbreviation LIKE '%' || :query || '%' 
        OR remedyFullName LIKE '%' || :query || '%' 
        GROUP BY remedyAbbreviation, remedyFullName 
        ORDER BY remedyFullName ASC
        LIMIT :limit
    """)
    suspend fun searchRemediesSummary(query: String, limit: Int = 50): List<MateriaMedicaRemedySummary>

    @Query("""
        SELECT * FROM materia_medica 
        WHERE symptomText LIKE '%' || :query || '%' 
        ORDER BY remedyFullName ASC 
        LIMIT :limit
    """)
    suspend fun searchSymptomText(query: String, limit: Int = 100): List<MateriaMedicaEntity>

    @Query("SELECT COUNT(*) FROM materia_medica")
    fun getTotalSectionCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM materia_medica")
    suspend fun getTotalSectionCountSync(): Int

    @Query("SELECT COUNT(DISTINCT remedyAbbreviation) FROM materia_medica")
    fun getDistinctRemedyCount(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT remedyAbbreviation) FROM materia_medica")
    suspend fun getDistinctRemedyCountSync(): Int

    @Query("DELETE FROM materia_medica")
    suspend fun deleteAll()
}
