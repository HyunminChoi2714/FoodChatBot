package com.example.foodchatbot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

data class DynamicInputInfo(
    val instruction: String = "",
    val label: String = "",
    val value: String = ""
)

private fun getDynamicInputForFoodItems(foodItems: List<FoodItem>): DynamicInputInfo? {
    val foodDetail = foodItems.mapNotNull { it.geminiResponse }

    return when {
        foodDetail.any { it.contains("육류") } ->
            DynamicInputInfo(
                instruction = "grams",
                label = "사용한 육류의 양을 그램 단위로 입력하세요.",
                value = ""
            )

        foodDetail.any { it.contains("곡물") } ->
            DynamicInputInfo(
                instruction = "volume",
                label = "사용한 곡물의 양을 밀리리터 단위로 입력하세요.",
                value = ""
            )

        else -> null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCodeScreen(
    foodItems: List<FoodItem>,
    onBack: () -> Unit
) {
    var foodItemsState by remember { mutableStateOf(foodItems) }
    var selectedFoodItem by remember { mutableStateOf<FoodItem?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoadingButton by remember { mutableStateOf(false) }

    var dynamicInputForCode by remember { mutableStateOf<String?>(null) }
    var dynamicInputInfo by remember { mutableStateOf<DynamicInputInfo?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Read the CSV file once
    var csvContent2 by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        try {
            context.assets.open("foodclassification.csv").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                    csvContent2 = reader.readText()
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error reading foodcode.csv: ${e.message}"
            e.printStackTrace()
        }
    }

    val generativeModel = remember {
        try {
            GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = "AIzaSyAeSHrnFdT2nJtFhAAup0PWT6h-BCo4Y94"
            )
        } catch (e: Exception) {
            errorMessage = "API configuration error: ${e.message}"
            null
        }
    }

    LaunchedEffect(Unit) {
        val requiredInput = getDynamicInputForFoodItems(foodItemsState)
        if (requiredInput != null) {
            dynamicInputInfo = requiredInput
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Go Back")
        }

        // Display the list of food code buttons
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(foodItemsState, key = { it.foodCode }) { item ->
                val isSelected = (selectedFoodItem?.foodCode == item.foodCode)

                // Determine if this item should show the dynamic input form
                val showDynamicInput = dynamicInputForCode == item.foodCode && dynamicInputInfo != null

                Card(
                    onClick = {
                        if (isSelected && !showDynamicInput) {
                            selectedFoodItem = null
                            dynamicInputForCode = null
                            dynamicInputInfo = null
                        } else if (!isLoadingButton) {
                            selectedFoodItem = item
                            isLoadingButton = true

                            // Make the API call to get the category detail
                            coroutineScope.launch {
                                try {
                                    val newPrompt = """
                                        음식 이름: ${item.foodName}
                                        
                                        ${csvContent2}
                                        위에 제공된 데이터를 바탕으로, 음식의 이름을 **반드시** **항목에 있는 내용 그대로**만 분류해서 출력해줘.
                                    """.trimIndent()
                                    val geminiResponse = generativeModel?.generateContent(newPrompt)

                                    // Update the item's geminiResponse
                                    val updatedList = foodItemsState.map {
                                        if (it.foodCode == item.foodCode) {
                                            it.copy(geminiResponse = geminiResponse?.text)
                                        } else {
                                            it
                                        }
                                    }
                                    foodItemsState = updatedList

                                    // Check if this item requires dynamic input and set state accordingly
                                    val requiredInput = getDynamicInputForFoodItems(listOf(updatedList.find { it.foodCode == item.foodCode }!!))
                                    if (requiredInput != null) {
                                        dynamicInputForCode = item.foodCode
                                        dynamicInputInfo = requiredInput
                                    }

                                } catch (e: Exception) {
                                    errorMessage = "Error fetching details: ${e.message}"
                                } finally {
                                    isLoadingButton = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(item.foodName, fontWeight = FontWeight.Bold)
                        Text(item.foodCode, color = Color.Gray)

                        // Conditional display based on state
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(8.dp))
                            if (isLoadingButton) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Text("Loading details...")
                            } else if (showDynamicInput) {
                                // Show the dynamic input form
                                Text(item.geminiResponse ?: "No details found.", color = Color.Gray)
                                OutlinedTextField(
                                    value = dynamicInputInfo!!.value,
                                    onValueChange = { newValue ->
                                        dynamicInputInfo = dynamicInputInfo!!.copy(value = newValue)
                                    },
                                    label = { Text(dynamicInputInfo!!.label) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Button(
                                    onClick = {
                                        // Handle the submission of dynamic input
                                        // e.g., Make a second API call, then reset dynamicInputForCode to null
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                ) {
                                    Text("Submit")
                                }
                            } else {
                                // Show the Gemini response text
                                Text(item.geminiResponse ?: "No details found.")
                            }
                        }
                    }
                }
            }
        }
    }
}