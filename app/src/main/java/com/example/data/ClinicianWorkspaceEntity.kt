package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "clinician_workspaces")
data class ClinicianWorkspaceEntity(
    @PrimaryKey val clinicianName: String,
    val workspaceId: String = UUID.randomUUID().toString(),
    val role: String = "Doctor",
    val lastLogin: Long = System.currentTimeMillis()
)
