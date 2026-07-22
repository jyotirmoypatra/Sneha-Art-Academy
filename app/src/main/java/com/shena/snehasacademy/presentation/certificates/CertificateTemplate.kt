package com.shena.snehasacademy.presentation.certificates

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shena.snehasacademy.core.components.AcademyLogo
import kotlin.math.roundToInt

/** Data needed to render a single certificate. All values are plain strings so the template stays fully reusable. */
data class CertificateTemplateData(
    val certificateId: String,
    val studentId: String,
    val studentName: String,
    val courseName: String,
    val courseDuration: String,
    val completionDate: String,
    val issueDate: String
)

private val CertIvory = Color(0xFFFBF6EC)
private val CertGold = Color(0xFFC9A227)
private val CertGoldLight = Color(0xFFE8CE8B)
private val CertMaroon = Color(0xFF6B0F1A)
private val CertDarkBrown = Color(0xFF3B2113)

/** A4-landscape aspect ratio (3508 x 2480 px @ 300dpi), laid out at a fixed design size and scaled to fit. */
private val CertDesignWidth = 1400.dp
private val CertDesignHeight = CertDesignWidth * (2480f / 3508f)

/**
 * The premium certificate-of-completion design, scaled to fit whatever width it's given
 * while preserving the exact A4-landscape proportions and layout.
 */
@Composable
fun CertificateTemplate(data: CertificateTemplateData, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        val scale = maxWidth / CertDesignWidth
        // A plain Box here would let the parent's tighter constraints coerce the "fixed" 1400dp
        // design size down before graphicsLayer ever gets to scale it (shrinking it twice).
        // Measuring with unbounded Constraints() guarantees the design is always laid out at its
        // true 1400dp size first, then scaled down visually to fit.
        Layout(
            content = {
                Box(
                    modifier = Modifier
                        .size(CertDesignWidth, CertDesignHeight)
                        .graphicsLayer(scaleX = scale, scaleY = scale, transformOrigin = TransformOrigin(0f, 0f))
                ) {
                    CertificateContent(data)
                }
            }
        ) { measurables, _ ->
            val placeable = measurables.first().measure(Constraints())
            val width = (placeable.width * scale).roundToInt()
            val height = (placeable.height * scale).roundToInt()
            layout(width, height) {
                placeable.place(0, 0)
            }
        }
    }
}

