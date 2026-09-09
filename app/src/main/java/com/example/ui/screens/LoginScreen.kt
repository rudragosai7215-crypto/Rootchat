package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClinicalTerracotta
import com.example.ui.theme.ClinicalTerracottaDark
import com.example.ui.theme.LinenBackground
import com.example.ui.theme.LinenSurface
import com.example.ui.theme.SageMiasm
import com.example.ui.theme.WarmCharcoal

@Composable
fun LoginScreen(
  initialName: String,
  initialRole: String,
  onLogin: (name: String, role: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var doctorName by remember { mutableStateOf(initialName.ifBlank { "Dr. Harshad Jinjala" }) }
  var selectedRole by remember { mutableStateOf(if (initialRole.isNotBlank()) initialRole else "Doctor") }
  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(LinenBackground)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Logo & App Name Brand Presentation
      Box(
        modifier = Modifier
          .size(88.dp)
          .clip(RoundedCornerShape(26.dp))
          .background(
            Brush.linearGradient(
              colors = listOf(ClinicalTerracotta, ClinicalTerracottaDark)
            )
          )
          .border(2.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(26.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.LocalHospital,
          contentDescription = "RootChart Logo",
          tint = Color.White,
          modifier = Modifier.size(48.dp)
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "RootChart",
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        color = WarmCharcoal,
        letterSpacing = 0.5.sp,
        modifier = Modifier.testTag("app_logo_title")
      )

      Text(
        text = "CLINICAL REPERTORIZATION STUDIO",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = ClinicalTerracotta,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(top = 4.dp)
      )

      Text(
        text = "Classical Kent Repertory, Acute & Chronic Clinical Totality",
        fontSize = 13.sp,
        color = WarmCharcoal.copy(alpha = 0.65f),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 6.dp, bottom = 28.dp)
      )

      // Login Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("doctor_login_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = LinenSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ClinicalTerracotta.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = ClinicalTerracotta,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Practitioner Access",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = WarmCharcoal
              )
              Text(
                text = "Enter your name to access studio & patient cases",
                fontSize = 12.sp,
                color = WarmCharcoal.copy(alpha = 0.6f)
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Doctor Name Input
          Text(
            text = "Doctor / Clinician Name",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = WarmCharcoal.copy(alpha = 0.8f)
          )

          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = doctorName,
            onValueChange = { doctorName = it },
            placeholder = { Text("e.g. Dr. Harshad Jinjala") },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = ClinicalTerracotta
              )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
              if (doctorName.isNotBlank()) onLogin(doctorName.trim(), selectedRole)
            }),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ClinicalTerracotta,
              unfocusedBorderColor = Color(0xFFD6CEBE),
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("doctor_name_input")
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Role selection chips (Doctor / Student)
          Text(
            text = "Clinician Role",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = WarmCharcoal.copy(alpha = 0.8f)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            listOf("Doctor", "Student").forEach { role ->
              val isSelected = selectedRole == role
              FilterChip(
                selected = isSelected,
                onClick = { selectedRole = role },
                label = {
                  Text(
                    text = if (role == "Doctor") "🩺 Doctor" else "🎓 Student",
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = ClinicalTerracotta,
                  selectedLabelColor = Color.White,
                  containerColor = Color.White,
                  labelColor = WarmCharcoal
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = if (selectedRole == "Doctor") "Full clinical case taking, repertorization & prescription privileges"
                   else "Academic repertory case analysis, rubric study & case library access",
            fontSize = 11.sp,
            color = WarmCharcoal.copy(alpha = 0.6f)
          )

          Spacer(modifier = Modifier.height(20.dp))

          // Local storage note
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF2ECE1), RoundedCornerShape(8.dp))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = SageMiasm,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Local Profile: Saved on device storage. No password needed.",
              fontSize = 11.sp,
              color = WarmCharcoal.copy(alpha = 0.75f)
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Login Button
          Button(
            onClick = {
              val nameToUse = doctorName.trim().ifBlank { "Dr. Harshad Jinjala" }
              onLogin(nameToUse, selectedRole)
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("enter_studio_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = ClinicalTerracotta
            ),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              text = "Continue to Case Library",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Studio Features badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("⚡ Acute Cases", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ClinicalTerracotta)
            Text("Keynotes & Modalities", fontSize = 10.sp, color = WarmCharcoal.copy(alpha = 0.6f))
          }
        }

        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("🌿 Chronic Cases", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SageMiasm)
            Text("Constitutional Totality", fontSize = 10.sp, color = WarmCharcoal.copy(alpha = 0.6f))
          }
        }

        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.7f)),
          shape = RoundedCornerShape(12.dp)
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("📖 Kent Repertory", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WarmCharcoal)
            Text("Full Chapter Browser", fontSize = 10.sp, color = WarmCharcoal.copy(alpha = 0.6f))
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
