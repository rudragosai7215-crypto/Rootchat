package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "kent_rubrics",
    indices = [
        Index(value = ["chapter"]),
        Index(value = ["rubricText"])
    ]
)
data class RubricEntity(
    @PrimaryKey val id: Long,
    val chapter: String,
    val rubricText: String,
    val source: String? = "Kent",
    val remediesJson: String // Serialized map of abbreviation -> grade
)

data class SelectedRubric(
    val id: Long,
    val chapter: String,
    val rubricText: String,
    val remedies: Map<String, Int> = emptyMap()
)
