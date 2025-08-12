package com.example.foodchatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodchatbot.ui.theme.FoodChatBotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodChatBotTheme {
                val navController = rememberNavController()
                // Shared state between screens
                var foodItemsState by remember { mutableStateOf(emptyList<FoodItem>()) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "chat_screen") {
                        composable("chat_screen") {
                            GeminiChatScreen(
                                onNavigateToFoodCodeScreen = { foodItems ->
                                    foodItemsState = foodItems
                                    navController.navigate("food_code_screen")
                                }
                            )
                        }
                        composable("food_code_screen") {
                            FoodCodeScreen(
                                foodItems = foodItemsState,
                                onBack = {
                                    navController.popBackStack()
                                    // Clear the state when going back to the chat screen
                                    foodItemsState = emptyList()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}