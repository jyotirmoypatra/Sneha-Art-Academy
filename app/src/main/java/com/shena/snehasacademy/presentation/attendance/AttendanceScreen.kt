package com.shena.snehasacademy.presentation.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shena.snehasacademy.core.components.InfoCard
import com.shena.snehasacademy.core.components.ListItemCard
import com.shena.snehasacademy.core.components.ScreenScaffold
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme

@Composable
fun AttendanceScreen(onBack: () -> Unit) {
    ScreenScaffold("Attendance", true, onBack) { contentModifier ->
        Column(contentModifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoCard("Today", "12 Present")
            ListItemCard("Bridal Mehendi Batch", "Mock attendance for today", "92%")
            ListItemCard("Arabic Pattern Batch", "Mock attendance for today", "88%")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AttendancePreview() {
    SnehasAcademyTheme { AttendanceScreen {} }
}
