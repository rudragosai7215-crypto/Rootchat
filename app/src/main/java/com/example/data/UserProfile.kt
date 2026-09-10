package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val clinicianName: String,
    val role: String, // "Doctor" or "Student"
    val isDarkMode: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
