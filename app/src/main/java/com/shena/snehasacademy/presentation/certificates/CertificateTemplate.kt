package com.shena.snehasacademy.presentation.certificates

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shena.snehasacademy.R
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

private val CertDarkBrown = Color(0xFF3B2113)
private val CertOnRibbon = Color(0xFFFBF1DC)
private val GreatVibes = FontFamily(Font(R.font.great_vibes))

/** cert_background.png's real pixel size — everything (border, seal, logo, labels) is baked in; we only overlay values. */
private val CertDesignWidth = 1400.dp
private val CertDesignHeight = CertDesignWidth * (2731f / 4096f)

private fun bias(fraction: Float) = fraction * 2f - 1f

/**
 * The certificate design: a full pre-made background image with the dynamic values
 * (certificate id, student id, student name, course name, duration/dates) overlaid at
 * fixed positions. Scaled to fit whatever width it's given, preserving the image's
 * exact proportions.
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
    Box(modifier = Modifier.size(CertDesignWidth, CertDesignHeight)) {
        Image(
            painter = painterResource(id = R.drawable.cert_background),
            contentDescription = null,
            modifier = Modifier.size(CertDesignWidth, CertDesignHeight),
            contentScale = ContentScale.FillBounds
        )

        // NOTE: positions are fractions of the image (0,0 = top-left, 1,1 = bottom-right),
        // eyeballed against cert_background.png's blank spots. Nudge the fraction pairs below
        // if a value needs to shift.
        CertOverlay(data.certificateId, xFraction = 0.120f, yFraction = 0.318f, fontSize = 15.sp, boxWidth = 220.dp)
        CertOverlay(data.studentId, xFraction = 0.880f, yFraction = 0.318f, fontSize = 15.sp, boxWidth = 220.dp)
        CertOverlay(
            data.studentName,
            xFraction = 0.5f,
            yFraction = 0.550f,
            fontSize = 52.sp,
            fontFamily = GreatVibes,
            fontWeight = FontWeight.Normal,
            boxWidth = 800.dp
        )
        CertOverlay(
            data.courseName.uppercase(),
            xFraction = 0.5f,
            yFraction = 0.693f,
            fontSize = 19.sp,
            letterSpacing = 1.sp,
            color = CertOnRibbon,
            boxWidth = 600.dp
        )
        CertOverlay(data.courseDuration, xFraction = 0.135f, yFraction = 0.945f, fontSize = 14.sp, boxWidth = 200.dp)
        CertOverlay(data.completionDate, xFraction = 0.315f, yFraction = 0.945f, fontSize = 14.sp, boxWidth = 200.dp)
        CertOverlay(data.issueDate, xFraction = 0.675f, yFraction = 0.945f, fontSize = 14.sp, boxWidth = 200.dp)
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.CertOverlay(
    text: String,
    xFraction: Float,
    yFraction: Float,
    fontSize: TextUnit,
    boxWidth: androidx.compose.ui.unit.Dp,
    fontFamily: FontFamily = FontFamily.Serif,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = CertDarkBrown,
    letterSpacing: TextUnit = TextUnit.Unspecified
) {
    Text(
        text.ifBlank { "—" },
        modifier = Modifier
            .align(BiasAlignment(bias(xFraction), bias(yFraction)))
            .size(boxWidth, 40.dp),
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        fontSize = fontSize,
        letterSpacing = letterSpacing,
        color = color,
        textAlign = TextAlign.Center,
        maxLines = 1
    )
}
