package com.vu.englishlearningapp.ui.screens.quiz

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vu.englishlearningapp.ui.components.AppTopNavigationBar

/**
 * Screen for taking a quiz — one question at a time.
 * Shows multiple-choice options with radio buttons.
 * "Finish Quiz" on the last question triggers local scoring.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTakingScreen(
    viewModel: QuizTakingViewModel,
    onQuizFinished: () -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate to result screen when quiz is finished
    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished) {
            onQuizFinished()
        }
    }

    Scaffold(
        topBar = {
            AppTopNavigationBar(
                title = uiState.testName.ifEmpty { "Quiz" },
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        when {
            // Loading state
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // Error state
            uiState.errorMessage != null && uiState.questions.isEmpty() -> {
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
                        Button(onClick = onBackClick) {
                            Text("Back to quizzes")
                        }
                    }
                }
            }
            // Empty state
            uiState.questions.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No questions in this quiz",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBackClick) {
                            Text("Back to quizzes")
                        }
                    }
                }
            }
            // Content — show current question
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    // Progress indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.progress,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = uiState.formattedRemainingTime,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.remainingSeconds <= 60) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = {
                            (uiState.currentIndex + 1).toFloat() / uiState.questions.size
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Scrollable question content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        uiState.currentQuestion?.let { question ->
                            if (question.typeKeyword == "multiple_choice") {
                                MultipleChoiceQuestionCard(
                                    question = question,
                                    questionNumber = uiState.currentIndex + 1,
                                    answer = uiState.currentSelectedAnswer,
                                    enabled = !uiState.isSavingAnswer && !uiState.isSubmitting,
                                    onAnswerChanged = viewModel::updateAnswer
                                )
                            } else {
                                Text(
                                    text = question.questionText,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                QuizAnswerInput(
                                    question = question,
                                    answer = uiState.currentSelectedAnswer,
                                    enabled = !uiState.isSavingAnswer && !uiState.isSubmitting,
                                    onAnswerChanged = viewModel::updateAnswer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val actionError = uiState.answerErrorMessage ?: uiState.errorMessage
                    if (actionError != null) {
                        Text(
                            text = actionError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = viewModel::previousQuestion,
                            enabled = !uiState.isFirstQuestion && !uiState.isSavingAnswer && !uiState.isSubmitting,
                            modifier = Modifier.weight(1f).height(52.dp)
                        ) {
                            Text("‹  Previous")
                        }
                        Button(
                            onClick = viewModel::saveAnswerAndContinue,
                            modifier = Modifier.weight(1f).height(52.dp),
                            enabled = uiState.hasCurrentAnswer &&
                                !uiState.isSavingAnswer &&
                                !uiState.isSubmitting &&
                                uiState.remainingSeconds > 0
                        ) {
                            Text(
                                text = when {
                                    uiState.isSavingAnswer -> "Saving..."
                                    uiState.isSubmitting -> "Submitting..."
                                    uiState.isLastQuestion -> "Finish"
                                    else -> "Next  ›"
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally)
                    ) {
                        uiState.questions.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == uiState.currentIndex) 10.dp else 7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == uiState.currentIndex) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outlineVariant
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
