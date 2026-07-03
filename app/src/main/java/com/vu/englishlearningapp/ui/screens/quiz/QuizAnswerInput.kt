package com.vu.englishlearningapp.ui.screens.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.vu.englishlearningapp.data.remote.dto.quiz.QuestionDto

@Composable
fun QuizAnswerInput(
    question: QuestionDto,
    answer: String?,
    enabled: Boolean,
    onAnswerChanged: (String) -> Unit
) {
    when (question.typeKeyword) {
        "multiple_choice" -> MultipleChoiceAnswer(question, answer, enabled, onAnswerChanged)
        "true_false" -> TrueFalseAnswer(answer, enabled, onAnswerChanged)
        "fill_in_blank" -> FillBlankAnswer(answer, enabled, onAnswerChanged)
        "matching" -> MatchingAnswer(question, answer, enabled, onAnswerChanged)
        else -> Text(
            "This question type is not supported.",
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun MultipleChoiceAnswer(
    question: QuestionDto,
    answer: String?,
    enabled: Boolean,
    onAnswerChanged: (String) -> Unit
) {
    val options = question.getOptions()
    val selectedIndex = answer?.toIntOrNull()?.takeIf { it in options.indices }
        ?: options.indexOf(answer).takeIf { it >= 0 }

    options.forEachIndexed { index, option ->
        MultipleChoiceOptionCard(
            label = ('A'.code + index).toChar().toString(),
            text = option,
            selected = selectedIndex == index,
            enabled = enabled,
            onClick = { onAnswerChanged(index.toString()) }
        )
    }
}

@Composable
fun MultipleChoiceQuestionCard(
    question: QuestionDto,
    questionNumber: Int,
    answer: String?,
    enabled: Boolean,
    onAnswerChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "Question $questionNumber",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                )
            }
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp, bottom = 16.dp)
            )
            MultipleChoiceAnswer(question, answer, enabled, onAnswerChanged)
        }
    }
}

@Composable
private fun MultipleChoiceOptionCard(
    label: String,
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 1.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                        else MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                    .wrapContentSize(Alignment.Center)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun TrueFalseAnswer(
    answer: String?,
    enabled: Boolean,
    onAnswerChanged: (String) -> Unit
) {
    AnswerChoiceCard(
        text = "True",
        selected = answer.equals("true", ignoreCase = true) || answer == "1",
        enabled = enabled,
        onClick = { onAnswerChanged("true") }
    )
    AnswerChoiceCard(
        text = "False",
        selected = answer.equals("false", ignoreCase = true) || answer == "0",
        enabled = enabled,
        onClick = { onAnswerChanged("false") }
    )
}

@Composable
private fun FillBlankAnswer(
    answer: String?,
    enabled: Boolean,
    onAnswerChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = answer.orEmpty(),
        onValueChange = onAnswerChanged,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Your Answer") },
        singleLine = true,
        supportingText = { Text("Answers are checked without case sensitivity.") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchingAnswer(
    question: QuestionDto,
    answer: String?,
    enabled: Boolean,
    onAnswerChanged: (String) -> Unit
) {
    val pairs = question.getMatchingPairs()
    val selections = remember(question.id) {
        mutableStateMapOf<String, String>().apply { putAll(parseMatchingAnswer(answer)) }
    }
    var expandedIndex by remember { mutableIntStateOf(-1) }
    val rightOptions = pairs.map { it.right }.distinct()

    Column {
        Text(
            "Select the matching value for each item.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        pairs.forEachIndexed { index, pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pair.left,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(12.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedIndex == index,
                    onExpandedChange = {
                        if (enabled) expandedIndex = if (expandedIndex == index) -1 else index
                    },
                    modifier = Modifier.weight(1.3f)
                ) {
                    OutlinedTextField(
                        value = selections[pair.left].orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        enabled = enabled,
                        label = { Text("Match") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedIndex == index)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedIndex == index,
                        onDismissRequest = { expandedIndex = -1 }
                    ) {
                        rightOptions
                            .filter { right -> selections[pair.left] == right || right !in selections.values }
                            .forEach { right ->
                            DropdownMenuItem(
                                text = { Text(right) },
                                onClick = {
                                    selections[pair.left] = right
                                    expandedIndex = -1
                                    if (pairs.all { selections[it.left].isNullOrBlank().not() }) {
                                        onAnswerChanged(buildMatchingAnswer(pairs.map { it.left }, selections))
                                    } else {
                                        onAnswerChanged("")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnswerChoiceCard(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        onClick = onClick,
        enabled = enabled,
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected, onClick = onClick, enabled = enabled)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

private fun parseMatchingAnswer(answer: String?): Map<String, String> {
    if (answer.isNullOrBlank()) return emptyMap()
    return runCatching {
        JsonParser.parseString(answer).asJsonArray.associate { element ->
            val pair = element.asJsonObject
            pair.get("left").asString to pair.get("right").asString
        }
    }.getOrDefault(emptyMap())
}

private fun buildMatchingAnswer(
    leftItems: List<String>,
    selections: Map<String, String>
): String = JsonArray().apply {
    leftItems.forEach { left ->
        add(JsonObject().apply {
            addProperty("left", left)
            addProperty("right", selections[left].orEmpty())
        })
    }
}.toString()
