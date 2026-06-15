package com.example.lab08.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lab08.ui.components.AppButton
import com.example.lab08.ui.viewmodel.StoreViewModel

private val EMOJI_SUGGESTIONS = listOf("🍕", "🎮", "📚", "🎵", "🌿", "🚗", "💄", "🏋️", "🐾", "✈️")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddCategoryScreen(
    viewModel: StoreViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var iconError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Category", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = false },
                label = { Text("Category Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                supportingText = if (nameError) ({ Text("Name is required") }) else null
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = icon,
                onValueChange = { icon = it; iconError = false },
                label = { Text("Emoji Icon *") },
                modifier = Modifier.fillMaxWidth(),
                isError = iconError,
                supportingText = if (iconError) ({ Text("Pick or type an emoji") }) else null
            )
            Spacer(Modifier.height(8.dp))
            Text("Suggestions:", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                EMOJI_SUGGESTIONS.forEach { emoji ->
                    FilterChip(
                        selected = icon == emoji,
                        onClick = { icon = emoji; iconError = false },
                        label = { Text(emoji, style = MaterialTheme.typography.titleMedium) }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            AppButton(
                text = "Save Category",
                onClick = {
                    nameError = name.isBlank()
                    iconError = icon.isBlank()
                    if (!nameError && !iconError) {
                        viewModel.addCategory(name.trim(), icon.trim())
                        onBack()
                    }
                }
            )
        }
    }
}
