package com.shena.snehasacademy.presentation.fees

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shena.snehasacademy.core.components.DateField
import com.shena.snehasacademy.core.components.InfoCard
import com.shena.snehasacademy.core.components.ListItemCard
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.ModernTextField
import com.shena.snehasacademy.core.components.PrimaryButton
import com.shena.snehasacademy.core.components.ScreenScaffold
import com.shena.snehasacademy.core.components.SecondaryButton
import com.shena.snehasacademy.core.components.SectionCard
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.Payment
import com.shena.snehasacademy.domain.model.Student
import com.shena.snehasacademy.presentation.enrollments.EnrollmentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PaymentDateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

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
        ScreenScaffold("Payment History", true, onBack) { contentModifier ->
            Box(contentModifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingView() }
        }
        return
    }

    val student = loadedStudent ?: MockData.students.find { it.id == studentId } ?: MockData.students.first()
    val enrollment = student.enrollments.find { it.id == enrollmentId } ?: student.enrollments.first()

    var showAddPayment by remember { mutableStateOf(false) }

    val payments = enrollment.payments
    val totalPaid = enrollment.amountPaid
    val due = (enrollment.fees - totalPaid).coerceAtLeast(0)

    fun applyNewPayment(payment: Payment) {
        loadedStudent = student.copy(
            enrollments = student.enrollments.map {
                if (it.id == enrollment.id) it.copy(payments = it.payments + payment) else it
            }
        )
    }

    ScreenScaffold("Payment History", true, onBack) { contentModifier ->
        Column(
            modifier = contentModifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    student.name,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    enrollment.courseName,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                InfoCard("Fees", "₹${enrollment.fees}", Modifier.weight(1f), valueStyle = MaterialTheme.typography.titleMedium)
                InfoCard("Paid", "₹$totalPaid", Modifier.weight(1f), valueStyle = MaterialTheme.typography.titleMedium)
                InfoCard("Due", "₹$due", Modifier.weight(1f), valueStyle = MaterialTheme.typography.titleMedium)
            }

            SectionCard("Payment History") {
                if (payments.isEmpty()) {
                    Text(
                        "No payments recorded yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    payments.forEach { payment ->
                        ListItemCard(payment.date, "Payment received", "₹${payment.amount}")
                    }
                }
            }

            PrimaryButton("Add Payment", { showAddPayment = true }, Modifier.padding(bottom = 24.dp))
        }
    }

    if (showAddPayment) {
        AddPaymentSheet(
            maxAmount = due,
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
private fun AddPaymentSheet(maxAmount: Int, onDismiss: () -> Unit, onAdd: (amount: Int, date: String) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var amountText by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
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
            ModernTextField(
                amountText,
                { amountText = it; errorMessage = null },
                "Amount",
                keyboardType = KeyboardType.Number
            )
            errorMessage?.let { message ->
                Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            DateField(
                label = "Payment Date",
                dateText = dateMillis?.let { PaymentDateFormatter.format(Date(it)) }.orEmpty(),
                onClick = { showDatePicker = true }
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryButton("Cancel", onDismiss, Modifier.weight(1f))
                PrimaryButton(
                    "Save",
                    {
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
                    Modifier.weight(2f)
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

@Preview(showBackground = true)
@Composable
private fun PaymentHistoryPreview() {
    SnehasAcademyTheme { PaymentHistoryScreen(studentId = "STU-1001", enrollmentId = "ENR-0001", onBack = {}) }
}
