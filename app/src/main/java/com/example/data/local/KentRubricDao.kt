package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface KentRubricDao {
  @Query("SELECT COUNT(*) FROM kent_rubrics")
  suspend fun getRubricsCount(): Int

  @Query("SELECT DISTINCT chapter FROM kent_rubrics ORDER BY chapter ASC")
  suspend fun getAllChapters(): List<String>

  @Query("SELECT * FROM kent_rubrics WHERE chapter = :chapter ORDER BY rubricText ASC LIMIT :limit OFFSET :offset")
  suspend fun getRubricsByChapter(chapter: String, limit: Int = 100, offset: Int = 0): List<KentRubricEntity>

  @Query("SELECT * FROM kent_rubrics WHERE rubricText LIKE '%' || :query || '%' OR chapter LIKE '%' || :query || '%' LIMIT :limit")
  suspend fun searchRubrics(query: String, limit: Int = 100): List<KentRubricEntity>

  @Query("SELECT * FROM kent_rubrics WHERE chapter = :chapter AND rubricText LIKE '%' || :query || '%' LIMIT :limit")
  suspend fun searchRubricsInChapter(chapter: String, query: String, limit: Int = 100): List<KentRubricEntity>

  @Query("SELECT * FROM kent_rubrics WHERE id = :id")
  suspend fun getRubricById(id: Long): KentRubricEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(rubrics: List<KentRubricEntity>)
}
