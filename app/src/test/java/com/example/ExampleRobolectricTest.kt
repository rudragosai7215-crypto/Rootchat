package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.UserProfile
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun testDatabaseOpenAndUserProfile() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getDatabase(context)
    val rubricCount = db.rubricDao().getRubricCountSync()
    println("DEBUG RUBRIC COUNT: $rubricCount")
    val dao = db.userProfileDao()
    dao.insertProfile(UserProfile(clinicianName = "Dr. Test", role = "Doctor"))
    val profile = dao.getUserProfileSync()
    assertNotNull(profile)
    assertEquals("Dr. Test", profile?.clinicianName)
  }
}

