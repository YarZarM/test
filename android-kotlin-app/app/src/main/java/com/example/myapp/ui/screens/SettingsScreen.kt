package com.example.myapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapp.ui.viewmodel.UserViewModel
import com.example.myapp.ui.viewmodel.UserViewModelFactory

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(LocalContext.current)
    )
) {
    var nameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.savedUserName) {
        if (nameText.isEmpty()) {
            nameText = uiState.savedUserName
        }
    }

    LaunchedEffect(uiState.savedUserEmail) {
        if (emailText.isEmpty()) {
            emailText = uiState.savedUserEmail
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "DataStore Preferences",
                style = MaterialTheme.typography.titleLarge
            )
            
            OutlinedTextField(
                value = nameText,
                onValueChange = { nameText = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = emailText,
                onValueChange = { emailText = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Button(
                onClick = {
                    viewModel.saveUserData(nameText, emailText)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Saved Data",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Name: ${uiState.savedUserName.ifEmpty { "Not set" }}")
                    Text("Email: ${uiState.savedUserEmail.ifEmpty { "Not set" }}")
                }
            }
        }
    }
}

