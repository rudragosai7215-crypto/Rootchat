package com.example.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

@Composable
fun GlassBackgroundScaffold(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val bgDrawable = if (isDark) R.drawable.img_dark_botanical else R.drawable.img_light_botanical
    val overlayColor = if (isDark) {
        Color(0xCC140F0D)
    } else {
        Color(0xBAF7F4EE)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Botanical background image
        Image(
            painter = painterResource(id = bgDrawable),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Soft ambient tint gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(overlayColor)
        )

        content()
    }
}

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    isDark: Boolean = false,
    borderColor: Color? = null,
    backgroundColor: Color? = null,
    elevation: Dp = 4.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val defaultBg = if (isDark) DarkGlassSurface else LightGlassSurface
    val defaultBorder = if (isDark) DarkGlassBorder else LightGlassBorder
    val surfaceColor = backgroundColor ?: defaultBg
    val strokeColor = borderColor ?: defaultBorder

    Surface(
        modifier = modifier
            .shadow(elevation, shape, clip = false)
            .clip(shape)
            .border(BorderStroke(1.dp, strokeColor), shape)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        color = surfaceColor,
        shape = shape
    ) {
        content()
    }
}

@Composable
fun CaseTypeBadge(
    caseType: String,
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    val isAcute = caseType.equals("ACUTE", ignoreCase = true)
    val bgColor = if (isAcute) TerracottaPrimary.copy(alpha = 0.22f) else SageGreenPrimary.copy(alpha = 0.25f)
    val textColor = if (isAcute) {
        if (isDark) TerracottaLight else TerracottaPrimary
    } else {
        if (isDark) SageGreenLight else SageGreenPrimary
    }
    val borderColor = if (isAcute) TerracottaPrimary.copy(alpha = 0.5f) else SageGreenPrimary.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = if (isAcute) "ACUTE (12)" else "CHRONIC (17)",
            color = textColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
