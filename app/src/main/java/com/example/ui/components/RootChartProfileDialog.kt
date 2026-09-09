package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CharcoalMutedLight
import com.example.ui.theme.CharcoalTextLight
import com.example.ui.theme.LinenBackgroundLight
import com.example.ui.theme.PaperSurfaceLight
import com.example.ui.theme.SecondaryContainerLight
import com.example.ui.theme.TerracottaPrimaryLight
import com.example.ui.viewmodel.PractitionerProfile

/**
 * Clean RootChart Vector Logo Mark matching IMG_2902.
 * Terracotta squircle with pulse heartbeat waveform.
 */
@Composable
fun RootChartLogo(
  modifier: Modifier = Modifier,
  size: Dp = 40.dp,
  backgroundColor: Color = TerracottaPrimaryLight,
  strokeColor: Color = Color.White
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(RoundedCornerShape(size * 0.26f))
      .background(backgroundColor),
    contentAlignment = Alignment.Center
  ) {
    Canvas(
      modifier = Modifier.size(size * 0.62f)
    ) {
      val w = this.size.width
      val h = this.size.height

      val path = Path().apply {
        moveTo(0f, h * 0.52f)
        lineTo(w * 0.28f, h * 0.52f)
        lineTo(w * 0.38f, h * 0.22f)
        lineTo(w * 0.50f, h * 0.82f)
        lineTo(w * 0.62f, h * 0.35f)
        lineTo(w * 0.72f, h * 0.52f)
        lineTo(w, h * 0.52f)
      }

      drawPath(
        path = path,
        color = strokeColor,
        style = Stroke(
          width = (size.value * 0.075f).coerceAtLeast(1.8f).dp.toPx(),
          cap = StrokeCap.Round,
          join = StrokeJoin.Round
        )
      )
    }
  }
}

/**
 * RootChart Practitioner Profile Modal Dialog matching IMG_2902
 */
@Composable
fun RootChartProfileDialog(
  currentProfile: PractitionerProfile,
  onDismiss: () -> Unit,
  onSave: (name: String, role: String) -> Unit
) {
  var name by remember { mutableStateOf(currentProfile.name) }
  var role by remember { mutableStateOf(currentProfile.role) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = LinenBackgroundLight,
      shadowElevation = 12.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .testTag("rootchart_profile_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.Start
      ) {
        // Logo and Brand Header
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          RootChartLogo(size = 40.dp)
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "RootChart",
              style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = CharcoalTextLight,
                fontSize = 22.sp
              )
            )
            Text(
              text = "CLINICAL REPERTORIZATION STUDIO",
              style = MaterialTheme.typography.labelSmall.copy(
                color = TerracottaPrimaryLight,
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "A clearer way to take the case.",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            color = CharcoalTextLight,
            fontSize = 20.sp,
            lineHeight = 26.sp
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Move from the patient's story to a thoughtful similimum with a calm, guided protocol.",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = CharcoalMutedLight,
            lineHeight = 20.sp
          )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Profile Form Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = PaperSurfaceLight),
          border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            // Field 1: YOUR NAME
            Text(
              text = "YOUR NAME",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.0.sp,
                color = CharcoalMutedLight
              )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              placeholder = { Text("e.g. Dr. Meena Patel", color = CharcoalMutedLight) },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TerracottaPrimaryLight,
                unfocusedBorderColor = CardBorderLight,
                focusedContainerColor = PaperSurfaceLight,
                unfocusedContainerColor = PaperSurfaceLight,
                focusedTextColor = CharcoalTextLight,
                unfocusedTextColor = CharcoalTextLight
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("input_practitioner_name")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Field 2: YOUR ROLE
            Text(
              text = "YOUR ROLE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.0.sp,
                color = CharcoalMutedLight
              )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Doctor Pill
              val isDoctor = role == "Doctor"
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(CircleShape)
                  .background(if (isDoctor) TerracottaPrimaryLight else SecondaryContainerLight)
                  .clickable { role = "Doctor" }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Doctor",
                  style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDoctor) Color.White else CharcoalTextLight
                  )
                )
              }

              // Student Pill
              val isStudent = role == "Student"
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(CircleShape)
                  .background(if (isStudent) TerracottaPrimaryLight else SecondaryContainerLight)
                  .clickable { role = "Student" }
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Student",
                  style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isStudent) Color.White else CharcoalTextLight
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Enter Workspace Button
            Button(
              onClick = { onSave(name, role) },
              shape = CircleShape,
              colors = ButtonDefaults.buttonColors(
                containerColor = TerracottaPrimaryLight,
                contentColor = Color.White
              ),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_enter_workspace")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Text(
                  text = "Enter workspace",
                  style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Security Footnote
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = CharcoalMutedLight,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Your profile is local to this workspace. Cases are saved securely offline.",
            style = MaterialTheme.typography.bodySmall.copy(
              color = CharcoalMutedLight,
              fontSize = 11.sp
            )
          )
        }
      }
    }
  }
}
