package com.shena.snehasacademy.presentation.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shena.snehasacademy.R
import com.shena.snehasacademy.core.components.AppToolbar
import com.shena.snehasacademy.core.components.ModernTextField
import com.shena.snehasacademy.core.components.PasswordField
import com.shena.snehasacademy.core.constants.AppConstants
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme

private val Cream = Color(0xFFFFF7E8)
private val SoftCream = Color(0xFFFFFCF5)
private val WarmBrown = Color(0xFF4B260C)
private val Cocoa = Color(0xFF7A4A24)
private val AntiqueGold = Color(0xFFD6A34A)
private val HennaBrown = Color(0xFF8B3F12)
private val DeepOlive = Color(0xFF3F4A20)

@Composable
fun LoginSelectionScreen(onAdminLogin: () -> Unit, onStudentLogin: () -> Unit) {
    Scaffold(containerColor = Cream) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Cream, SoftCream, Color(0xFFF1DFC2))))
        ) {
            MehendiBackdrop(Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_badge),
                    contentDescription = "Sneha's Mehendi & Art Academy logo",
                    modifier = Modifier.size(226.dp).padding(bottom = 28.dp),
                    contentScale = ContentScale.Fit
                )
                LoginRoleCard(
                    title = "Login as Admin",
                    subtitle = "Academy management portal",
                    admin = true,
                    onClick = onAdminLogin
                )
                Spacer(Modifier.height(16.dp))
                LoginRoleCard(
                    title = "Login as Student",
                    subtitle = "Student learning portal",
                    admin = false,
                    onClick = onStudentLogin
                )
                Spacer(Modifier.height(28.dp))
                Text(
                    text = AppConstants.Tagline,
                    color = Cocoa.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun LoginRoleCard(title: String, subtitle: String, admin: Boolean, onClick: () -> Unit) {
    val cardBrush = if (admin) {
        Brush.horizontalGradient(listOf(WarmBrown, HennaBrown))
    } else {
        Brush.horizontalGradient(listOf(DeepOlive, Cocoa))
    }
    val roleText = if (admin) "ADMIN" else "STUDENT"

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .border(1.dp, AntiqueGold.copy(alpha = 0.55f), RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(cardBrush)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(SoftCream.copy(alpha = 0.96f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = roleText.take(1),
                    color = WarmBrown,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    color = SoftCream,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = subtitle,
                    color = SoftCream.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MehendiBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val line = Cocoa.copy(alpha = 0.12f)
        drawCircle(
            color = AntiqueGold.copy(alpha = 0.12f),
            radius = size.width * 0.42f,
            center = Offset(size.width * 0.02f, size.height * 0.12f)
        )
        drawCircle(
            color = HennaBrown.copy(alpha = 0.08f),
            radius = size.width * 0.36f,
            center = Offset(size.width * 0.98f, size.height * 0.88f)
        )
        repeat(7) { index ->
            val y = size.height * (0.14f + index * 0.095f)
            drawLine(
                color = line,
                start = Offset(size.width * 0.08f, y),
                end = Offset(size.width * 0.25f, y + size.width * 0.08f),
                strokeWidth = 2.2f,
                cap = StrokeCap.Round
            )
        }
        repeat(5) { index ->
            drawCircle(
                color = AntiqueGold.copy(alpha = 0.22f),
                radius = 4.dp.toPx(),
                center = Offset(size.width * (0.72f + index * 0.045f), size.height * 0.18f)
            )
        }
    }
}

@Composable
fun AdminLoginScreen(viewModel: AdminLoginViewModel? = null, onLogin: () -> Unit, onRegister: () -> Unit, onBack: () -> Unit) {
    AuthFormScaffold(
        title = "Admin Login",
        subtitle = "Manage students, courses, attendance, fees, and academy reports.",
        onBack = onBack
    ) {
        val context = LocalContext.current
        val prefs = remember { context.getSharedPreferences("admin_login_prefs", Context.MODE_PRIVATE) }
        var email by remember { mutableStateOf(prefs.getString("email", "").orEmpty()) }
        var password by remember { mutableStateOf("") }
        var rememberMe by remember { mutableStateOf(prefs.getBoolean("remember_me", false)) }
        val isLoading = viewModel?.isLoading == true
        ModernTextField(email, { email = it }, "Email", keyboardType = KeyboardType.Email)
        PasswordField(password, { password = it })
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
            Text("Remember me", style = MaterialTheme.typography.bodySmall)
        }
        viewModel?.errorMessage?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        AuthPrimaryButton(
            text = if (isLoading) "Logging in..." else "Login",
            enabled = !isLoading,
            onClick = {
                viewModel?.login(email, password) {
                    if (rememberMe) {
                        prefs.edit().putBoolean("remember_me", true).putString("email", email).apply()
                    } else {
                        prefs.edit().clear().apply()
                    }
                    onLogin()
                }
            }
        )
        var showForgotPassword by remember { mutableStateOf(false) }
        TextButton(
            onClick = {
                viewModel?.clearResetState()
                showForgotPassword = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Forgot Password", color = HennaBrown, fontWeight = FontWeight.SemiBold)
        }

        if (showForgotPassword) {
            ForgotPasswordDialog(
                initialEmail = email,
                isSending = viewModel?.isSendingReset == true,
                isSent = viewModel?.resetEmailSent == true,
                errorMessage = viewModel?.resetError,
                onSend = { resetEmail -> viewModel?.sendPasswordReset(resetEmail) },
                onDismiss = {
                    showForgotPassword = false
                    viewModel?.clearResetState()
                }
            )
        }
    }
}

@Composable
private fun ForgotPasswordDialog(
    initialEmail: String,
    isSending: Boolean,
    isSent: Boolean,
    errorMessage: String?,
    onSend: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var resetEmail by remember { mutableStateOf(initialEmail) }
    AlertDialog(
        onDismissRequest = { if (!isSending) onDismiss() },
        title = { Text("Reset Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (isSent) {
                    Text("A password reset link has been sent to $resetEmail. Please check your inbox.")
                } else {
                    Text("Enter your admin email address and we'll send you a link to reset your password.")
                    ModernTextField(resetEmail, { resetEmail = it }, "Email", keyboardType = KeyboardType.Email)
                    errorMessage?.let { message ->
                        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            if (isSent) {
                TextButton(onClick = onDismiss) { Text("Done") }
            } else {
                TextButton(enabled = !isSending, onClick = { onSend(resetEmail) }) {
                    Text(if (isSending) "Sending..." else "Send Link")
                }
            }
        },
        dismissButton = {
            if (!isSent) {
                TextButton(enabled = !isSending, onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}

@Composable
fun AdminRegisterScreen(viewModel: AdminRegisterViewModel? = null, onCreate: () -> Unit, onBack: () -> Unit) {
    AuthFormScaffold(
        title = "Register Admin",
        subtitle = "Create an admin account for the academy portal.",
        onBack = onBack
    ) {
        var name by remember { mutableStateOf("") }
        var mobile by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirm by remember { mutableStateOf("") }
        val isLoading = viewModel?.isLoading == true
        ModernTextField(name, { name = it }, "Full Name")
        ModernTextField(mobile, { mobile = it }, "Mobile Number", keyboardType = KeyboardType.Phone)
        ModernTextField(email, { email = it }, "Email", keyboardType = KeyboardType.Email)
        PasswordField(password, { password = it })
        PasswordField(confirm, { confirm = it }, "Confirm Password")
        viewModel?.errorMessage?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        AuthPrimaryButton(
            text = if (isLoading) "Creating..." else "Create Admin",
            enabled = !isLoading,
            onClick = { viewModel?.register(name, mobile, email, password, confirm, onCreate) }
        )
    }
}

@Composable
fun StudentLoginScreen(viewModel: StudentLoginViewModel? = null, onLogin: (String) -> Unit, onBack: () -> Unit) {
    AuthFormScaffold(
        title = "Student Login",
        subtitle = "View your courses, attendance, fee status, certificates, and updates.",
        onBack = onBack
    ) {
        val context = LocalContext.current
        val prefs = remember { context.getSharedPreferences("student_login_prefs", Context.MODE_PRIVATE) }
        var studentId by remember { mutableStateOf(prefs.getString("student_id", "").orEmpty()) }
        var mobile by remember { mutableStateOf(prefs.getString("mobile", "").orEmpty()) }
        var rememberMe by remember { mutableStateOf(prefs.getBoolean("remember_me", false)) }
        val isLoading = viewModel?.isLoading == true
        ModernTextField(studentId, { studentId = it }, "Student ID")
        ModernTextField(mobile, { mobile = it }, "Mobile Number", keyboardType = KeyboardType.Phone)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
            Text("Remember me", style = MaterialTheme.typography.bodySmall)
        }
        viewModel?.errorMessage?.let { message ->
            Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        AuthPrimaryButton(
            text = if (isLoading) "Please wait..." else "Continue",
            enabled = !isLoading,
            onClick = {
                viewModel?.login(studentId, mobile) { loggedInStudentId ->
                    if (rememberMe) {
                        prefs.edit()
                            .putBoolean("remember_me", true)
                            .putString("student_id", studentId)
                            .putString("mobile", mobile)
                            .apply()
                    } else {
                        prefs.edit().clear().apply()
                    }
                    onLogin(loggedInStudentId)
                }
            }
        )
    }
}

@Composable
private fun AuthFormScaffold(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    showNavbarTitle: Boolean = false,
    fields: @Composable ColumnScopeHack.() -> Unit
) {
    Scaffold(
        containerColor = Cream,
        topBar = { AppToolbar(if (showNavbarTitle) title else "", canNavigateBack = true, onBack = onBack) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Cream, SoftCream, Color(0xFFF1DFC2))))
        ) {
            MehendiBackdrop(Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_badge),
                    contentDescription = "Sneha's Mehendi & Art Academy logo",
                    modifier = Modifier.size(132.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = title,
                    color = WarmBrown,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = subtitle,
                    color = Cocoa.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftCream.copy(alpha = 0.96f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ColumnScopeHack.fields()
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthPrimaryButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = WarmBrown, contentColor = SoftCream)
    ) {
        Text(text, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun AuthSecondaryButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = HennaBrown)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

private object ColumnScopeHack

@Preview(showBackground = true)
@Composable
private fun LoginSelectionPreview() {
    SnehasAcademyTheme { LoginSelectionScreen({}, {}) }
}

@Preview(showBackground = true)
@Composable
private fun AdminLoginPreview() {
    SnehasAcademyTheme { AdminLoginScreen(onLogin = {}, onRegister = {}, onBack = {}) }
}

@Preview(showBackground = true)
@Composable
private fun AdminRegisterPreview() {
    SnehasAcademyTheme { AdminRegisterScreen(onCreate = {}, onBack = {}) }
}

@Preview(showBackground = true)
@Composable
private fun StudentLoginPreview() {
    SnehasAcademyTheme { StudentLoginScreen(onLogin = {}, onBack = {}) }
}
