package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "clinical_cases")
data class CaseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val clinicianName: String = "",
    val caseType: String, // "ACUTE" or "CHRONIC"
    val dateCreated: Long = System.currentTimeMillis(),
    val dateModified: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val currentStep: Int = 0,

    // Patient identification
    val patientName: String = "",
    val patientAge: String = "",
    val patientGender: String = "",
    val patientContact: String = "",
    val patientOccupation: String = "",
    val caseDate: String = "",

    // Core clinical fields
    val chiefComplaint: String = "",
    val hpi: String = "",
    val etiology: String = "", // Exciting cause
    
    // Particular symptoms (LSMC)
    val lsmcLocation: String = "",
    val lsmcSensation: String = "",
    val lsmcModality: String = "", // Aggravation & Amelioration
    val lsmcConcomitant: String = "",

    // Mental & Physical Generals
    val mentalSymptoms: String = "",
    val physicalGenerals: String = "",
    val thermalState: String = "", // Chilly / Hot / Ambithermal
    val thirstState: String = "",  // Thirsty / Thirstless / Sips / Large quantities
    val cravingsAversions: String = "",

    // Chronic specific fields
    val pastHistory: String = "",
    val familyHistory: String = "",
    val personalHistory: String = "",
    val femaleHistory: String = "",
    val miasmaticEvaluation: String = "", // Psoric / Sycotic / Syphilitic / Tubercular / Mixed
    val miasmaticNotes: String = "",

    // Clinical Examination & Diagnosis
    val clinicalExamination: String = "",
    val vitals: String = "", // BP, Pulse, Temp, RR
    val clinicalDiagnosis: String = "",

    // Totality & Repertorization
    val totalitySymptomNotes: String = "",
    val selectedRubricsJson: String = "[]",

    // Prescription
    val prescribedRemedy: String = "",
    val prescribedRemedyFullName: String = "",
    val potency: String = "",
    val dose: String = "",
    val repetition: String = "",
    val instructions: String = "",

    // Chronic follow up
    val followUpNotes: String = "",
    val followUpDate: String = ""
)
