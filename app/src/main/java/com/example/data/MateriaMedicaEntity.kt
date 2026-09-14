package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "materia_medica",
    indices = [
        Index(value = ["remedyAbbreviation"]),
        Index(value = ["section"])
    ]
)
data class MateriaMedicaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val remedyAbbreviation: String,
    val remedyFullName: String,
    val section: String,
    val symptomText: String
)

data class MateriaMedicaRemedySummary(
    val remedyAbbreviation: String,
    val remedyFullName: String,
    val sectionCount: Int
)
