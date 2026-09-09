package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.Healing
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CharcoalMutedLight
import com.example.ui.theme.CharcoalTextLight
import com.example.ui.theme.TerracottaPrimaryLight
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.PractitionerProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepertoryTopBar(
  currentScreen: AppScreen,
  totalityCount: Int,
  practitionerProfile: PractitionerProfile,
  onOpenProfileDialog: () -> Unit,
  onTotalityBadgeClicked: () -> Unit,
  modifier: Modifier = Modifier
) {
  TopAppBar(
    modifier = modifier,
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface,
      titleContentColor = MaterialTheme.colorScheme.onSurface
    ),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onOpenProfileDialog() }
      ) {
        RootChartLogo(size = 36.dp)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "RootChart",
            style = MaterialTheme.typography.titleLarge.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp,
              color = CharcoalTextLight
            )
          )
          Text(
            text = "${practitionerProfile.name} • ${practitionerProfile.role}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = CharcoalMutedLight,
              fontSize = 10.sp,
              letterSpacing = 0.2.sp
            )
          )
        }
      }
    },
    actions = {
      // Practitioner profile trigger button
      Surface(
        onClick = onOpenProfileDialog,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
          .padding(end = 6.dp)
          .testTag("btn_practitioner_profile")
      ) {
        Box(
          modifier = Modifier.padding(6.dp),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Edit Practitioner Profile",
            tint = TerracottaPrimaryLight,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      // Quick Totality Badge Button
      Surface(
        onClick = onTotalityBadgeClicked,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
          .padding(end = 12.dp)
          .testTag("totality_quick_badge")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Filled.Healing,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = "$totalityCount Symptoms",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )
        }
      }
    }
  )
}

@Composable
fun RepertoryBottomNavBar(
  currentScreen: AppScreen,
  totalityCount: Int,
  onScreenSelected: (AppScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier.testTag("bottom_nav_bar"),
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 6.dp
  ) {
    NavigationBarItem(
      selected = currentScreen == AppScreen.BROWSER,
      onClick = { onScreenSelected(AppScreen.BROWSER) },
      icon = {
        Icon(
          imageVector = if (currentScreen == AppScreen.BROWSER) Icons.Filled.Search else Icons.Outlined.Search,
          contentDescription = "Repertory Rubrics Browser"
        )
      },
      label = {
        Text(
          "Rubrics",
          fontSize = 11.sp,
          fontWeight = if (currentScreen == AppScreen.BROWSER) FontWeight.Bold else FontWeight.Normal
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer
      ),
      modifier = Modifier.testTag("nav_tab_browser")
    )

    NavigationBarItem(
      selected = currentScreen == AppScreen.TOTALITY,
      onClick = { onScreenSelected(AppScreen.TOTALITY) },
      icon = {
        BadgedBox(
          badge = {
            if (totalityCount > 0) {
              Badge(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
              ) {
                Text(text = "$totalityCount")
              }
            }
          }
        ) {
          Icon(
            imageVector = if (currentScreen == AppScreen.TOTALITY) Icons.Filled.Healing else Icons.Outlined.Healing,
            contentDescription = "Totality of Symptoms"
          )
        }
      },
      label = {
        Text(
          "Totality",
          fontSize = 11.sp,
          fontWeight = if (currentScreen == AppScreen.TOTALITY) FontWeight.Bold else FontWeight.Normal
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer
      ),
      modifier = Modifier.testTag("nav_tab_totality")
    )

    NavigationBarItem(
      selected = currentScreen == AppScreen.REPERTORIZATION,
      onClick = { onScreenSelected(AppScreen.REPERTORIZATION) },
      icon = {
        Icon(
          imageVector = if (currentScreen == AppScreen.REPERTORIZATION) Icons.Filled.GridOn else Icons.Outlined.GridOn,
          contentDescription = "Repertorization Grid"
        )
      },
      label = {
        Text(
          "Grid & Rank",
          fontSize = 11.sp,
          fontWeight = if (currentScreen == AppScreen.REPERTORIZATION) FontWeight.Bold else FontWeight.Normal
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer
      ),
      modifier = Modifier.testTag("nav_tab_repertorization")
    )

    NavigationBarItem(
      selected = currentScreen == AppScreen.MATERIA_MEDICA,
      onClick = { onScreenSelected(AppScreen.MATERIA_MEDICA) },
      icon = {
        Icon(
          imageVector = if (currentScreen == AppScreen.MATERIA_MEDICA) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
          contentDescription = "Materia Medica"
        )
      },
      label = {
        Text(
          "M. Medica",
          fontSize = 11.sp,
          fontWeight = if (currentScreen == AppScreen.MATERIA_MEDICA) FontWeight.Bold else FontWeight.Normal
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer
      ),
      modifier = Modifier.testTag("nav_tab_materia_medica")
    )

    NavigationBarItem(
      selected = currentScreen == AppScreen.LIBRARY,
      onClick = { onScreenSelected(AppScreen.LIBRARY) },
      icon = {
        Icon(
          imageVector = if (currentScreen == AppScreen.LIBRARY) Icons.Filled.FolderShared else Icons.Outlined.FolderShared,
          contentDescription = "Patient Case Library"
        )
      },
      label = {
        Text(
          "Library",
          fontSize = 11.sp,
          fontWeight = if (currentScreen == AppScreen.LIBRARY) FontWeight.Bold else FontWeight.Normal
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer
      ),
      modifier = Modifier.testTag("nav_tab_library")
    )
  }
}

