package com.vu.englishlearningapp.ui.screens.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ToggleOn
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Profile screen displaying user information.
 * Shows avatar placeholder, name, email, phone, birthday, address,
 * status, super admin flag, permissions, and action buttons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onEditClick: () -> Unit,
    onLoggedOut: () -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate to login when logged out
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLoggedOut()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        when {
            // Loading state (initial load)
            uiState.isLoading && uiState.user == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // Error state with no data
            uiState.errorMessage != null && uiState.user == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadProfile() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            // Content with pull-to-refresh
            else -> {
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { viewModel.refreshProfile() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        uiState.user?.let { user ->
                            // --- Avatar Placeholder ---
                            // TODO: Replace with actual avatar image when avatar upload is implemented
                            Card(
                                modifier = Modifier.size(100.dp),
                                shape = MaterialTheme.shapes.extraLarge,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Avatar",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Name
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            // Email
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // --- Info Card ---
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    ProfileInfoRow(
                                        icon = Icons.Default.Email,
                                        label = "Email",
                                        value = user.email
                                    )

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                    ProfileInfoRow(
                                        icon = Icons.Default.Phone,
                                        label = "Phone",
                                        value = user.phone ?: "Not set"
                                    )

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                    ProfileInfoRow(
                                        icon = Icons.Default.Cake,
                                        label = "Birthday",
                                        value = formatBirthday(user.birthday)
                                    )

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                    ProfileInfoRow(
                                        icon = Icons.Default.Home,
                                        label = "Address",
                                        value = user.address ?: "Not set"
                                    )

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                    ProfileInfoRow(
                                        icon = Icons.Default.ToggleOn,
                                        label = "Status",
                                        value = if (user.status == 1) "Active" else "Inactive"
                                    )

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                    ProfileInfoRow(
                                        icon = Icons.Default.AdminPanelSettings,
                                        label = "Super Admin",
                                        value = if (user.isSuperAdmin) "Yes" else "No"
                                    )

                                    // Permissions section
                                    if (user.permissions.isNotEmpty()) {
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                        ProfileInfoRow(
                                            icon = Icons.Default.Security,
                                            label = "Permissions",
                                            value = user.permissions.joinToString(", ")
                                        )
                                    } else {
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                        ProfileInfoRow(
                                            icon = Icons.Default.Security,
                                            label = "Permissions",
                                            value = "None"
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // --- Action Buttons ---

                            // Edit Profile button
                            Button(
                                onClick = onEditClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Edit Profile", style = MaterialTheme.typography.labelLarge)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Logout button
                            Button(
                                onClick = { viewModel.logout() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ),
                                enabled = !uiState.isLoading
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Logout", style = MaterialTheme.typography.labelLarge)
                            }
                        }

                        // Error message while data is shown (e.g., refresh error)
                        uiState.errorMessage?.let { error ->
                            if (uiState.user != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * A row showing an icon, label, and value for profile info.
 */
@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Format the birthday string for display.
 * Extracts just the date part from ISO strings like "1989-12-31T17:00:00.000000Z".
 */
private fun formatBirthday(birthday: String?): String {
    if (birthday.isNullOrEmpty()) return "Not set"
    // If it contains 'T', extract just the date part
    return if (birthday.contains("T")) {
        birthday.substringBefore("T")
    } else {
        birthday
    }
}
