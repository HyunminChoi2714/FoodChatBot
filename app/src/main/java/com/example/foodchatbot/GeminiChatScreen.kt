package com.example.foodchatbot

import android.graphics.Bitmap
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.ActivityNavigatorExtras
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

data class FoodItem(
    val foodName: String,
    val foodCode: String,
    var geminiResponse: String? = null
)

data class FoodInputState(
    val name: String = "",
    val ingredients: String = "",
    val method: String = "",
    val sauces: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiChatScreen(
    onNavigateToFoodCodeScreen: (List<FoodItem>) -> Unit,
    onPhotoTaken: (Bitmap?) -> Unit
) {
    var inputState by remember { mutableStateOf(FoodInputState()) }
    var responseText by remember { mutableStateOf("Gemini's response will appear here.") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var dynamicInputInfo by remember { mutableStateOf<DynamicInputInfo?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var csvContent by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        try {
            context.assets.open("foodcode.csv").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                    csvContent = reader.readText()
                }
            }
        }
        catch (e: Exception) {
            errorMessage = "Error reading foodcode.csv: ${e.message}"
            e.printStackTrace()
        }
    }

    val generativeModel = remember {
        try {
            GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = "AIzaSyAWb-u5X8FyEuj3_jYA7tmpKiphVaUH0Us"
            )
        }
        catch (e: Exception) {
            errorMessage = "API configuration error: ${e.message}"
            null
        }
    }

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { photoBitmap ->
            onPhotoTaken(photoBitmap)
            if (photoBitmap != null) {
                if (inputState.name.isBlank()) {
                    errorMessage = "Please enter a food name"
                    isLoading = false
                    return@rememberLauncherForActivityResult
                }
                if (generativeModel == null) {
                    errorMessage = "API not configured properly"
                    isLoading = false
                    return@rememberLauncherForActivityResult
                }

                coroutineScope.launch {
                    try {
                        val fullPrompt = """
                            음식 이름: ${inputState.name}
                            주재료/부재료: ${inputState.ingredients}
                            조리법: ${inputState.method}
                            양념장/소스: ${inputState.sauces}
                            
                            이 음식 정보를 바탕으로, 이 식품에 들어가 있는 재료들의 농진청식품코드를 모두 다 적어주세요.
                            응답은 반드시 다음과 같은 JSON 형식의 배열로만 제공해야 합니다. 다른 텍스트, 설명, 또는 마크다운 형식(예: ```json)을 절대 포함하지 마세요.
                            
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
                        onNavigateToFoodCodeScreen(foodItems)
                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.message}"
                        e.printStackTrace()
                    } finally {
                        isLoading = false
                    }
                }
            } else {
                isLoading = false
            }
        }
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                takePhotoLauncher.launch(null)
            }
            else {
                errorMessage = "Camera permission is required to take a photo."
                isLoading = false
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Food Name Conversion",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
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
                label = { Text("Enter the cooking method (ex. 볶는다, 끓인다.)") },
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

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Button(
            onClick = {
                isLoading = true
                val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    takePhotoLauncher.launch(null)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp),
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
    }
}