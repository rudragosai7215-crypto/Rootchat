package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remedy_names")
data class RemedyNameEntity(
    @PrimaryKey val abbreviation: String,
    val fullName: String,
    val commonName: String? = null
)

data class RepertorizationScore(
    val abbreviation: String,
    val fullName: String,
    val score: Int,      // Sum of remedy grades across selected rubrics
    val coverage: Int,   // Number of selected rubrics the remedy appears in
    val gradeBreakdown: Map<Long, Int> = emptyMap() // rubricId -> grade
)
