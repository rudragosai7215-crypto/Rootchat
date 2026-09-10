package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.DarkGlassSurface
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.LightGlassSurface
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary

enum class NavTab {
    HOME,
    LIBRARY,
    REPORTS,
    PROFILE
}

@Composable
fun RootChartBottomBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onNewCaseClicked: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val barBg = if (isDark) DarkGlassSurface else LightGlassSurface
    val barBorder = if (isDark) DarkGlassBorder else LightGlassBorder
    val activeColor = if (isDark) TerracottaLight else TerracottaPrimary
    val inactiveColor = if (isDark) Color(0xFF9E928A) else Color(0xFF7A6A61)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main floating glass pill
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(12.dp, RoundedCornerShape(26.dp))
                .clip(RoundedCornerShape(26.dp))
                .border(1.dp, barBorder, RoundedCornerShape(26.dp)),
            color = barBg
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    label = "Home",
                    selected = currentTab == NavTab.HOME,
                    iconSelected = Icons.Filled.Home,
                    iconUnselected = Icons.Outlined.Home,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    testTag = "nav_home",
                    onClick = { onTabSelected(NavTab.HOME) }
                )

                BottomNavItem(
                    label = "Library",
                    selected = currentTab == NavTab.LIBRARY,
                    iconSelected = Icons.Filled.CollectionsBookmark,
                    iconUnselected = Icons.Outlined.CollectionsBookmark,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    testTag = "nav_library",
                    onClick = { onTabSelected(NavTab.LIBRARY) }
                )

                // Space for raised center button
                Spacer(modifier = Modifier.size(52.dp))

                BottomNavItem(
                    label = "Reports",
                    selected = currentTab == NavTab.REPORTS,
                    iconSelected = Icons.Filled.BarChart,
                    iconUnselected = Icons.Outlined.BarChart,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    testTag = "nav_reports",
                    onClick = { onTabSelected(NavTab.REPORTS) }
                )

                BottomNavItem(
                    label = "Profile",
                    selected = currentTab == NavTab.PROFILE,
                    iconSelected = Icons.Filled.Person,
                    iconUnselected = Icons.Outlined.Person,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    testTag = "nav_profile",
                    onClick = { onTabSelected(NavTab.PROFILE) }
                )
            }
        }

        // Central Raised "+" New Case Button
        Box(
            modifier = Modifier
                .offset(y = (-14).dp)
                .size(56.dp)
                .shadow(14.dp, CircleShape)
                .clip(CircleShape)
                .background(TerracottaPrimary)
                .border(2.dp, Color(0x66FFFFFF), CircleShape)
                .clickable { onNewCaseClicked() }
                .testTag("new_case_fab"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Start New Case",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selected: Boolean,
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    activeColor: Color,
    inactiveColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (selected) iconSelected else iconUnselected,
            contentDescription = label,
            tint = if (selected) activeColor else inactiveColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = if (selected) activeColor else inactiveColor,
            fontSize = 10.5.sp,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
