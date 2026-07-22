package com.shena.snehasacademy.presentation.fees

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.shena.snehasacademy.core.components.EmptyState
import com.shena.snehasacademy.core.components.HeaderIconBadge
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.SearchBar
import com.shena.snehasacademy.core.components.StatusBadge
import com.shena.snehasacademy.core.components.SubtitledHeader
import com.shena.snehasacademy.core.theme.AcademyGreen
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.Student
import com.shena.snehasacademy.presentation.enrollments.EnrollmentViewModel
import java.text.SimpleDateFormat
import java.util.Locale

private data class FeeRow(
    val studentId: String,
    val enrollmentId: String,
    val studentName: String,
    val courseName: String,
    val fees: Int,
    val amountPaid: Int,
    val enrollmentDate: String
) {
    val due: Int get() = (fees - amountPaid).coerceAtLeast(0)
}

private val FeeDateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

private fun feeRowsFrom(students: List<Student>): List<FeeRow> =
    students.flatMap { student ->
        student.enrollments.map { enrollment ->
            FeeRow(student.id, enrollment.id, student.name, enrollment.courseName, enrollment.fees, enrollment.amountPaid, enrollment.enrollmentDate)
        }
    }.sortedByDescending { row ->
        runCatching { FeeDateFormatter.parse(row.enrollmentDate)?.time }.getOrNull() ?: 0L
    }

private val FeeFilterOptions = listOf("All", "Paid", "Due")

private val FeeAvatarPalette = listOf(
    Color(0xFF2E8B57) to Color(0xFFDCF3E1),
    Color(0xFF2E6FD9) to Color(0xFFDCEAFB),
    Color(0xFF6C4FC1) to Color(0xFFEAE4FA),
    Color(0xFFD97706) to Color(0xFFFCEBD9)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeManagementScreen(
    viewModel: EnrollmentViewModel? = null,
    onOpenHistory: (String, String) -> Unit = { _, _ -> },
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("All") }
    var showFilterMenu by remember { mutableStateOf(false) }

    val students = viewModel?.students ?: MockData.students
    val rows = remember(students) { feeRowsFrom(students) }
    val totalCollected = remember(rows) { rows.sumOf { it.amountPaid } }
    val totalDue = remember(rows) { rows.sumOf { it.due } }

    val statusFiltered = remember(statusFilter, rows) {
        when (statusFilter) {
            "Paid" -> rows.filter { it.due <= 0 }
            "Due" -> rows.filter { it.due > 0 }
            else -> rows
        }
    }
    val filtered = remember(query, statusFiltered) {
        if (query.isBlank()) {
            statusFiltered
        } else {
            statusFiltered.filter {
                it.studentId.contains(query, ignoreCase = true) || it.studentName.contains(query, ignoreCase = true)
            }
        }
    }

    LifecycleResumeEffect(viewModel) {
        viewModel?.refresh()
        onPauseOrDispose { }
    }

    var isPullRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(viewModel?.isLoading) {
        if (viewModel?.isLoading == false) isPullRefreshing = false
    }

    Scaffold(
        topBar = {
            SubtitledHeader(title = "Fee Management", subtitle = "Track fees collected and pending", onBack = onBack) {
                Box {
                    HeaderIconBadge(
                        icon = Icons.Rounded.FilterAlt,
                        tint = AcademyGreen,
                        background = Color.White,
                        contentDescription = "Filter payments",
                        onClick = { showFilterMenu = true }
                    )
                    DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                        FeeFilterOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { statusFilter = option; showFilterMenu = false },
                                trailingIcon = {
                                    if (option == statusFilter) {
                                        Icon(Icons.Rounded.Check, contentDescription = null, tint = AcademyGreen)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isPullRefreshing,
            onRefresh = {
                isPullRefreshing = true
                viewModel?.refresh()
            },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp)
            ) {
                item { SearchBar(query, { query = it }, placeholder = "Search by student ID or name...") }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        FeeStatCard(Color(0xFFDCF3E1), AcademyGreen, "₹$totalCollected", "Collected", Modifier.weight(1f))
                        FeeStatCard(Color(0xFFFCEBD9), Color(0xFFD97706), "₹$totalDue", "Pending", Modifier.weight(1f))
                    }
                }
                if (viewModel?.isLoading == true && rows.isEmpty()) {
                    item { LoadingView() }
                } else if (filtered.isEmpty()) {
                    item {
                        EmptyState(
                            "No payment records found",
                            "Try a different student ID or name.",
                            icon = {
                                Icon(
                                    Icons.Rounded.SearchOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        )
                    }
                } else {
                    items(filtered) { row ->
                        FeeRowCard(row, Modifier.padding(top = 2.dp), onClick = { onOpenHistory(row.studentId, row.enrollmentId) })
                    }
                    item {
                        Text(
                            "Showing ${filtered.size} of ${rows.size} enrollments",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeeStatCard(iconBg: Color, accent: Color, value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = iconBg.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Box(
                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Payments, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            }
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FeeRowCard(row: FeeRow, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val (accent, bg) = remember(row.studentId) {
        FeeAvatarPalette[(row.studentId.hashCode() and Int.MAX_VALUE) % FeeAvatarPalette.size]
    }
    val initials = remember(row.studentName) {
        row.studentName.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) }.uppercase()
    }

    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(bg),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, color = accent, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Column(
                modifier = Modifier.weight(1f).padding(start = 12.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(row.studentName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(row.studentId, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        Icons.AutoMirrored.Rounded.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        row.courseName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        Icons.Rounded.Payments,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        "₹${row.amountPaid}/₹${row.fees}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(if (row.due <= 0) "Paid" else "Due")
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 2.dp).size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeeManagementPreview() {
    SnehasAcademyTheme { FeeManagementScreen {} }
}
