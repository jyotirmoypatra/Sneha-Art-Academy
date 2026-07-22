package com.shena.snehasacademy.core.components

import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shena.snehasacademy.R
import com.shena.snehasacademy.core.theme.AcademyGold
import com.shena.snehasacademy.core.theme.AcademyGreen
import com.shena.snehasacademy.core.theme.AcademyNavy
import com.shena.snehasacademy.core.theme.AcademyRose
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    height: Dp = 52.dp
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier.fillMaxWidth().height(height),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AcademyGreen)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, height: Dp = 50.dp) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(height),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = academyTextFieldColors(),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordField(value: String, onValueChange: (String) -> Unit, label: String = "Password") {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Text(
                    if (visible) "Hide" else "Show",
                    color = Color(0xFF4B260C),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        colors = academyTextFieldColors(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun academyTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF1D1B20),
    unfocusedTextColor = Color(0xFF1D1B20),
    disabledTextColor = Color(0xFF1D1B20).copy(alpha = 0.55f),
    focusedContainerColor = Color(0xFFFFFCF5),
    unfocusedContainerColor = Color(0xFFFFFCF5),
    disabledContainerColor = Color(0xFFFFFCF5),
    cursorColor = Color(0xFF4B260C),
    focusedBorderColor = Color(0xFFD6A34A),
    unfocusedBorderColor = Color(0xFF7A4A24).copy(alpha = 0.42f),
    focusedLabelColor = Color(0xFF4B260C),
    unfocusedLabelColor = Color(0xFF7A4A24),
    focusedPlaceholderColor = Color(0xFF7A4A24),
    unfocusedPlaceholderColor = Color(0xFF7A4A24).copy(alpha = 0.75f)
)

@Composable
fun DateField(label: String, dateText: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = dateText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text("Select date") },
            trailingIcon = { Text("Select", style = MaterialTheme.typography.labelMedium) },
            shape = RoundedCornerShape(16.dp),
            colors = academyTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        )
    }
}

private val ToolbarHeight = 48.dp

@Composable
fun AppToolbar(
    title: String,
    canNavigateBack: Boolean = false,
    onBack: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    if (canNavigateBack) {
        var lastBackClickAt by remember { mutableStateOf(0L) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF7E8))
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(ToolbarHeight)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val now = SystemClock.elapsedRealtime()
                    if (now - lastBackClickAt > 700L) {
                        lastBackClickAt = now
                        onBack()
                    }
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = Color(0xFF4B260C),
                    modifier = Modifier.size(18.dp)
                )
            }
            if (title.isNotBlank()) {
                Text(
                    title,
                    modifier = Modifier.weight(1f).padding(start = 10.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B260C)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            actions()
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF7E8))
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(ToolbarHeight)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AcademyLogo(Modifier.size(28.dp))
            Text(
                title,
                modifier = Modifier.weight(1f).padding(start = 10.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF4B260C)
            )
            actions()
        }
    }
}

@Composable
fun AcademyLogo(modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Fit) {
    Image(
        painter = painterResource(id = R.drawable.logo_full),
        contentDescription = "Sneha's Mehendi & Art Academy logo",
        modifier = modifier,
        contentScale = contentScale
    )
}

@Composable
fun DashboardCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(AcademyGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = title, tint = AcademyGreen, modifier = Modifier.size(20.dp))
                }
            } else {
                MehendiMark(Modifier.size(30.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueStyle: TextStyle = MaterialTheme.typography.headlineSmall
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = valueStyle, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = academyTextFieldColors(),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun ProfileCard(name: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(
                    Brush.linearGradient(listOf(AcademyGreen, AcademyGold, AcademyRose))
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.padding(start = 14.dp)) {
                Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier, style: TextStyle = MaterialTheme.typography.titleLarge) {
    Text(
        text = title,
        modifier = modifier.fillMaxWidth(),
        style = style,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                actions()
            }
            content()
        }
    }
}

@Composable
fun LabeledInfo(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value.ifBlank { "—" }, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

/**
 * A compact "Label : Value" row with a fixed-width label column so labels/colons line up
 * vertically across multiple rows (used by card summaries like Certificates and Courses).
 */
@Composable
fun LabelValueRow(label: String, value: String, modifier: Modifier = Modifier, labelWidth: Dp = 108.dp) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            "$label:",
            modifier = Modifier.width(labelWidth),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value.ifBlank { "—" },
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Shared "view certificate" dialog used by both the Enrollment Details and Certificates
 * screens, so the fields shown for a generated certificate stay identical everywhere.
 */
@Composable
fun CertificateViewDialog(
    certificateId: String,
    studentName: String,
    studentId: String,
    courseName: String,
    issueDate: String,
    createdBy: String,
    onDismiss: () -> Unit,
    onViewPreview: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Certificate") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LabeledInfo("Certificate ID", certificateId)
                LabeledInfo("Student Name", studentName)
                LabeledInfo("Student ID", studentId)
                LabeledInfo("Course", courseName)
                LabeledInfo("Issue Date", issueDate)
                LabeledInfo("Created By", createdBy)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        dismissButton = onViewPreview?.let { preview ->
            { TextButton(onClick = preview) { Text("View Preview") } }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedSelector(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = selected == option,
                    onClick = { onSelect(option) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    icon = {}
                ) {
                    Text(option)
                }
            }
        }
    }
}

@Composable
fun LoadingView() {
    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AcademyGreen)
    }
}

