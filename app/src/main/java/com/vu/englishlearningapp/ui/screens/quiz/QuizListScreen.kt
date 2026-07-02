package com.vu.englishlearningapp.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vu.englishlearningapp.data.remote.dto.quiz.CollectionTestDto
import com.vu.englishlearningapp.ui.components.AppTopNavigationBar

private val QuizScreenBackground = Color(0xFFFAF8F5)
private val QuizCardBackground = Color.White
private val QuizAccent = Color(0xFF4968A8)
private val QuizSecondaryText = Color(0xFF697386)

@Composable
fun QuizListScreen(
    viewModel: QuizListViewModel,
    onTestClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = QuizScreenBackground,
        topBar = {
            AppTopNavigationBar(
                title = "Quizzes",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter quizzes")
                    }
                    IconButton(onClick = viewModel::loadTests) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> QuizMessageContainer(innerPadding) {
                CircularProgressIndicator(color = QuizAccent)
            }

            uiState.errorMessage != null -> QuizMessageContainer(innerPadding) {
                Text(
                    text = uiState.errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = viewModel::loadTests) { Text("Retry") }
            }

            uiState.tests.isEmpty() -> QuizMessageContainer(innerPadding) {
                Text(
                    text = "No quizzes available",
                    color = QuizSecondaryText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            else -> QuizList(
                tests = uiState.tests,
                innerPadding = innerPadding,
                onTestClick = onTestClick
            )
        }
    }

    if (showFilterDialog) {
        QuizFilterDialog(
            selectedFilter = uiState.selectedFilter,
            onFilterSelected = {
                viewModel.selectFilter(it)
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
private fun QuizList(
    tests: List<CollectionTestDto>,
    innerPadding: PaddingValues,
    onTestClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "Available quizzes",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${tests.size} quizzes ready for practice",
                    color = QuizSecondaryText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        items(tests, key = { it.id }) { test ->
            QuizCard(test = test, onClick = { onTestClick(test.id) })
        }
    }
}

@Composable
private fun QuizCard(test: CollectionTestDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = QuizCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(QuizAccent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Quiz,
                    contentDescription = null,
                    tint = QuizAccent,
                    modifier = Modifier.size(25.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    text = test.testName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = test.description?.takeIf { it.isNotBlank() }
                        ?: "Practice and review your English skills",
                    color = QuizSecondaryText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = test.collection?.collectionName ?: "General quiz",
                    color = QuizAccent,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(9.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    QuizMetadata(Icons.Default.Quiz, "${test.totalQuestions} questions")
                    QuizMetadata(Icons.Default.AccessTime, "${test.duration} min")
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = QuizSecondaryText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun QuizMetadata(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = QuizSecondaryText, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = QuizSecondaryText, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun QuizFilterDialog(
    selectedFilter: QuizFilter,
    onFilterSelected: (QuizFilter) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter quizzes") },
        text = {
            Column {
                QuizFilter.entries.forEach { filter ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFilterSelected(filter) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedFilter == filter,
                            onClick = { onFilterSelected(filter) }
                        )
                        Text(filter.displayName)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
private fun QuizMessageContainer(
    innerPadding: PaddingValues,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        content()
    }
}

private val QuizFilter.displayName: String
    get() = when (this) {
        QuizFilter.NEWEST -> "Recently updated"
        QuizFilter.SHORTEST_DURATION -> "Shortest duration"
        QuizFilter.FEWEST_QUESTIONS -> "Fewest questions"
        QuizFilter.NAME_ASCENDING -> "Name A-Z"
    }
