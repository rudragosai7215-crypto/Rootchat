package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET isDarkMode = :isDark WHERE id = 1")
    suspend fun updateDarkMode(isDark: Boolean)

    @Query("DELETE FROM user_profile")
    suspend fun clearProfile()
}

@Dao
interface RubricDao {
    @Query("""
        SELECT * FROM kent_rubrics 
        WHERE rubricText LIKE '%' || :query || '%' 
        OR chapter LIKE '%' || :query || '%'
        ORDER BY LENGTH(rubricText) ASC
        LIMIT :limit
    """)
    suspend fun searchRubrics(query: String, limit: Int = 40): List<RubricEntity>

    @Query("""
        SELECT * FROM kent_rubrics 
        WHERE (rubricText LIKE '%' || :term1 || '%' OR chapter LIKE '%' || :term1 || '%')
        AND (rubricText LIKE '%' || :term2 || '%' OR chapter LIKE '%' || :term2 || '%')
        ORDER BY LENGTH(rubricText) ASC
        LIMIT :limit
    """)
    suspend fun searchRubricsMulti(term1: String, term2: String, limit: Int = 40): List<RubricEntity>

    @Query("""
        SELECT * FROM kent_rubrics 
        WHERE (rubricText LIKE '%' || :term1 || '%' OR chapter LIKE '%' || :term1 || '%')
        AND (rubricText LIKE '%' || :term2 || '%' OR chapter LIKE '%' || :term2 || '%')
        AND (rubricText LIKE '%' || :term3 || '%' OR chapter LIKE '%' || :term3 || '%')
        ORDER BY LENGTH(rubricText) ASC
        LIMIT :limit
    """)
    suspend fun searchRubricsTriple(term1: String, term2: String, term3: String, limit: Int = 40): List<RubricEntity>

    @Query("""
        SELECT * FROM kent_rubrics 
        WHERE rubricText LIKE :pattern 
        OR chapter LIKE :pattern
        LIMIT :limit
    """)
    suspend fun searchRubricsByPattern(pattern: String, limit: Int = 40): List<RubricEntity>

    @Query("""
        SELECT * FROM kent_rubrics 
        WHERE rubricText LIKE :prefix || '%' 
        OR chapter LIKE :prefix || '%'
        ORDER BY LENGTH(rubricText) ASC
        LIMIT :limit
    """)
    suspend fun searchRubricsPrefix(prefix: String, limit: Int = 100): List<RubricEntity>

    @Query("SELECT COUNT(*) FROM kent_rubrics")
    fun getRubricCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM kent_rubrics")
    suspend fun getRubricCountSync(): Int

    @Query("SELECT DISTINCT chapter FROM kent_rubrics ORDER BY chapter ASC")
    suspend fun getAllChapters(): List<String>

    @Query("SELECT * FROM kent_rubrics WHERE chapter = :chapter ORDER BY rubricText ASC LIMIT :limit OFFSET :offset")
    suspend fun getRubricsByChapter(chapter: String, limit: Int = 100, offset: Int = 0): List<RubricEntity>

    @Query("SELECT * FROM kent_rubrics WHERE chapter = :chapter AND rubricText LIKE '%' || :query || '%' ORDER BY rubricText ASC LIMIT :limit OFFSET :offset")
    suspend fun searchRubricsInChapter(chapter: String, query: String, limit: Int = 100, offset: Int = 0): List<RubricEntity>

    @Query("SELECT * FROM kent_rubrics WHERE chapter IN (:chapters) ORDER BY rubricText ASC LIMIT :limit OFFSET :offset")
    suspend fun getRubricsByChapters(chapters: List<String>, limit: Int = 180, offset: Int = 0): List<RubricEntity>

    @Query("SELECT * FROM kent_rubrics WHERE chapter IN (:chapters) AND rubricText LIKE '%' || :query || '%' ORDER BY rubricText ASC LIMIT :limit OFFSET :offset")
    suspend fun searchRubricsInChapters(chapters: List<String>, query: String, limit: Int = 180, offset: Int = 0): List<RubricEntity>

    @Query("SELECT COUNT(*) FROM kent_rubrics WHERE chapter IN (:chapters)")
    suspend fun getRubricCountInChapters(chapters: List<String>): Int

    @Query("SELECT COUNT(*) FROM kent_rubrics WHERE chapter IN (:chapters) AND rubricText LIKE '%' || :query || '%'")
    suspend fun getSearchRubricCountInChapters(chapters: List<String>, query: String): Int

    @Query("SELECT * FROM kent_rubrics WHERE id = :id LIMIT 1")
    suspend fun getRubricById(id: Long): RubricEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rubrics: List<RubricEntity>)

    @Query("DELETE FROM kent_rubrics")
    suspend fun deleteAll()
}

