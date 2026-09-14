package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.caseflow.BrowseChapterItem
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary
import kotlin.math.cos
import kotlin.math.sin

/**
 * Custom minimalist semantic line-art icons for all 33 Classical Kent Chapters
 * and 2 Additional Chapters (Blood, Clinical), adhering strictly to Reference Image 2.
 */
@Composable
fun ChapterSemanticIcon(
    chapterName: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val norm = chapterName.uppercase().trim()

    Canvas(modifier = modifier.size(26.dp)) {
        val strokeWidth = 1.8.dp.toPx()
        val w = size.width
        val h = size.height

        when {
            // 1. MIND -> Brain with cerebral hemispheres & sulci
            norm == "MIND" || norm.contains("MIND") -> {
                drawBrain(tint, strokeWidth, w, h)
            }
            // 2. VERTIGO -> Spinning spiral / vortex
            norm == "VERTIGO" || norm.contains("VERTIGO") -> {
                drawSpiral(tint, strokeWidth, w, h)
            }
            // 3. HEAD -> Head/skull profile silhouette
            norm == "HEAD" -> {
                drawHeadProfile(tint, strokeWidth, w, h)
            }
            // 5. VISION -> Eye with radiant rays
            norm == "VISION" || norm.contains("VISION") -> {
                drawVision(tint, strokeWidth, w, h)
            }
            // 4. EYE -> Almond eye with pupil
            norm == "EYE" || norm.contains("EYE") -> {
                drawEye(tint, strokeWidth, w, h)
            }
            // 7. HEARING -> Ear with acoustic sound waves
            norm == "HEARING" || norm.contains("HEARING") -> {
                drawHearing(tint, strokeWidth, w, h)
            }
            // 6. EAR -> Anatomical ear auricle
            norm == "EAR" || norm.contains("EAR") -> {
                drawEar(tint, strokeWidth, w, h)
            }
            // 8. NOSE -> Nose profile with nostril
            norm == "NOSE" || norm.contains("NOSE") || norm.contains("SMELL") -> {
                drawNose(tint, strokeWidth, w, h)
            }
            // 9. FACE -> Face profile silhouette
            norm == "FACE" || norm.contains("FACE") -> {
                drawFace(tint, strokeWidth, w, h)
            }
            // 10. MOUTH -> Lips / mouth outline
            norm == "MOUTH" || norm.contains("MOUTH") -> {
                drawLips(tint, strokeWidth, w, h)
            }
            // 11. TEETH -> Molar tooth with anatomical roots
            norm == "TEETH" || norm.contains("TOOTH") || norm.contains("TEETH") -> {
                drawTooth(tint, strokeWidth, w, h)
            }
            // 13. EXTERNAL THROAT -> Neck with thyroid & clavicle anchors
            norm == "EXTERNAL THROAT" || norm.contains("EXTERNAL THROAT") -> {
                drawExternalThroat(tint, strokeWidth, w, h)
            }
            // 12. THROAT -> Neck / throat contours
            norm == "THROAT" || norm.contains("THROAT") -> {
                drawThroat(tint, strokeWidth, w, h)
            }
            // 14. STOMACH -> Stomach organ contour
            norm == "STOMACH" || norm.contains("STOMACH") || norm.contains("APPETITE") -> {
                drawStomach(tint, strokeWidth, w, h)
            }
            // 15. ABDOMEN -> Intestines / abdominal organ coils
            norm == "ABDOMEN" || norm.contains("ABDOMEN") -> {
                drawAbdomen(tint, strokeWidth, w, h)
            }
            // 16. RECTUM -> Rectal & colon tract
            norm == "RECTUM" || norm.contains("RECTUM") -> {
                drawRectum(tint, strokeWidth, w, h)
            }
            // 17. STOOL -> Digestive elimination / swirl
            norm == "STOOL" || norm.contains("STOOL") -> {
                drawStool(tint, strokeWidth, w, h)
            }
            // 18. URINARY ORGANS -> Kidneys & Bladder
            norm == "URINARY ORGANS" || norm.contains("URIN") || norm.contains("BLADDER") || norm.contains("KIDNEY") -> {
                drawUrinaryOrgans(tint, strokeWidth, w, h)
            }
            // 19. MALE GENITALIA -> Mars symbol ♂
            norm.contains("MALE") || norm.contains("GENITALIA MALE") -> {
                drawMarsSymbol(tint, strokeWidth, w, h)
            }
            // 20. FEMALE GENITALIA -> Venus symbol ♀
            norm.contains("FEMALE") || norm.contains("GENITALIA FEMALE") -> {
                drawVenusSymbol(tint, strokeWidth, w, h)
            }
            // 21. LARYNX AND TRACHEA -> Vocal box and ribbed airway
            norm.contains("LARYNX") || norm.contains("TRACHEA") -> {
                drawLarynxTrachea(tint, strokeWidth, w, h)
            }
            // 23. COUGH -> Head coughing with blast indication
            norm == "COUGH" || norm.contains("COUGH") -> {
                drawCough(tint, strokeWidth, w, h)
            }
            // 24. EXPECTORATION -> Airway with bronchial secretions
            norm == "EXPECTORATION" || norm.contains("EXPECTORATION") -> {
                drawExpectoration(tint, strokeWidth, w, h)
            }
            // 22. RESPIRATION -> Lungs with bronchial tree
            norm == "RESPIRATION" || norm.contains("RESPIRATION") || norm.contains("LUNG") -> {
                drawLungs(tint, strokeWidth, w, h)
            }
            // 25. CHEST -> Ribcage / Thorax
            norm == "CHEST" || norm.contains("CHEST") -> {
                drawRibcage(tint, strokeWidth, w, h)
            }
            // 26. BACK -> Spinal vertebral column
            norm == "BACK" || norm.contains("BACK") || norm.contains("SPINE") -> {
                drawSpine(tint, strokeWidth, w, h)
            }
            // 27. EXTREMITIES -> Limb joint / bone junction
            norm == "EXTREMITIES" || norm.contains("EXTREMITIES") -> {
                drawJoint(tint, strokeWidth, w, h)
            }
            // 28. SLEEP -> Bed with zzz indication
            norm == "SLEEP" || norm.contains("SLEEP") || norm.contains("DREAM") -> {
                drawSleep(tint, strokeWidth, w, h)
            }
            // 29. CHILL -> Snowflake / frost crystal
            norm == "CHILL" || norm.contains("CHILL") -> {
                drawSnowflake(tint, strokeWidth, w, h)
            }
            // 30. FEVER -> Thermometer with temperature mercury
            norm == "FEVER" || norm.contains("FEVER") -> {
                drawThermometer(tint, strokeWidth, w, h)
            }
            // 31. PERSPIRATION -> Water / sweat droplets
            norm == "PERSPIRATION" || norm.contains("PERSPIRATION") || norm.contains("SWEAT") -> {
                drawPerspiration(tint, strokeWidth, w, h)
            }
            // 32. SKIN -> Hand with dermatological markings
            norm == "SKIN" || norm.contains("SKIN") -> {
                drawSkinHand(tint, strokeWidth, w, h)
            }
            // 33. GENERALITIES -> Standing human figure
            norm == "GENERALITIES" || norm.contains("GENERAL") -> {
                drawHumanFigure(tint, strokeWidth, w, h)
            }
            // ADDITIONAL 1: BLOOD -> Blood drop
            norm == "BLOOD" || norm.contains("BLOOD") || norm.contains("CIRCULATION") -> {
                drawBloodDrop(tint, strokeWidth, w, h)
            }
            // ADDITIONAL 2: CLINICAL -> Clipboard with clinical cross
            norm == "CLINICAL" || norm.contains("CLINIC") -> {
                drawClinicalClipboard(tint, strokeWidth, w, h)
            }
            // Fallback: Elegant book symbol
            else -> {
                drawBookSymbol(tint, strokeWidth, w, h)
            }
        }
    }
}

