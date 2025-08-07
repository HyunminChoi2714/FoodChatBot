package com.example.foodchatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodchatbot.ui.theme.FoodChatBotTheme
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodChatBotTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GeminiChatScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiChatScreen() {
    var prompt by remember { mutableStateOf("") }
    var mainIngredients by remember { mutableStateOf("") }
    var cookingMethod by remember { mutableStateOf("") }
    var sauces by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("Gemini's response will appear here.") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // State to hold the content of the CSV file
    var csvContent by remember { mutableStateOf("") }

    // Read the CSV file from assets when the composable is first created
    LaunchedEffect(Unit) {
        try {
            context.assets.open("foodcode.csv").use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
                csvContent = reader.readText()
            }
        } catch (e: Exception) {
            // Handle file not found or other exceptions
            csvContent = "Error reading foodcode.csv: ${e.message}"
            e.printStackTrace()
        }
    }

    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = "AIzaSyDshuj5OTDcBv8QTV6VFoo9C_3McyQvKs8"
        )
    }

    // ... (rest of your UI code remains the same) ...
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // ... (UI elements like TextFields and Spacer) ...
        Text(
            text = "Food Name Conversion",
            fontSize = 28.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        )

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Enter your food title here...") },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = mainIngredients,
            onValueChange = { mainIngredients = it },
            label = { Text("Enter the main / sub ingredients of the food.")},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = cookingMethod,
            onValueChange = { cookingMethod = it},
            label = { Text("Enter the cooking method of the food.") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = sauces,
            onValueChange = { sauces = it },
            label = { Text("Enter whether seasonings, dressings, or sauces are added.") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (prompt.isNotBlank() && !isLoading) {
                    isLoading = true // Set loading state to true
                    responseText = "Generating response..." // Show loading message
                    coroutineScope.launch {
                        try {
                            val fullPrompt = """
                                음식 이름: $prompt
                                주재료/부재료: $mainIngredients
                                조리법: $cookingMethod
                                양념장/소스: $sauces
                                
                                이 음식 정보를 바탕으로, 이 식품에 들어가 있는 재료들의 농진청식품코드를 모두 다 적어주세요.
                                응답은 반드시 다음과 같은 JSON 형식으로 제공해야 합니다.
                                
                                예시:
                                {
                                    {
                                        "food_name": "고춧가루",
                                        "food_code": "R0070000005a"
                                    },
                                    {
                                        "food_name": "소금",
                                        "food_code": "R0200000009a"
                                    }
                                }
                                
                                다음은 참고용으로 제공된 식품 코드 데이터입니다. 이 식품 코드 데이터에 있는 내용만 적어주세요:
                                $csvContent
                            """.trimIndent()
                            val response = generativeModel.generateContent(fullPrompt)
                            responseText = response.text ?: "No response generated or an error occurred."
                        } catch (e: Exception) {
                            responseText = "Error: ${e.message}"
                            e.printStackTrace()
                        } finally {
                            isLoading = false // Reset loading state
                        }
                    }
                } else if (prompt.isBlank()) {
                    responseText = "Please enter a prompt."
                }
            },
            enabled = !isLoading, // Disable button while loading
            modifier = Modifier
                .fillMaxWidth(0.6f) // Make button take 60% of width
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            }
            else {
                Text("Convert Food Title", fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Take up remaining space
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState()) // Make response scrollable
            ) {
                Text(
                    text = responseText,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}