@Composable
private fun CertificateContent(data: CertificateTemplateData) {
    Box(modifier = Modifier.size(CertDesignWidth, CertDesignHeight).background(CertIvory)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGoldFrame()
            drawCornerFloral(Offset(0f, 0f), mirrorX = false, mirrorY = false)
            drawCornerFloral(Offset(size.width, 0f), mirrorX = true, mirrorY = false)
            drawCornerFloral(Offset(0f, size.height), mirrorX = false, mirrorY = true)
            drawCornerFloral(Offset(size.width, size.height), mirrorX = true, mirrorY = true)
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 90.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AcademyLogo(modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                "SNEHA'S",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                letterSpacing = 4.sp,
                color = CertMaroon
            )
            Text(
                "MEHENDI & ART ACADEMY",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 3.sp,
                color = CertDarkBrown
            )
            Spacer(Modifier.height(12.dp))
            GoldDivider()
            Spacer(Modifier.height(16.dp))
            Text(
                "CERTIFICATE",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 42.sp,
                letterSpacing = 6.sp,
                color = CertMaroon
            )
            Text(
                "OF COMPLETION",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                letterSpacing = 8.sp,
                color = CertGold
            )
            Spacer(Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                CertInfoBox("CERTIFICATE ID", data.certificateId)
                CertInfoBox("STUDENT ID", data.studentId, alignEnd = true)
            }
            Spacer(Modifier.height(22.dp))
            Text(
                "This is to proudly certify that",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 14.sp,
                color = CertDarkBrown
            )
            Spacer(Modifier.height(10.dp))
            Text(
                data.studentName,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                fontSize = 38.sp,
                color = CertMaroon,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(14.dp))
            Text(
                "has successfully completed the",
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp,
                color = CertDarkBrown
            )
            Spacer(Modifier.height(12.dp))
            CourseBanner(data.courseName)
            Spacer(Modifier.height(20.dp))
            Text(
                "The student has successfully completed all practical and theoretical requirements of the " +
                    "course with dedication, creativity, outstanding craftsmanship, and excellent performance. " +
                    "This achievement reflects commitment, artistic excellence, and professional skill " +
                    "development through the training conducted by Sneha's Mehendi & Art Academy.",
                fontFamily = FontFamily.Serif,
                fontSize = 11.5.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                color = CertDarkBrown,
                modifier = Modifier.widthIn(max = 940.dp)
            )
            Spacer(Modifier.height(22.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                CertInfoCard("Course Duration", data.courseDuration)
                CertInfoCard("Completion Date", data.completionDate)
                CertInfoCard("Issue Date", data.issueDate)
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Spacer(Modifier.weight(1f))
                CertificateSeal()
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Box(Modifier.width(150.dp).height(1.dp).background(CertDarkBrown))
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Sneha Mondal",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CertDarkBrown
                    )
                    Text(
                        "Founder — Authorized Signature",
                        fontSize = 10.sp,
                        color = CertDarkBrown.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CertInfoBox(label: String, value: String, alignEnd: Boolean = false) {
    Column(
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
        modifier = Modifier
            .border(BorderStroke(1.dp, CertGold), RoundedCornerShape(6.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label, fontSize = 9.sp, letterSpacing = 2.sp, color = CertGold, fontWeight = FontWeight.Bold)
        Text(
            value.ifBlank { "—" },
            fontSize = 13.sp,
            color = CertDarkBrown,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif
        )
    }
}

@Composable
private fun CourseBanner(courseName: String) {
    Box(
        modifier = Modifier
            .border(BorderStroke(1.dp, CertGold), RoundedCornerShape(30.dp))
            .background(CertMaroon.copy(alpha = 0.06f), RoundedCornerShape(30.dp))
            .padding(horizontal = 40.dp, vertical = 10.dp)
    ) {
        Text(
            courseName.ifBlank { "—" },
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            letterSpacing = 1.sp,
            color = CertMaroon
        )
    }
}

@Composable
private fun CertInfoCard(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(220.dp)
            .border(BorderStroke(1.dp, CertGoldLight), RoundedCornerShape(10.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Text(label, fontSize = 10.sp, letterSpacing = 1.sp, color = CertGold, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(
            value.ifBlank { "—" },
            fontSize = 13.sp,
            color = CertDarkBrown,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif
        )
    }
}

@Composable
private fun GoldDivider() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(70.dp).height(1.dp).background(CertGold))
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(8.dp)
                .graphicsLayer(rotationZ = 45f)
                .background(CertGold, RoundedCornerShape(2.dp))
        )
        Box(Modifier.width(70.dp).height(1.dp).background(CertGold))
    }
}

@Composable
private fun CertificateSeal() {
    Box(modifier = Modifier.size(92.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = CertGold, radius = size.minDimension / 2, style = Stroke(width = 3.dp.toPx()))
            drawCircle(
                color = CertGoldLight,
                radius = size.minDimension / 2 - 8.dp.toPx(),
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(color = CertGold.copy(alpha = 0.12f), radius = size.minDimension / 2 - 8.dp.toPx())
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("S", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Black, fontSize = 24.sp, color = CertMaroon)
            Text("OFFICIAL SEAL", fontSize = 6.5.sp, letterSpacing = 1.sp, color = CertDarkBrown)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGoldFrame() {
    val outerInset = 26.dp.toPx()
    val innerInset = 40.dp.toPx()
    drawRect(
        color = CertGold,
        topLeft = Offset(outerInset, outerInset),
        size = Size(size.width - 2 * outerInset, size.height - 2 * outerInset),
        style = Stroke(width = 6.dp.toPx())
    )
    drawRect(
        color = CertGoldLight,
        topLeft = Offset(innerInset, innerInset),
        size = Size(size.width - 2 * innerInset, size.height - 2 * innerInset),
        style = Stroke(width = 2.dp.toPx())
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCornerFloral(
    corner: Offset,
    mirrorX: Boolean,
    mirrorY: Boolean
) {
    val sx = if (mirrorX) -1f else 1f
    val sy = if (mirrorY) -1f else 1f
    val len = 90.dp.toPx()
    withTransform({
        translate(corner.x, corner.y)
        scale(sx, sy, pivot = Offset.Zero)
    }) {
        val sweep = Path().apply {
            moveTo(0f, len)
            quadraticBezierTo(len * 0.15f, len * 0.15f, len, 0f)
        }
        drawPath(sweep, color = CertGold, style = Stroke(width = 3.dp.toPx()))

        val leaf = Path().apply {
            moveTo(len * 0.35f, len * 0.05f)
            quadraticBezierTo(len * 0.5f, len * 0.25f, len * 0.3f, len * 0.42f)
            quadraticBezierTo(len * 0.14f, len * 0.25f, len * 0.35f, len * 0.05f)
            close()
        }
        drawPath(leaf, color = CertMaroon.copy(alpha = 0.5f))
        drawCircle(color = CertGold, radius = 4.dp.toPx(), center = Offset(len * 0.1f, len * 0.1f))
        drawCircle(color = CertGold, radius = 2.5.dp.toPx(), center = Offset(len * 0.55f, len * 0.55f))
    }
}