/**
 * Circular Chapter Icon item for horizontal carousel matching Reference Image 1 & 2.
 */
@Composable
fun CircularChapterItem(
    item: BrowseChapterItem,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeBg = if (isDark) TerracottaPrimary else TerracottaPrimary
    val unselectedBg = if (isDark) Color(0xFF28201A) else Color(0xFFF3ECE0)
    val borderColor = if (isSelected) {
        if (isDark) TerracottaLight else TerracottaPrimary
    } else {
        if (isDark) DarkGlassBorder else LightGlassBorder
    }
    val iconTint = if (isSelected) Color.White else (if (isDark) CreamIvoryText else DarkBrownText)
    val labelColor = if (isSelected) (if (isDark) TerracottaLight else TerracottaPrimary) else (if (isDark) CreamIvoryText else DarkBrownText)

    Column(
        modifier = modifier
            .width(68.dp)
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular Icon Container
        Box(
            modifier = Modifier
                .size(54.dp)
                .then(
                    if (isSelected) Modifier.shadow(6.dp, CircleShape) else Modifier
                )
                .clip(CircleShape)
                .background(if (isSelected) activeBg else unselectedBg)
                .border(if (isSelected) 1.5.dp else 1.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            ChapterSemanticIcon(
                chapterName = item.title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.title,
            fontSize = 11.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = labelColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ==========================================
// CANVASES FOR SEMANTIC LINE-ART CHAPTER ICONS
// ==========================================

private fun DrawScope.drawBrain(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Left hemisphere
    val leftPath = Path().apply {
        moveTo(w * 0.48f, h * 0.15f)
        cubicTo(w * 0.25f, h * 0.10f, w * 0.10f, h * 0.30f, w * 0.15f, h * 0.50f)
        cubicTo(w * 0.08f, h * 0.65f, w * 0.20f, h * 0.88f, w * 0.46f, h * 0.85f)
        // Sulci folds
        moveTo(w * 0.48f, h * 0.35f)
        cubicTo(w * 0.30f, h * 0.32f, w * 0.25f, h * 0.48f, w * 0.40f, h * 0.52f)
        moveTo(w * 0.46f, h * 0.65f)
        cubicTo(w * 0.28f, h * 0.65f, w * 0.28f, h * 0.75f, w * 0.42f, h * 0.78f)
    }
    // Right hemisphere
    val rightPath = Path().apply {
        moveTo(w * 0.52f, h * 0.15f)
        cubicTo(w * 0.75f, h * 0.10f, w * 0.90f, h * 0.30f, w * 0.85f, h * 0.50f)
        cubicTo(w * 0.92f, h * 0.65f, w * 0.80f, h * 0.88f, w * 0.54f, h * 0.85f)
        // Sulci folds
        moveTo(w * 0.52f, h * 0.35f)
        cubicTo(w * 0.70f, h * 0.32f, w * 0.75f, h * 0.48f, w * 0.60f, h * 0.52f)
        moveTo(w * 0.54f, h * 0.65f)
        cubicTo(w * 0.72f, h * 0.65f, w * 0.72f, h * 0.75f, w * 0.58f, h * 0.78f)
    }
    drawPath(leftPath, tint, style = style)
    drawPath(rightPath, tint, style = style)
    drawLine(tint, Offset(w * 0.5f, h * 0.16f), Offset(w * 0.5f, h * 0.84f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
}

private fun DrawScope.drawSpiral(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val cx = w * 0.5f
    val cy = h * 0.5f
    val path = Path()
    val totalPoints = 60
    var first = true
    for (i in 0..totalPoints) {
        val theta = i * 0.18f
        val r = (i.toFloat() / totalPoints) * (w * 0.38f)
        val x = cx + r * cos(theta)
        val y = cy + r * sin(theta)
        if (first) {
            path.moveTo(x, y)
            first = false
        } else {
            path.lineTo(x, y)
        }
    }
    drawPath(path, tint, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.drawHeadProfile(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        moveTo(w * 0.32f, h * 0.88f)
        lineTo(w * 0.32f, h * 0.70f)
        cubicTo(w * 0.18f, h * 0.55f, w * 0.20f, h * 0.18f, w * 0.52f, h * 0.16f)
        cubicTo(w * 0.75f, h * 0.15f, w * 0.82f, h * 0.32f, w * 0.78f, h * 0.42f)
        lineTo(w * 0.86f, h * 0.50f) // nose tip
        lineTo(w * 0.78f, h * 0.54f) // nostril
        lineTo(w * 0.80f, h * 0.60f) // upper lip
        lineTo(w * 0.75f, h * 0.64f) // mouth
        lineTo(w * 0.78f, h * 0.70f) // chin
        cubicTo(w * 0.72f, h * 0.78f, w * 0.62f, h * 0.80f, w * 0.60f, h * 0.88f) // neck
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawEye(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        moveTo(w * 0.12f, h * 0.5f)
        cubicTo(w * 0.30f, h * 0.22f, w * 0.70f, h * 0.22f, w * 0.88f, h * 0.5f)
        cubicTo(w * 0.70f, h * 0.78f, w * 0.30f, h * 0.78f, w * 0.12f, h * 0.5f)
        close()
    }
    drawPath(path, tint, style = style)
    drawCircle(tint, radius = w * 0.12f, center = Offset(w * 0.5f, h * 0.5f), style = style)
}

private fun DrawScope.drawVision(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Lower eye
    val eyePath = Path().apply {
        moveTo(w * 0.15f, h * 0.62f)
        cubicTo(w * 0.32f, h * 0.38f, w * 0.68f, h * 0.38f, w * 0.85f, h * 0.62f)
        cubicTo(w * 0.68f, h * 0.86f, w * 0.32f, h * 0.86f, w * 0.15f, h * 0.62f)
        close()
    }
    drawPath(eyePath, tint, style = style)
    drawCircle(tint, radius = w * 0.10f, center = Offset(w * 0.5f, h * 0.62f), style = style)

    // Rays radiating upwards
    drawLine(tint, Offset(w * 0.50f, h * 0.28f), Offset(w * 0.50f, h * 0.12f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    drawLine(tint, Offset(w * 0.30f, h * 0.33f), Offset(w * 0.18f, h * 0.20f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    drawLine(tint, Offset(w * 0.70f, h * 0.33f), Offset(w * 0.82f, h * 0.20f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
}

private fun DrawScope.drawEar(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        moveTo(w * 0.38f, h * 0.72f)
        cubicTo(w * 0.38f, h * 0.85f, w * 0.55f, h * 0.88f, w * 0.62f, h * 0.78f)
        cubicTo(w * 0.75f, h * 0.65f, w * 0.76f, h * 0.22f, w * 0.48f, h * 0.16f)
        cubicTo(w * 0.30f, h * 0.12f, w * 0.26f, h * 0.32f, w * 0.32f, h * 0.42f)
        // Inner concha
        moveTo(w * 0.48f, h * 0.32f)
        cubicTo(w * 0.58f, h * 0.35f, w * 0.58f, h * 0.58f, w * 0.44f, h * 0.60f)
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawHearing(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val earPath = Path().apply {
        moveTo(w * 0.28f, h * 0.70f)
        cubicTo(w * 0.28f, h * 0.82f, w * 0.42f, h * 0.85f, w * 0.48f, h * 0.76f)
        cubicTo(w * 0.58f, h * 0.65f, w * 0.58f, h * 0.24f, w * 0.38f, h * 0.20f)
        cubicTo(w * 0.22f, h * 0.16f, w * 0.18f, h * 0.32f, w * 0.24f, h * 0.42f)
    }
    drawPath(earPath, tint, style = style)
    // Sound waves
    drawArc(
        color = tint,
        startAngle = -45f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(w * 0.42f, h * 0.32f),
        size = Size(w * 0.28f, h * 0.36f),
        style = style
    )
    drawArc(
        color = tint,
        startAngle = -45f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(w * 0.56f, h * 0.22f),
        size = Size(w * 0.32f, h * 0.56f),
        style = style
    )
}

private fun DrawScope.drawNose(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        moveTo(w * 0.38f, h * 0.15f)
        cubicTo(w * 0.40f, h * 0.40f, w * 0.42f, h * 0.60f, w * 0.34f, h * 0.72f)
        cubicTo(w * 0.30f, h * 0.82f, w * 0.58f, h * 0.86f, w * 0.68f, h * 0.74f) // bulb & nostril
        cubicTo(w * 0.72f, h * 0.68f, w * 0.65f, h * 0.60f, w * 0.56f, h * 0.68f)
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawFace(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        moveTo(w * 0.28f, h * 0.85f)
        cubicTo(w * 0.22f, h * 0.55f, w * 0.25f, h * 0.20f, w * 0.52f, h * 0.15f)
        cubicTo(w * 0.74f, h * 0.15f, w * 0.80f, h * 0.32f, w * 0.76f, h * 0.42f)
        lineTo(w * 0.85f, h * 0.50f)
        lineTo(w * 0.77f, h * 0.54f)
        lineTo(w * 0.80f, h * 0.62f)
        lineTo(w * 0.74f, h * 0.68f)
        cubicTo(w * 0.68f, h * 0.78f, w * 0.58f, h * 0.82f, w * 0.55f, h * 0.85f)
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawLips(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        // Upper lip
        moveTo(w * 0.14f, h * 0.50f)
        cubicTo(w * 0.32f, h * 0.30f, w * 0.44f, h * 0.42f, w * 0.50f, h * 0.38f)
        cubicTo(w * 0.56f, h * 0.42f, w * 0.68f, h * 0.30f, w * 0.86f, h * 0.50f)
        // Center seam
        cubicTo(w * 0.68f, h * 0.55f, w * 0.32f, h * 0.55f, w * 0.14f, h * 0.50f)
        // Lower lip
        cubicTo(w * 0.32f, h * 0.78f, w * 0.68f, h * 0.78f, w * 0.86f, h * 0.50f)
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawTooth(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        // Crown cusps
        moveTo(w * 0.22f, h * 0.38f)
        cubicTo(w * 0.22f, h * 0.18f, w * 0.40f, h * 0.15f, w * 0.50f, h * 0.24f)
        cubicTo(w * 0.60f, h * 0.15f, w * 0.78f, h * 0.18f, w * 0.78f, h * 0.38f)
        // Right root
        cubicTo(w * 0.78f, h * 0.58f, w * 0.72f, h * 0.82f, w * 0.64f, h * 0.85f)
        cubicTo(w * 0.58f, h * 0.85f, w * 0.55f, h * 0.68f, w * 0.50f, h * 0.58f)
        // Left root
        cubicTo(w * 0.45f, h * 0.68f, w * 0.42f, h * 0.85f, w * 0.36f, h * 0.85f)
        cubicTo(w * 0.28f, h * 0.82f, w * 0.22f, h * 0.58f, w * 0.22f, h * 0.38f)
        close()
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawThroat(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        // Left neck line
        moveTo(w * 0.28f, h * 0.15f)
        cubicTo(w * 0.28f, h * 0.45f, w * 0.24f, h * 0.65f, w * 0.14f, h * 0.85f)
        // Right neck line
        moveTo(w * 0.72f, h * 0.15f)
        cubicTo(w * 0.72f, h * 0.45f, w * 0.76f, h * 0.65f, w * 0.86f, h * 0.85f)
        // Larynx notch
        moveTo(w * 0.44f, h * 0.45f)
        cubicTo(w * 0.48f, h * 0.50f, w * 0.52f, h * 0.50f, w * 0.56f, h * 0.45f)
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawExternalThroat(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        // Neck lines flaring into shoulders
        moveTo(w * 0.32f, h * 0.15f)
        cubicTo(w * 0.32f, h * 0.48f, w * 0.25f, h * 0.65f, w * 0.10f, h * 0.75f)
        moveTo(w * 0.68f, h * 0.15f)
        cubicTo(w * 0.68f, h * 0.48f, w * 0.75f, h * 0.65f, w * 0.90f, h * 0.75f)
        // Clavicle curves
        moveTo(w * 0.25f, h * 0.72f)
        cubicTo(w * 0.42f, h * 0.78f, w * 0.58f, h * 0.78f, w * 0.75f, h * 0.72f)
        // Thyroid indication
        moveTo(w * 0.50f, h * 0.42f)
        lineTo(w * 0.50f, h * 0.56f)
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawStomach(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        moveTo(w * 0.45f, h * 0.15f) // esophagus
        cubicTo(w * 0.20f, h * 0.18f, w * 0.14f, h * 0.45f, w * 0.22f, h * 0.68f) // fundus & greater curvature
        cubicTo(w * 0.30f, h * 0.88f, w * 0.65f, h * 0.88f, w * 0.75f, h * 0.72f) // antrum
        cubicTo(w * 0.82f, h * 0.60f, w * 0.72f, h * 0.52f, w * 0.60f, h * 0.52f) // pylorus / lesser curvature
        cubicTo(w * 0.50f, h * 0.52f, w * 0.50f, h * 0.28f, w * 0.45f, h * 0.15f)
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawAbdomen(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        moveTo(w * 0.25f, h * 0.25f)
        cubicTo(w * 0.75f, h * 0.25f, w * 0.75f, h * 0.42f, w * 0.25f, h * 0.42f)
        cubicTo(w * 0.15f, h * 0.52f, w * 0.85f, h * 0.52f, w * 0.75f, h * 0.62f)
        cubicTo(w * 0.25f, h * 0.62f, w * 0.25f, h * 0.80f, w * 0.65f, h * 0.80f)
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawRectum(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        // Descending colon tube into canal
        moveTo(w * 0.35f, h * 0.18f)
        lineTo(w * 0.35f, h * 0.58f)
        cubicTo(w * 0.35f, h * 0.75f, w * 0.65f, h * 0.75f, w * 0.65f, h * 0.58f)
        lineTo(w * 0.65f, h * 0.18f)
        // Sphincter canal
        moveTo(w * 0.42f, h * 0.75f)
        lineTo(w * 0.42f, h * 0.88f)
        moveTo(w * 0.58f, h * 0.75f)
        lineTo(w * 0.58f, h * 0.88f)
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawStool(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        moveTo(w * 0.50f, h * 0.18f)
        cubicTo(w * 0.65f, h * 0.25f, w * 0.65f, h * 0.38f, w * 0.50f, h * 0.42f)
        cubicTo(w * 0.28f, h * 0.45f, w * 0.25f, h * 0.62f, w * 0.50f, h * 0.65f)
        cubicTo(w * 0.78f, h * 0.68f, w * 0.78f, h * 0.84f, w * 0.22f, h * 0.84f)
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawUrinaryOrgans(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Left kidney bean
    val leftKidney = Path().apply {
        moveTo(w * 0.28f, h * 0.20f)
        cubicTo(w * 0.12f, h * 0.25f, w * 0.12f, h * 0.45f, w * 0.28f, h * 0.50f)
        cubicTo(w * 0.36f, h * 0.45f, w * 0.36f, h * 0.25f, w * 0.28f, h * 0.20f)
    }
    // Right kidney bean
    val rightKidney = Path().apply {
        moveTo(w * 0.72f, h * 0.20f)
        cubicTo(w * 0.88f, h * 0.25f, w * 0.88f, h * 0.45f, w * 0.72f, h * 0.50f)
        cubicTo(w * 0.64f, h * 0.45f, w * 0.64f, h * 0.25f, w * 0.72f, h * 0.20f)
    }
    // Ureters to bladder
    val ureters = Path().apply {
        moveTo(w * 0.30f, h * 0.45f)
        cubicTo(w * 0.38f, h * 0.55f, w * 0.44f, h * 0.65f, w * 0.46f, h * 0.72f)
        moveTo(w * 0.70f, h * 0.45f)
        cubicTo(w * 0.62f, h * 0.55f, w * 0.56f, h * 0.65f, w * 0.54f, h * 0.72f)
    }
    drawPath(leftKidney, tint, style = style)
    drawPath(rightKidney, tint, style = style)
    drawPath(ureters, tint, style = style)
    // Bladder at base
    drawArc(
        color = tint,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(w * 0.38f, h * 0.68f),
        size = Size(w * 0.24f, h * 0.20f),
        style = style
    )
}

private fun DrawScope.drawMarsSymbol(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Circle at bottom-left
    drawCircle(tint, radius = w * 0.22f, center = Offset(w * 0.42f, h * 0.58f), style = style)
    // Arrow pointing top-right
    drawLine(tint, Offset(w * 0.56f, h * 0.44f), Offset(w * 0.82f, h * 0.18f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    drawLine(tint, Offset(w * 0.65f, h * 0.18f), Offset(w * 0.82f, h * 0.18f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    drawLine(tint, Offset(w * 0.82f, h * 0.18f), Offset(w * 0.82f, h * 0.35f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
}

private fun DrawScope.drawVenusSymbol(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Circle at top
    drawCircle(tint, radius = w * 0.22f, center = Offset(w * 0.50f, h * 0.38f), style = style)
    // Cross pointing down
    drawLine(tint, Offset(w * 0.50f, h * 0.60f), Offset(w * 0.50f, h * 0.88f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    drawLine(tint, Offset(w * 0.34f, h * 0.74f), Offset(w * 0.66f, h * 0.74f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
}

private fun DrawScope.drawLarynxTrachea(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        // Larynx thyroid box
        moveTo(w * 0.32f, h * 0.18f)
        lineTo(w * 0.68f, h * 0.18f)
        lineTo(w * 0.62f, h * 0.42f)
        lineTo(w * 0.50f, h * 0.48f)
        lineTo(w * 0.38f, h * 0.42f)
        close()
        // Trachea tube with cartilaginous rings
        moveTo(w * 0.42f, h * 0.48f)
        lineTo(w * 0.42f, h * 0.85f)
        moveTo(w * 0.58f, h * 0.48f)
        lineTo(w * 0.58f, h * 0.85f)
        // Rings
        moveTo(w * 0.42f, h * 0.58f)
        lineTo(w * 0.58f, h * 0.58f)
        moveTo(w * 0.42f, h * 0.68f)
        lineTo(w * 0.58f, h * 0.68f)
        moveTo(w * 0.42f, h * 0.78f)
        lineTo(w * 0.58f, h * 0.78f)
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawLungs(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Left lung
    val leftLung = Path().apply {
        moveTo(w * 0.46f, h * 0.28f)
        cubicTo(w * 0.30f, h * 0.25f, w * 0.18f, h * 0.45f, w * 0.18f, h * 0.68f)
        cubicTo(w * 0.22f, h * 0.85f, w * 0.40f, h * 0.85f, w * 0.46f, h * 0.74f)
        close()
    }
    // Right lung
    val rightLung = Path().apply {
        moveTo(w * 0.54f, h * 0.28f)
        cubicTo(w * 0.70f, h * 0.25f, w * 0.82f, h * 0.45f, w * 0.82f, h * 0.68f)
        cubicTo(w * 0.78f, h * 0.85f, w * 0.60f, h * 0.85f, w * 0.54f, h * 0.74f)
        close()
    }
    drawPath(leftLung, tint, style = style)
    drawPath(rightLung, tint, style = style)
    // Central trachea
    drawLine(tint, Offset(w * 0.50f, h * 0.15f), Offset(w * 0.50f, h * 0.34f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
}

private fun DrawScope.drawCough(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val headPath = Path().apply {
        moveTo(w * 0.20f, h * 0.85f)
        cubicTo(w * 0.12f, h * 0.55f, w * 0.15f, h * 0.20f, w * 0.42f, h * 0.15f)
        cubicTo(w * 0.64f, h * 0.15f, w * 0.70f, h * 0.32f, w * 0.66f, h * 0.42f)
        lineTo(w * 0.75f, h * 0.50f)
        lineTo(w * 0.66f, h * 0.54f)
        lineTo(w * 0.68f, h * 0.64f)
        cubicTo(w * 0.55f, h * 0.76f, w * 0.45f, h * 0.80f, w * 0.42f, h * 0.85f)
    }
    drawPath(headPath, tint, style = style)
    // Cough sound waves
    drawLine(tint, Offset(w * 0.74f, h * 0.58f), Offset(w * 0.88f, h * 0.52f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    drawLine(tint, Offset(w * 0.75f, h * 0.64f), Offset(w * 0.92f, h * 0.64f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    drawLine(tint, Offset(w * 0.74f, h * 0.70f), Offset(w * 0.88f, h * 0.76f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
}

private fun DrawScope.drawExpectoration(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    drawLungs(tint, strokeWidth, w, h)
    // Small droplet in airway
    drawCircle(tint, radius = w * 0.05f, center = Offset(w * 0.50f, h * 0.45f))
    drawCircle(tint, radius = w * 0.04f, center = Offset(w * 0.38f, h * 0.52f))
}

private fun DrawScope.drawRibcage(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Sternum
    drawLine(tint, Offset(w * 0.50f, h * 0.16f), Offset(w * 0.50f, h * 0.84f), strokeWidth = strokeWidth * 1.2f, cap = StrokeCap.Round)
    // Bilateral ribs
    for (i in 1..4) {
        val y = h * (0.16f + i * 0.14f)
        val span = w * (0.18f + i * 0.05f)
        drawArc(
            color = tint,
            startAngle = 180f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(w * 0.50f - span, y - h * 0.06f),
            size = Size(span, h * 0.14f),
            style = style
        )
        drawArc(
            color = tint,
            startAngle = -120f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(w * 0.50f, y - h * 0.06f),
            size = Size(span, h * 0.14f),
            style = style
        )
    }
}

private fun DrawScope.drawSpine(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val cx = w * 0.50f
    for (i in 0..6) {
        val cy = h * (0.18f + i * 0.10f)
        drawArc(
            color = tint,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(cx - w * 0.14f, cy - h * 0.035f),
            size = Size(w * 0.28f, h * 0.07f),
            style = style
        )
    }
    drawLine(tint, Offset(cx, h * 0.15f), Offset(cx, h * 0.85f), strokeWidth = strokeWidth * 0.8f, cap = StrokeCap.Round)
}

private fun DrawScope.drawJoint(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        // Upper bone
        moveTo(w * 0.42f, h * 0.15f)
        lineTo(w * 0.42f, h * 0.38f)
        cubicTo(w * 0.32f, h * 0.42f, w * 0.32f, h * 0.48f, w * 0.42f, h * 0.48f)
        // Joint gap
        moveTo(w * 0.58f, h * 0.15f)
        lineTo(w * 0.58f, h * 0.38f)
        cubicTo(w * 0.68f, h * 0.42f, w * 0.68f, h * 0.48f, w * 0.58f, h * 0.48f)

        // Lower bone
        moveTo(w * 0.42f, h * 0.54f)
        cubicTo(w * 0.32f, h * 0.54f, w * 0.32f, h * 0.60f, w * 0.42f, h * 0.64f)
        lineTo(w * 0.42f, h * 0.85f)
        moveTo(w * 0.58f, h * 0.54f)
        cubicTo(w * 0.68f, h * 0.54f, w * 0.68f, h * 0.60f, w * 0.58f, h * 0.64f)
        lineTo(w * 0.58f, h * 0.85f)
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawSleep(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Bed frame
    val bedPath = Path().apply {
        moveTo(w * 0.15f, h * 0.42f)
        lineTo(w * 0.15f, h * 0.78f) // headboard
        moveTo(w * 0.15f, h * 0.65f)
        lineTo(w * 0.85f, h * 0.65f) // mattress
        lineTo(w * 0.85f, h * 0.78f) // footboard
        // Pillow
        moveTo(w * 0.22f, h * 0.60f)
        cubicTo(w * 0.22f, h * 0.52f, w * 0.38f, h * 0.52f, w * 0.38f, h * 0.60f)
    }
    drawPath(bedPath, tint, style = style)
    // "z z"
    drawZ(tint, strokeWidth, Offset(w * 0.62f, h * 0.25f), scale = 0.12f * w)
    drawZ(tint, strokeWidth, Offset(w * 0.78f, h * 0.18f), scale = 0.16f * w)
}

private fun DrawScope.drawZ(tint: Color, strokeWidth: Float, origin: Offset, scale: Float) {
    val path = Path().apply {
        moveTo(origin.x, origin.y)
        lineTo(origin.x + scale, origin.y)
        lineTo(origin.x, origin.y + scale)
        lineTo(origin.x + scale, origin.y + scale)
    }
    drawPath(path, tint, style = Stroke(width = strokeWidth * 0.8f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

private fun DrawScope.drawSnowflake(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val cx = w * 0.50f
    val cy = h * 0.50f
    val r = w * 0.38f

    // 6 primary branches
    for (i in 0..5) {
        val angle = i * (Math.PI / 3)
        val ex = cx + r * cos(angle).toFloat()
        val ey = cy + r * sin(angle).toFloat()
        drawLine(tint, Offset(cx, cy), Offset(ex, ey), strokeWidth = strokeWidth, cap = StrokeCap.Round)

        // Sub-branches
        val midX = cx + (r * 0.65f) * cos(angle).toFloat()
        val midY = cy + (r * 0.65f) * sin(angle).toFloat()
        val subR = r * 0.25f
        val a1 = angle + Math.PI / 4
        val a2 = angle - Math.PI / 4
        drawLine(tint, Offset(midX, midY), Offset(midX + subR * cos(a1).toFloat(), midY + subR * sin(a1).toFloat()), strokeWidth = strokeWidth * 0.8f, cap = StrokeCap.Round)
        drawLine(tint, Offset(midX, midY), Offset(midX + subR * cos(a2).toFloat(), midY + subR * sin(a2).toFloat()), strokeWidth = strokeWidth * 0.8f, cap = StrokeCap.Round)
    }
}

private fun DrawScope.drawThermometer(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val cx = w * 0.50f
    // Bulb
    drawCircle(tint, radius = w * 0.16f, center = Offset(cx, h * 0.72f), style = style)
    // Stem
    val stem = Path().apply {
        moveTo(cx - w * 0.08f, h * 0.62f)
        lineTo(cx - w * 0.08f, h * 0.22f)
        cubicTo(cx - w * 0.08f, h * 0.14f, cx + w * 0.08f, h * 0.14f, cx + w * 0.08f, h * 0.22f)
        lineTo(cx + w * 0.08f, h * 0.62f)
    }
    drawPath(stem, tint, style = style)
    // Mercury level
    drawLine(tint, Offset(cx, h * 0.72f), Offset(cx, h * 0.35f), strokeWidth = strokeWidth * 1.5f, cap = StrokeCap.Round)
}

private fun DrawScope.drawPerspiration(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Left drop
    val drop1 = Path().apply {
        moveTo(w * 0.38f, h * 0.22f)
        cubicTo(w * 0.18f, h * 0.52f, w * 0.18f, h * 0.75f, w * 0.38f, h * 0.75f)
        cubicTo(w * 0.55f, h * 0.75f, w * 0.55f, h * 0.52f, w * 0.38f, h * 0.22f)
    }
    // Right drop
    val drop2 = Path().apply {
        moveTo(w * 0.68f, h * 0.38f)
        cubicTo(w * 0.52f, h * 0.62f, w * 0.52f, h * 0.82f, w * 0.68f, h * 0.82f)
        cubicTo(w * 0.82f, h * 0.82f, w * 0.82f, h * 0.62f, w * 0.68f, h * 0.38f)
    }
    drawPath(drop1, tint, style = style)
    drawPath(drop2, tint, style = style)
}

private fun DrawScope.drawSkinHand(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val hand = Path().apply {
        moveTo(w * 0.35f, h * 0.85f)
        lineTo(w * 0.25f, h * 0.58f) // thumb
        cubicTo(w * 0.25f, h * 0.50f, w * 0.34f, h * 0.50f, w * 0.36f, h * 0.58f)
        lineTo(w * 0.38f, h * 0.30f) // index
        cubicTo(w * 0.38f, h * 0.22f, w * 0.48f, h * 0.22f, w * 0.48f, h * 0.30f)
        lineTo(w * 0.50f, h * 0.24f) // middle
        cubicTo(w * 0.50f, h * 0.16f, w * 0.60f, h * 0.16f, w * 0.60f, h * 0.24f)
        lineTo(w * 0.62f, h * 0.34f) // ring
        cubicTo(w * 0.62f, h * 0.28f, w * 0.72f, h * 0.28f, w * 0.72f, h * 0.38f)
        lineTo(w * 0.74f, h * 0.48f) // pinky
        cubicTo(w * 0.74f, h * 0.42f, w * 0.82f, h * 0.42f, w * 0.82f, h * 0.52f)
        cubicTo(w * 0.82f, h * 0.72f, w * 0.65f, h * 0.85f, w * 0.55f, h * 0.85f)
    }
    drawPath(hand, tint, style = style)
    // Stippled skin pores
    drawCircle(tint, radius = w * 0.02f, center = Offset(w * 0.46f, h * 0.52f))
    drawCircle(tint, radius = w * 0.02f, center = Offset(w * 0.58f, h * 0.55f))
    drawCircle(tint, radius = w * 0.02f, center = Offset(w * 0.50f, h * 0.64f))
}

private fun DrawScope.drawHumanFigure(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val cx = w * 0.50f
    // Head
    drawCircle(tint, radius = w * 0.10f, center = Offset(cx, h * 0.22f), style = style)
    // Torso
    drawLine(tint, Offset(cx, h * 0.32f), Offset(cx, h * 0.60f), strokeWidth = strokeWidth * 1.3f, cap = StrokeCap.Round)
    // Arms
    drawLine(tint, Offset(cx - w * 0.28f, h * 0.45f), Offset(cx + w * 0.28f, h * 0.45f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    // Legs
    drawLine(tint, Offset(cx, h * 0.60f), Offset(cx - w * 0.18f, h * 0.85f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
    drawLine(tint, Offset(cx, h * 0.60f), Offset(cx + w * 0.18f, h * 0.85f), strokeWidth = strokeWidth, cap = StrokeCap.Round)
}

private fun DrawScope.drawBloodDrop(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        moveTo(w * 0.50f, h * 0.15f)
        cubicTo(w * 0.22f, h * 0.48f, w * 0.22f, h * 0.82f, w * 0.50f, h * 0.82f)
        cubicTo(w * 0.78f, h * 0.82f, w * 0.78f, h * 0.48f, w * 0.50f, h * 0.15f)
        close()
    }
    drawPath(path, tint, style = style)
}

private fun DrawScope.drawClinicalClipboard(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Board
    val board = Path().apply {
        moveTo(w * 0.22f, h * 0.24f)
        lineTo(w * 0.36f, h * 0.24f)
        moveTo(w * 0.64f, h * 0.24f)
        lineTo(w * 0.78f, h * 0.24f)
        lineTo(w * 0.78f, h * 0.85f)
        lineTo(w * 0.22f, h * 0.85f)
        close()
    }
    drawPath(board, tint, style = style)
    // Clip at top
    val clip = Path().apply {
        moveTo(w * 0.36f, h * 0.24f)
        lineTo(w * 0.36f, h * 0.16f)
        lineTo(w * 0.64f, h * 0.16f)
        lineTo(w * 0.64f, h * 0.24f)
    }
    drawPath(clip, tint, style = style)
    // Medical cross in center
    val cx = w * 0.50f
    val cy = h * 0.55f
    val crossSize = w * 0.12f
    drawLine(tint, Offset(cx - crossSize, cy), Offset(cx + crossSize, cy), strokeWidth = strokeWidth * 1.3f, cap = StrokeCap.Round)
    drawLine(tint, Offset(cx, cy - crossSize), Offset(cx, cy + crossSize), strokeWidth = strokeWidth * 1.3f, cap = StrokeCap.Round)
}

private fun DrawScope.drawBookSymbol(tint: Color, strokeWidth: Float, w: Float, h: Float) {
    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path().apply {
        moveTo(w * 0.50f, h * 0.25f)
        cubicTo(w * 0.35f, h * 0.20f, w * 0.20f, h * 0.25f, w * 0.18f, h * 0.28f)
        lineTo(w * 0.18f, h * 0.75f)
        cubicTo(w * 0.20f, h * 0.72f, w * 0.35f, h * 0.68f, w * 0.50f, h * 0.72f)
        cubicTo(w * 0.65f, h * 0.68f, w * 0.80f, h * 0.72f, w * 0.82f, h * 0.75f)
        lineTo(w * 0.82f, h * 0.28f)
        cubicTo(w * 0.80f, h * 0.25f, w * 0.65f, h * 0.20f, w * 0.50f, h * 0.25f)
        close()
        moveTo(w * 0.50f, h * 0.25f)
        lineTo(w * 0.50f, h * 0.72f)
    }
    drawPath(path, tint, style = style)
}
