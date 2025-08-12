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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCodeScreen(
    foodItems: List<FoodItem>,
    onBack: () -> Unit
) {
    var selectedFoodItem by remember { mutableStateOf<FoodItem?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoadingButton by remember { mutableStateOf(false) }

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
                apiKey = "AIzaSyDshuj5OTDcBv8QTV6VFoo9C_3McyQvKs8"
            )
        } catch (e: Exception) {
            errorMessage = "API configuration error: ${e.message}"
            null
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(foodItems, key = { it.foodCode }) { item ->
                val isSelected = (selectedFoodItem?.foodCode == item.foodCode)
                Card(
                    onClick = {
                        if (isSelected) {
                            selectedFoodItem = null
                        } else if (!isLoadingButton) {
                            selectedFoodItem = item
                            isLoadingButton = true
                            coroutineScope.launch {
                                try {
                                    val newPrompt = """
                                        음식 이름: ${item.foodName}
                                        
                                        ${csvContent2}
                                        위에 제공된 데이터를 바탕으로, 음식의 이름을 **반드시** **항목에 있는 내용 그대로**만 분류해서 출력해줘.
                                    """.trimIndent()
                                    val geminiResponse = generativeModel?.generateContent(newPrompt)
                                    item.geminiResponse = geminiResponse?.text

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
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(8.dp))
                            if (isLoadingButton) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Text("Loading details...")
                            } else {
                                Text(item.geminiResponse ?: "No details found.")
                            }
                        }
                    }
                }
            }
        }
        // "Go Back" button at the top
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Go Back")
        }
    }
}