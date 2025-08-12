package com.example.foodchatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodchatbot.ui.theme.FoodChatBotTheme
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import org.json.JSONArray
import org.json.JSONObject

data class FoodItem(
    val foodName: String,
    val foodCode: String,
    val geminiResponse: String? = null
)

data class FoodInputState(
    val name: String = "",
    val ingredients: String = "",
    val method: String = "",
    val sauces: String = ""
)

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
    var inputState by remember { mutableStateOf(FoodInputState()) }
    var responseText by remember { mutableStateOf("Gemini's response will appear here.") }
    var isLoading by remember { mutableStateOf(false) }
    var foodItemsState by remember { mutableStateOf(emptyList<FoodItem>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var selectedFoodItem by remember { mutableStateOf<FoodItem?>(null) }
    var expandedFoodCodes by remember { mutableStateOf(setOf<String>()) }
    var isLoadingButton by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Read the CSV file from assets
    var csvContent by remember { mutableStateOf("") }
    var csvContent2 by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        try {
            context.assets.open("foodcode.csv").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                    csvContent = reader.readText()
                }
            }
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
                apiKey = "AIzaSyDshuj5OTDcBv8QTV6VFoo9C_3McyQvKs8" // Store this in local.properties
            )
        } catch (e: Exception) {
            errorMessage = "API configuration error: ${e.message}"
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Food Name Conversion",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Input fields
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = inputState.name,
                onValueChange = { inputState = inputState.copy(name = it) },
                label = { Text("Enter your food title here...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = inputState.ingredients,
                onValueChange = { inputState = inputState.copy(ingredients = it) },
                label = { Text("Enter the main/sub ingredients") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = inputState.method,
                onValueChange = { inputState = inputState.copy(method = it) },
                label = { Text("Enter the cooking method") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = inputState.sauces,
                onValueChange = { inputState = inputState.copy(sauces = it) },
                label = { Text("Enter seasonings/dressings/sauces") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Error message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Submit button
        Button(
            onClick = {
                if (inputState.name.isBlank()) {
                    errorMessage = "Please enter a food name"
                    return@Button
                }

                if (generativeModel == null) {
                    errorMessage = "API not configured properly"
                    return@Button
                }

                isLoading = true
                errorMessage = null
                responseText = "Generating response..."

                coroutineScope.launch {
                    try {
                        val fullPrompt = """
                            음식 이름: ${inputState.name}
                            주재료/부재료: ${inputState.ingredients}
                            조리법: ${inputState.method}
                            양념장/소스: ${inputState.sauces}
                            
                            이 음식 정보를 바탕으로, 이 식품에 들어가 있는 재료들의 농진청식품코드를 모두 다 적어주세요.
                            응답은 **반드시** 다음과 같은 JSON 형식의 배열로만 제공해야 합니다. 다른 텍스트, 설명, 또는 마크다운 형식(예: ```json)을 **절대** 포함하지 마세요.
                            
                            예시:
                            [
                                {
                                    "food_name": "고춧가루",
                                    "food_code": "R0070000005a"
                                },
                                {
                                    "food_name": "소금",
                                    "food_code": "R0200000009a"
                                }
                            ]
                            
                            다음은 참고용으로 제공된 식품 코드 데이터입니다:
                            $csvContent
                        """.trimIndent()

                        val response = generativeModel.generateContent(fullPrompt)
                        val rawText = response.text ?: "[]"
                        val cleanedJsonText = rawText.trim()

                        val jsonArray = JSONArray(cleanedJsonText)
                        val foodItems = mutableListOf<FoodItem>()
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            foodItems.add(
                                FoodItem(
                                    foodName = jsonObject.getString("food_name"),
                                    foodCode = jsonObject.getString("food_code")
                                )
                            )
                        }
                        foodItemsState = foodItems
                        responseText = "Found ${foodItems.size} food codes"
                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.message}"
                        e.printStackTrace()
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Convert Food Title", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 16.dp)
        ) {
            when {
                foodItemsState.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(foodItemsState, key = { it.foodCode })  { item ->
                            val isExpanded = expandedFoodCodes.contains(item.foodCode)
                            val isSelected = (selectedFoodItem?.foodCode == item.foodCode)
                            Card(
                                onClick = {
                                    if (isExpanded) {
                                        expandedFoodCodes = expandedFoodCodes - item.foodCode
                                    }
                                    else {
                                        expandedFoodCodes = expandedFoodCodes + item.foodCode

                                        if (item.geminiResponse == null) {
                                            val detailPrompt = """
                                                음식 이름: ${item.foodName}
                                                
                                                ${csvContent2}
                                                위에 제공된 데이터를 바탕으로, 음식의 이름을 **반드시** **항목에 있는 내용 그대로** 기술해줘.
                                            """.trimIndent()
                                            isLoadingButton = true
                                            coroutineScope.launch {
                                                try {
                                                    val geminiResponse = generativeModel?.generateContent(detailPrompt)
                                                    val updatedList = foodItemsState.map {
                                                        if (it.foodCode == item.foodCode) {
                                                            it.copy(geminiResponse = geminiResponse?.text)
                                                        }
                                                        else {
                                                            it
                                                        }
                                                    }
                                                    foodItemsState = updatedList
                                                }
                                                catch (e: Exception) {
                                                    errorMessage = "Error fetching details"
                                                }
                                                finally {
                                                    isLoadingButton = false
                                                }
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
                                            Text("Loading details....")
                                        }
                                        else {
                                            Text(item.geminiResponse ?: "No details found.")
                                        }
                                    }
                                    if (isExpanded) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(item.geminiResponse ?: "Loading details...")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGeminiChatScreen() {
    FoodChatBotTheme {
        GeminiChatScreen()
    }
}