/**
 * Full-screen, undismissable cover shown over the entire app whenever there is no active
 * internet connection. Consumes taps and back presses so nothing underneath can be interacted
 * with; it disappears on its own once connectivity is restored (driven by [rememberIsOnline]
 * at the call site — this composable has no dismiss action of its own).
 */
@Composable
fun NoInternetOverlay() {
    BackHandler {}
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF7E8))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(AcademyRose.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = AcademyRose
                )
            }
            Text(
                "No Internet Connection",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B260C),
                textAlign = TextAlign.Center
            )
            Text(
                "Please check your Wi-Fi or mobile data. This will disappear automatically once you're back online.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7A4A24),
                textAlign = TextAlign.Center
            )
            CircularProgressIndicator(
                color = AcademyGreen,
                strokeWidth = 3.dp,
                modifier = Modifier.padding(top = 8.dp).size(28.dp)
            )
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = { MehendiMark(Modifier.size(64.dp)) }
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        icon()
        Text(title, fontWeight = FontWeight.Bold)
        Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Confirm") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun statusColor(status: String): Color = when (status.trim().lowercase()) {
    "active", "ongoing", "paid" -> AcademyGreen
    "due", "pending" -> AcademyGold
    "completed" -> AcademyNavy
    "inactive", "cancelled", "canceled" -> AcademyRose
    else -> AcademyNavy
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val color = statusColor(status)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(status, color = color, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun StatusCapsuleSelector(options: List<String>, selected: String, modifier: Modifier = Modifier, onSelect: (String) -> Unit) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        options.forEach { option ->
            val color = statusColor(option)
            val isSelected = option.equals(selected, ignoreCase = true)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) color else color.copy(alpha = 0.12f))
                    .border(1.dp, color, RoundedCornerShape(50))
                    .clickable { onSelect(option) }
                    .padding(horizontal = 3.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    color = if (isSelected) Color.White else color,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}

@Composable
fun ListItemCard(
    title: String,
    subtitle: String,
    trailing: String = "",
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth().let { if (onClick != null) it.clickable(onClick = onClick) else it },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (trailing.isNotBlank()) {
                StatusBadge(trailing, Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun ScreenScaffold(
    title: String,
    canNavigateBack: Boolean = false,
    onBack: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(topBar = { AppToolbar(title, canNavigateBack, onBack, actions) }) { padding ->
        content(Modifier.padding(padding))
    }
}

@Composable
fun MehendiMark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(AcademyGreen.copy(alpha = 0.16f), radius = size.minDimension / 2f)
        drawCircle(AcademyGold.copy(alpha = 0.85f), radius = size.minDimension * 0.18f, center = center)
        repeat(8) { index ->
            val angle = Math.toRadians((index * 45).toDouble())
            val end = Offset(
                x = center.x + kotlin.math.cos(angle).toFloat() * size.minDimension * 0.32f,
                y = center.y + kotlin.math.sin(angle).toFloat() * size.minDimension * 0.32f
            )
            drawLine(AcademyNavy.copy(alpha = 0.55f), center, end, strokeWidth = 2.5f, cap = StrokeCap.Round)
        }
        drawCircle(AcademyGreen, radius = size.minDimension * 0.39f, style = Stroke(width = 2.5f))
    }
}

/**
 * Shared header for "list" and "details/registration" screens that need a subtitle line under
 * the title plus a single action slot on the right (filter, edit, or a decorative badge) — used
 * so every such screen has byte-identical padding/typography instead of each screen re-deriving it.
 */
@Composable
fun SubtitledHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    action: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF7E8))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Back",
                tint = Color(0xFF4B260C),
                modifier = Modifier.size(18.dp)
            )
        }
        Column(Modifier.weight(1f).padding(start = 6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4B260C))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF7A4A24))
        }
        action()
    }
}