@Dao
interface RemedyDao {
    @Query("SELECT * FROM remedy_names ORDER BY abbreviation ASC")
    fun getAllRemedies(): Flow<List<RemedyNameEntity>>

    @Query("SELECT * FROM remedy_names ORDER BY abbreviation ASC")
    suspend fun getAllRemediesSync(): List<RemedyNameEntity>

    @Query("SELECT * FROM remedy_names WHERE abbreviation = :abbr COLLATE NOCASE LIMIT 1")
    suspend fun getRemedyByAbbreviation(abbr: String): RemedyNameEntity?

    @Query("SELECT COUNT(*) FROM remedy_names")
    fun getRemedyCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM remedy_names")
    suspend fun getRemedyCountSync(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remedies: List<RemedyNameEntity>)

    @Query("""
        SELECT * FROM remedy_names 
        WHERE abbreviation LIKE '%' || :query || '%' 
        OR fullName LIKE '%' || :query || '%'
        ORDER BY abbreviation ASC
        LIMIT :limit
    """)
    suspend fun searchRemedies(query: String, limit: Int = 50): List<RemedyNameEntity>

    @Query("DELETE FROM remedy_names")
    suspend fun deleteAll()
}

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM clinician_workspaces WHERE clinicianName = :name COLLATE NOCASE LIMIT 1")
    suspend fun getWorkspaceByName(name: String): ClinicianWorkspaceEntity?

    @Query("SELECT * FROM clinician_workspaces ORDER BY lastLogin DESC")
    fun getAllWorkspaces(): Flow<List<ClinicianWorkspaceEntity>>

    @Query("SELECT DISTINCT clinicianName FROM clinician_workspaces ORDER BY lastLogin DESC")
    fun getAllClinicianNames(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: ClinicianWorkspaceEntity)

    @Query("UPDATE clinician_workspaces SET lastLogin = :time WHERE clinicianName = :name")
    suspend fun updateLastLogin(name: String, time: Long = System.currentTimeMillis())
}

@Dao
interface CaseDao {
    @Query("SELECT * FROM clinical_cases ORDER BY dateModified DESC")
    fun getAllCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM clinical_cases WHERE workspaceId = :workspaceId ORDER BY dateModified DESC")
    fun getCasesForWorkspace(workspaceId: String): Flow<List<CaseEntity>>

    @Query("SELECT * FROM clinical_cases WHERE clinicianName = :clinician ORDER BY dateModified DESC")
    fun getCasesForClinician(clinician: String): Flow<List<CaseEntity>>

    @Query("SELECT * FROM clinical_cases WHERE id = :id LIMIT 1")
    fun getCaseById(id: String): Flow<CaseEntity?>

    @Query("SELECT * FROM clinical_cases WHERE id = :id LIMIT 1")
    suspend fun getCaseByIdSync(id: String): CaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(caseEntity: CaseEntity)

    @Update
    suspend fun updateCase(caseEntity: CaseEntity)

    @Query("DELETE FROM clinical_cases WHERE id = :id")
    suspend fun deleteCaseById(id: String)

    @Query("SELECT COUNT(*) FROM clinical_cases")
    fun getTotalCaseCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM clinical_cases WHERE workspaceId = :workspaceId")
    fun getTotalCaseCountForWorkspace(workspaceId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM clinical_cases WHERE clinicianName = :clinician")
    fun getTotalCaseCountForClinician(clinician: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM clinical_cases WHERE caseType = 'ACUTE'")
    fun getAcuteCaseCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM clinical_cases WHERE workspaceId = :workspaceId AND caseType = 'ACUTE'")
    fun getAcuteCaseCountForWorkspace(workspaceId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM clinical_cases WHERE clinicianName = :clinician AND caseType = 'ACUTE'")
    fun getAcuteCaseCountForClinician(clinician: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM clinical_cases WHERE caseType = 'CHRONIC'")
    fun getChronicCaseCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM clinical_cases WHERE workspaceId = :workspaceId AND caseType = 'CHRONIC'")
    fun getChronicCaseCountForWorkspace(workspaceId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM clinical_cases WHERE clinicianName = :clinician AND caseType = 'CHRONIC'")
    fun getChronicCaseCountForClinician(clinician: String): Flow<Int>

    @Query("SELECT DISTINCT clinicianName FROM clinical_cases WHERE clinicianName != '' ORDER BY clinicianName ASC")
    fun getAllClinicians(): Flow<List<String>>

    @Query("DELETE FROM clinical_cases")
    suspend fun deleteAllCases()
}
