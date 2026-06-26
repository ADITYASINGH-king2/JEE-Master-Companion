package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.viewmodel.BackupViewModel
import com.example.ui.viewmodel.MigrationState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ProfileScreen - Custom configuration settings and secure preferences editor.
 * Securely persists updates inside hardware-backed SharedPreferences.
 * Integrates native Storage Access Framework (SAF) for zero-memory-leak JSON migration.
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    backupViewModel: BackupViewModel = viewModel(),
    onNavigateToSetup: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Observe global reactive state values from the ViewModel
    val currentUserName by backupViewModel.userName.collectAsState()
    val currentStudyMotto by backupViewModel.studyMotto.collectAsState()
    val currentTargetYear by backupViewModel.targetYear.collectAsState()
    val currentAvatarIndex by backupViewModel.avatarIndex.collectAsState()
    val isLoading by backupViewModel.isLoading.collectAsState()

    var userName by remember { mutableStateOf("") }
    var studyMotto by remember { mutableStateOf("") }
    var targetYear by remember { mutableStateOf("2027") }

    var saveConfirmationMessage by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val migrationState by backupViewModel.migrationState.collectAsState()

    // SAF ActivityResult Launchers for Backup Export & Import
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { backupViewModel.exportBackup(context, it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { backupViewModel.importBackup(context, it) }
    }

    // Load initial values from view model on start
    LaunchedEffect(Unit) {
        backupViewModel.loadProfile(context)
    }

    // Keep editable local states synchronized whenever ViewModel states shift (e.g. after import)
    LaunchedEffect(currentUserName, currentStudyMotto, currentTargetYear) {
        userName = currentUserName
        studyMotto = currentStudyMotto
        targetYear = currentTargetYear.toString()
    }

    // Observe Event Flow for snackbar notifications
    LaunchedEffect(Unit) {
        backupViewModel.eventFlow.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar selection header
                item {
                    val avatarIcon = getAvatarIconByIndex(currentAvatarIndex)
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = avatarIcon,
                            contentDescription = "User Avatar Placeholder",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(70.dp)
                        )
                    }
                }

                // Profile input fields card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Secure Profile Settings",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            OutlinedTextField(
                                value = userName,
                                onValueChange = { userName = it },
                                label = { Text("Profile Display Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = studyMotto,
                                onValueChange = { studyMotto = it },
                                label = { Text("My Study Motto") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = targetYear,
                                onValueChange = { targetYear = it },
                                label = { Text("Target JEE Exam Year") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            AvatarSelectionGrid(
                                selectedAvatarId = getAvatarIdByIndex(currentAvatarIndex),
                                onAvatarSelected = { avatarId ->
                                    val index = getAvatarIndexById(avatarId)
                                    backupViewModel.saveAvatarIndex(context, index)
                                }
                            )

                            if (saveConfirmationMessage.isNotEmpty()) {
                                Text(
                                    text = saveConfirmationMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            // Save Button with non-blocking Main thread execution checks
                            Button(
                                onClick = {
                                    scope.launch {
                                        backupViewModel.saveProfile(
                                            context = context,
                                            name = userName,
                                            motto = studyMotto,
                                            year = targetYear.toIntOrNull() ?: 2027
                                        )

                                        saveConfirmationMessage = "Settings saved securely to device hardware!"
                                        kotlinx.coroutines.delay(2000)
                                        saveConfirmationMessage = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Save Action")
                                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                Text(
                                    text = "Save Profile Securely",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedButton(
                                onClick = { onNavigateToSetup() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("open_profile_setup_wizard_button"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Setup Wizard Icon",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Open Profile Setup Wizard",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // SAF Local Data Migration Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Storage & Local Migration",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Export database scores and secure credentials as a recovery file or restore an existing backup offline instantly.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )

                            // Migration Progress / State UI (Inline)
                            when (migrationState) {
                                is MigrationState.Processing -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Processing streaming backup data...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                is MigrationState.Success -> {
                                    Text(
                                        text = (migrationState as MigrationState.Success).message,
                                        color = MaterialTheme.colorScheme.secondary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                is MigrationState.Error -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Error icon",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            text = (migrationState as MigrationState.Error).error,
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                is MigrationState.Idle -> {
                                    // Default state - show nothing extra
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Export Button (SAF CreateDocument)
                                Button(
                                    onClick = {
                                        backupViewModel.resetState()
                                        exportLauncher.launch("JEEMaster_Backup.json")
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Export backup",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Export", fontWeight = FontWeight.Bold)
                                }

                                // Import Button (SAF OpenDocument)
                                OutlinedButton(
                                    onClick = {
                                        backupViewModel.resetState()
                                        importLauncher.launch(arrayOf("application/json"))
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Import", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Informational security card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Security Status",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "All profiles and analytical database states are stored offline directly on your local device with AES-256 system hardware level encryption.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // High-priority full-screen blur loading indicator if in active processing
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp
                            )
                            Text(
                                text = "Synchronizing Local Storage...",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
