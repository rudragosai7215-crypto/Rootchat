package com.example.data.local

import androidx.room.ColumnInfo
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
data class KentRubricEntity(
  @PrimaryKey
  val id: Long,
  val chapter: String,
  @ColumnInfo(name = "rubricText")
  val rubricText: String,
  @ColumnInfo(name = "remediesJson")
  val remediesJson: String // map of remedy abbreviations to grades, e.g. {"Acon.":3,"Ars.":3}
) {
  fun toKentRubric(): com.example.data.model.KentRubric {
    val remedyList = mutableListOf<com.example.data.model.RemedyGrade>()
    try {
      val json = org.json.JSONObject(remediesJson)
      val keys = json.keys()
      while (keys.hasNext()) {
        val key = keys.next()
        val grade = json.optInt(key, 1)
        remedyList.add(com.example.data.model.RemedyGrade(key, grade))
      }
    } catch (_: Exception) {}

    return com.example.data.model.KentRubric(
      id = id.toString(),
      chapter = chapter,
      rubricName = rubricText,
      subRubric = "",
      remedies = remedyList,
      miasm = when (chapter.lowercase()) {
        "mind", "vertigo", "sleep" -> "Psora"
        "genitalia female", "genitalia male", "urinary organs", "urethra", "bladder", "kidneys", "prostate gland" -> "Sycosis"
        "throat", "mouth", "teeth", "external throat" -> "Syphilis"
        "chest", "respiration", "cough", "larynx and trachea", "expectoration" -> "Tubercular"
        else -> "Psora"
      }
    )
  }
}
