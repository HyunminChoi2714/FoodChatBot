package com.example.foodchatbot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCodeScreen(
    foodItems: List<FoodItem>,
    onBack: () -> Unit
) {
    var responseText by remember { mutableStateOf("Click a button to see its details.") }
    var expandedFoodCodes by remember { mutableStateOf(setOf<String>()) }
    var isLoadingButton by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Go Back")
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(foodItems, key = { it.foodCode }) { item ->
            val isExpanded = expandedFoodCodes.contains(item.foodCode)

            Card(
                onClick = {
                    if (isExpanded) {
                        expandedFoodCodes = expandedFoodCodes - item.foodCode
                    }
                    else {
                        expandedFoodCodes = expandedFoodCodes + item.foodCode
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(item.foodName, fontWeight = FontWeight.Bold)
                    Text(item.foodCode, color = Color.Gray)
                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(responseText)
                    }
                }
            }
        }
    }
}