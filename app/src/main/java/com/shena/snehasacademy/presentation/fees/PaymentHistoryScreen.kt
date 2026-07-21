package com.shena.snehasacademy.presentation.fees

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shena.snehasacademy.core.components.CheckBadgedAvatar
import com.shena.snehasacademy.core.components.DetailInfoRow
import com.shena.snehasacademy.core.components.DetailSectionContainer
import com.shena.snehasacademy.core.components.DetailSectionHeaderRow
import com.shena.snehasacademy.core.components.FormFieldIconBox
import com.shena.snehasacademy.core.components.FormIconField
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.PrimaryButton
import com.shena.snehasacademy.core.components.ProfileMetaItem
import com.shena.snehasacademy.core.components.ProfileSummaryCard
import com.shena.snehasacademy.core.components.SecondaryButton
import com.shena.snehasacademy.core.components.SubtitledHeader
import com.shena.snehasacademy.core.components.academyTextFieldColors
import com.shena.snehasacademy.core.components.statusColor
import com.shena.snehasacademy.core.theme.AcademyGreen
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.Payment
import com.shena.snehasacademy.domain.model.Student
import com.shena.snehasacademy.presentation.enrollments.EnrollmentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PaymentDateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
private val PaymentInfoAccent = Color(0xFF6C4FC1)
private val PaymentInfoBg = Color(0xFFEAE4FA)

@Composable
fun PaymentHistoryScreen(
    studentId: String,
    enrollmentId: String,
    viewModel: EnrollmentViewModel? = null,
    onBack: () -> Unit
) {
    var loadedStudent by remember(studentId) { mutableStateOf<Student?>(null) }
    var isLoadingStudent by remember(studentId) { mutableStateOf(viewModel != null) }

    LaunchedEffect(studentId, viewModel) {
        if (viewModel != null) {
            isLoadingStudent = true
            loadedStudent = viewModel.getStudent(studentId)
            isLoadingStudent = false
        }
    }

    if (viewModel != null && isLoadingStudent && loadedStudent == null) {
        Scaffold(
            topBar = { SubtitledHeader(title = "Payment History", subtitle = "View and manage fee payments", onBack = onBack) }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { LoadingView() }
        }
        return
    }

    val student = loadedStudent ?: MockData.students.find { it.id == studentId } ?: MockData.students.first()
    val enrollment = student.enrollments.find { it.id == enrollmentId } ?: student.enrollments.first()

    var showAddPayment by remember { mutableStateOf(false) }

    val payments = enrollment.payments
    val totalPaid = enrollment.amountPaid
    val due = (enrollment.fees - totalPaid).coerceAtLeast(0)

    val initials = remember(student.name) {
        student.name.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) }.uppercase()
    }

    fun applyNewPayment(payment: Payment) {
        loadedStudent = student.copy(
            enrollments = student.enrollments.map {
                if (it.id == enrollment.id) it.copy(payments = it.payments + payment) else it
            }
        )
    }

    Scaffold(
        topBar = { SubtitledHeader(title = "Payment History", subtitle = "View and manage fee payments", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileSummaryCard(
                avatar = {
                    CheckBadgedAvatar(
                        initialsOrIcon = {
                            Text(initials, color = AcademyGreen, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                        },
                        badgeColor = statusColor(if (due <= 0) "Paid" else "Due")
                    )
                },
                title = student.name,
                tagText = enrollment.courseName,
                tagColor = statusColor(if (due <= 0) "Paid" else "Due")
            ) {
                ProfileMetaItem(
                    Icons.Rounded.Payments, "Fees", "₹${enrollment.fees}", Modifier.weight(1f),
                    iconBg = PaymentInfoBg, iconTint = PaymentInfoAccent
                )
                VerticalDivider(modifier = Modifier.height(34.dp), color = MaterialTheme.colorScheme.outlineVariant)
                ProfileMetaItem(
                    Icons.Rounded.Payments, "Due", "₹$due", Modifier.weight(1f).padding(start = 10.dp),
                    iconBg = PaymentInfoBg, iconTint = PaymentInfoAccent
                )
            }

            DetailSectionContainer {
                DetailSectionHeaderRow(Icons.Rounded.Payments, PaymentInfoBg, PaymentInfoAccent, "Payment History")
                if (payments.isEmpty()) {
                    Text(
                        "No payments recorded yet.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    payments.forEachIndexed { index, payment ->
                        DetailInfoRow(
                            Icons.Rounded.CalendarMonth, payment.date, "₹${payment.amount}",
                            iconBg = PaymentInfoBg, iconTint = PaymentInfoAccent,
                            showDivider = index != payments.lastIndex
                        )
                    }
                }
            }

            PrimaryButton("Add Payment", { showAddPayment = true }, Modifier.padding(bottom = 24.dp), height = 40.dp)
        }
    }

    if (showAddPayment) {
        AddPaymentSheet(
            maxAmount = due,
            isSaving = viewModel?.isSavingPayment == true,
            onDismiss = { showAddPayment = false },
            onAdd = { amount, date ->
                val payment = Payment(id = "PAY-${payments.size + 1}-${enrollment.id}", amount = amount, date = date)
                if (viewModel != null) {
                    viewModel.addPayment(studentId, enrollment.id, payment) {
                        applyNewPayment(payment)
                        showAddPayment = false
                    }
                } else {
                    applyNewPayment(payment)
                    showAddPayment = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPaymentSheet(
    maxAmount: Int,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onAdd: (amount: Int, date: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var amountText by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = { if (!isSaving) onDismiss() }, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Add Payment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "Remaining due: ₹$maxAmount",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FormIconField(
                Icons.Rounded.Payments, PaymentInfoBg, PaymentInfoAccent, amountText,
                { amountText = it; errorMessage = null }, "Amount",
                keyboardType = KeyboardType.Number
            )
            errorMessage?.let { message ->
                Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            PaymentDateField(
                dateText = dateMillis?.let { PaymentDateFormatter.format(Date(it)) }.orEmpty(),
                onClick = { showDatePicker = true }
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryButton("Cancel", onDismiss, Modifier.weight(1f), height = 40.dp)
                PrimaryButton(
                    "Save",
                    {
                        if (isSaving) return@PrimaryButton
                        val amount = amountText.toIntOrNull() ?: 0
                        when {
                            amount <= 0 -> errorMessage = "Enter a valid amount."
                            amount > maxAmount -> errorMessage = "Amount cannot exceed the remaining due of ₹$maxAmount."
                            else -> {
                                val date = dateMillis?.let { PaymentDateFormatter.format(Date(it)) } ?: PaymentDateFormatter.format(Date())
                                onAdd(amount, date)
                            }
                        }
                    },
                    Modifier.weight(1f),
                    enabled = !isSaving,
                    isLoading = isSaving,
                    height = 40.dp
                )
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun PaymentDateField(dateText: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = dateText,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Payment Date", style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            leadingIcon = { FormFieldIconBox(Icons.Rounded.CalendarMonth, PaymentInfoBg, PaymentInfoAccent) },
            trailingIcon = { Text("Select", style = MaterialTheme.typography.labelSmall, color = PaymentInfoAccent) },
            shape = RoundedCornerShape(14.dp),
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

@Preview(showBackground = true)
@Composable
private fun PaymentHistoryPreview() {
    SnehasAcademyTheme { PaymentHistoryScreen(studentId = "STU-1001", enrollmentId = "ENR-0001", onBack = {}) }
}
