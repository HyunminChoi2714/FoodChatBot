package com.example.foodchatbot

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

data class DynamicInputInfo(
    val label: String = "",
    val value: String = "",
    val label2: String = "",
    val value2: String = ""
)

private fun getDynamicInputForFoodItems(foodItems: List<FoodItem>): DynamicInputInfo? {
    val foodDetail = foodItems.mapNotNull { it.geminiResponse }

    return when {
        foodDetail.any { it.contains("낱알류") } ->
            DynamicInputInfo(
                label = "사용한 곡물의 양을 mL 단위로 입력하세요.",
                value = "",
                label2 = "곡물을 불렸는지, 끓였는지의 여부를 입력하세요.",
                value2 = ""
            )

        // 면 종류 선택에 따른 수정 필요
        foodDetail.any { it.contains("면류 및 밀가공품") } ->
            DynamicInputInfo(
                label = "사용한 면이 생면인지, 삶은 면인지, 마른 면인지, 인스턴트 면인지 고르시오.",
                value = "",
                label2 = "면의 중량을 확인하여 그램 단위로 기록하세요.",
                value2 = ""
            )

        foodDetail.any { it.contains("빵, 떡") } ->
            DynamicInputInfo(
                label = "빵이나 떡의 가로, 세로, 높이를 cm 단위로 입력하세요.",
                value = ""
            )

        foodDetail.any { it.contains("시리얼류") } ->
            DynamicInputInfo(
                label = "시리얼의 부피를 mL 단위로 입력하세요.",
                value = ""
            )

        foodDetail.any { it.contains("묵, 두부") } ->
            DynamicInputInfo(
                label = "묵이나 두부의 가로, 세로, 높이를 cm 단위로 입력하세요.",
                value = ""
            )

        foodDetail.any { it.contains("옥수수") } ->
            DynamicInputInfo(
                label = "옥수수 알갱이들의 부피를 mL 단위로 입력하세요.",
                value = ""
            )

        foodDetail.any { it.contains("만두, 바람떡") } ->
            DynamicInputInfo(
                label = "만두나 바람떡을 반원기둥으로 생각해서 반지름과 높이를 각각 cm 단위로 입력하세요.",
                value = ""
            )

        foodDetail.any { it.contains("양배추, 양상추") } ->
            DynamicInputInfo(
                label = "양배추나 양상추를 구로 취급했을 때 그 반지름을 cm 단위로 입력하세요.",
                value = ""
            )

        foodDetail.any { it.contains("통마늘") } ->
            DynamicInputInfo(
                label = "통마늘의 갯수를 입력하세요.",
                value = "",
                label2 = "통마늘을 원기둥으로 취급하여 그 반지름과 높이를 cm 단위로 입력하세요.",
                value2 = ""
            )

        foodDetail.any { it.contains("편마늘") } ->
            DynamicInputInfo(
                label = "편마늘이 총 몇 조각인지 갯수를 입력하세요.",
                value = "",
                label2 = "각 편마늘의 밑바닥 면적과 두께를 각각 cm^2과 cm 단위로 입력하세요.",
                value2 = ""
            )

        foodDetail.any { it.contains("생강, 마늘") } ->
            DynamicInputInfo(
                label = "다진 마늘을 넣은 양을 그램 단위로 입력하세요.",
                value = ""
            )

        foodDetail.any { it.contains("상추, 깻잎") } ->
            DynamicInputInfo(
                label = "낱장으로 ",
                value = ""
            )

        foodDetail.any { it.contains("구형") } ->
            DynamicInputInfo(
                label = "",
                value = "",
                label2 = "",
                value2 = ""
            )

        foodDetail.any { it.contains("쑥갓") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("삶은 것") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("김치류") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("양송이버섯") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("느타리버섯") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("그 외 버섯류") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("마른 김, 마른 다시마") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("파래, 미역줄기, 매생이") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("땅콩, 아몬드, 캐슈넛") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("밤") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("호두") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("믹스넛") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("구형 과일") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("단감, 연시, 대봉, 참외, 키위") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("바나나") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("딸기, 대추") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("슬라이스 햄") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("그 외 육류") } ->
            DynamicInputInfo(
                label = "사용한 육류의 양을 그램 단위로 입력하세요.",
                value = ""
            )

        foodDetail.any { it.contains("일반 어류") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("멸치") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("미꾸라지") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("게") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("새우") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("조개 종류") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("가리비") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("바지락, 홍합, 미더덕, 멍게") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("조개관자") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("밋조개") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("오징어") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("마른 오징어") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("낙지") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("문어") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("쥐포") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("채 형태와 자건품") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("명란젓") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("그 외의 젓갈류") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("어묵") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("난류") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("조미료류") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        foodDetail.any { it.contains("티백") } ->
            DynamicInputInfo(
                label = "",
                value = ""
            )

        else -> null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCodeScreen(
    foodItems: List<FoodItem>,
    takenPhoto: Bitmap?,
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
        if (takenPhoto != null) {
            Image(
                bitmap = takenPhoto.asImageBitmap(),
                contentDescription = "Taken photo of food",
                modifier = Modifier.fillMaxWidth().height(200.dp).padding(bottom = 16.dp)
            )
        }
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
                                        
                                        $csvContent2
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
                                if (dynamicInputInfo!!.label2 != "") {
                                    OutlinedTextField(
                                        value = dynamicInputInfo!!.value2,
                                        onValueChange = { newValue ->
                                            dynamicInputInfo = dynamicInputInfo!!.copy(value2 = newValue)
                                        },
                                        label = { Text(dynamicInputInfo!!.label2) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
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