package com.shena.snehasacademy.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shena.snehasacademy.core.components.ListItemCard
import com.shena.snehasacademy.core.components.ProfileCard
import com.shena.snehasacademy.core.components.ScreenScaffold
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    ScreenScaffold("Settings", true, onBack) { contentModifier ->
        Column(contentModifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileCard("Sneha's Mehendi & Art Academy", "Profile and preferences")
            ListItemCard("Academy Profile", "Name, contact, and address placeholders", "")
            ListItemCard("Theme", "Light and dark mode supported by system setting", "")
            ListItemCard("Future Firebase", "Authentication and Firestore hooks can be added here", "")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    SnehasAcademyTheme { SettingsScreen {} }
}