/** The small rounded-square icon box used on the right side of [SubtitledHeader] (filter/edit/badge). */
@Composable
fun HeaderIconBadge(
    icon: ImageVector,
    tint: Color,
    background: Color,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(35.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(18.dp))
    }
}

/** [HeaderIconBadge] overload for drawable-resource icons (e.g. the custom edit pencil). */
@Composable
fun HeaderIconBadge(
    painter: Painter,
    tint: Color,
    background: Color,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(35.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it },
        contentAlignment = Alignment.Center
    ) {
        Icon(painter = painter, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(18.dp))
    }
}

/** A form section with an icon-badge header, bold title + gray subtitle, and a colored accent underline. */
@Composable
fun FormSectionCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Box(Modifier.width(56.dp).height(2.dp).clip(RoundedCornerShape(1.dp)).background(iconTint))
            }
            content()
        }
    }
}

/** The small colored icon square used as the leading icon inside [FormIconField]/date fields. */
@Composable
fun FormFieldIconBox(icon: ImageVector, iconBg: Color, iconTint: Color) {
    Box(
        modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(iconBg),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
    }
}

@Composable
fun FormIconField(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
        textStyle = MaterialTheme.typography.bodySmall,
        leadingIcon = { FormFieldIconBox(icon, iconBg, iconTint) },
        singleLine = maxLines <= 1,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        colors = academyTextFieldColors(),
        modifier = modifier.fillMaxWidth()
    )
}

/** A single pill option for a 2-4 way choice row (e.g. gender, course level) — tinted + bold when selected. */
@Composable
fun SelectablePill(
    label: String,
    selected: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) accent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (selected) accent else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )
            }
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) accent else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/** A white rounded card wrapping a "details" section (used below a [ProfileSummaryCard]). */
@Composable
fun DetailSectionContainer(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp), content = content)
    }
}

/** The icon-badge + bold title header at the top of a [DetailSectionContainer], with an optional trailing action. */
@Composable
fun DetailSectionHeaderRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    trailing: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(30.dp).clip(CircleShape).background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(15.dp))
        }
        Text(
            title,
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        trailing()
    }
}

/** A "icon | label ..... value" row inside a [DetailSectionContainer], with an optional bottom divider. */
@Composable
fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconBg: Color = AcademyGreen.copy(alpha = 0.14f),
    iconTint: Color = AcademyGreen,
    showDivider: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
        }
        Text(
            label,
            modifier = Modifier.padding(start = 8.dp).weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
    if (showDivider) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    }
}

/** The icon-badge "label / bold value" meta item used in a [ProfileSummaryCard]'s bottom row. */
@Composable
fun ProfileMetaItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    iconBg: Color = AcademyGreen.copy(alpha = 0.14f),
    iconTint: Color = AcademyGreen
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier.size(26.dp).clip(RoundedCornerShape(8.dp)).background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(13.dp))
        }
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Text(
                value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * The top "profile" card on a details screen: an icon/initials avatar with a small status-dot
 * badge, a title + colored tag row, a divider, then two [ProfileMetaItem]s split by a vertical rule.
 */
@Composable
fun ProfileSummaryCard(
    avatar: @Composable () -> Unit,
    title: String,
    tagText: String,
    tagColor: Color,
    metaItems: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                avatar()
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.size(7.dp).clip(CircleShape).background(tagColor))
                        Text(tagText, color = tagColor, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(Modifier.fillMaxWidth(), content = metaItems)
        }
    }
}

/** The small circular avatar with a status-colored checkmark badge, used inside [ProfileSummaryCard]. */
@Composable
fun CheckBadgedAvatar(initialsOrIcon: @Composable () -> Unit, badgeColor: Color) {
    Box {
        Box(
            modifier = Modifier.size(68.dp).clip(CircleShape).background(Color(0xFFDCF3E1)),
            contentAlignment = Alignment.Center
        ) {
            initialsOrIcon()
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(20.dp)
                .clip(CircleShape)
                .background(badgeColor)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ComponentsPreview() {
    SnehasAcademyTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PrimaryButton("Primary Button", {})
            SecondaryButton("Secondary Button", {})
            ModernTextField("", {}, "Email")
            PasswordField("", {})
            AcademyLogo(Modifier.height(90.dp).fillMaxWidth())
            DashboardCard("Students", "Manage student profiles", {})
            InfoCard("Courses", "03")
            ProfileCard("Sneha", "Founder and mentor")
            SearchBar("", {})
            EmptyState("Nothing here yet", "Future data will appear here.")
        }
    }
}
