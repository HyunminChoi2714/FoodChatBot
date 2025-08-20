package com.example.foodchatbot

import android.graphics.Bitmap
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
class MainActivity : ComponentActivity() { // MainActivity 클래스를 정의합니다. ComponentActivity는 안드로이드 앱의 기본 진입점입니다. (Defines the MainActivity class. ComponentActivity is the base entry point for an Android app.)
    override fun onCreate(savedInstanceState: Bundle?) {     // 앱이 처음 생성될 때 호출되는 onCreate 메서드를 오버라이드합니다. (Overrides the onCreate method, which is called when the app is first created.)
        // 부모 클래스의 onCreate를 호출하여 기본적인 설정을 완료합니다. (Calls the superclass's onCreate to complete basic setup.)
        super.onCreate(savedInstanceState) // 앱이 화면 전체를 사용하도록 설정합니다. (상태 표시줄 및 탐색 모음 포함) (Configures the app to use the full screen (including status and navigation bars).)
        enableEdgeToEdge() // Compose UI를 이 액티비티의 콘텐츠 뷰로 설정합니다. (Sets the Compose UI as the content view for this activity.)
        setContent {
            FoodChatBotTheme { // FoodChatBotTheme 테마를 적용합니다. (Applies the FoodChatBotTheme theme.)
                val navController = rememberNavController() // Jetpack Compose 네비게이션을 위한 컨트롤러를 생성하고 기억합니다. (Creates and remembers a navigation controller for Jetpack Compose navigation.)
                var foodItemsState by remember { mutableStateOf(emptyList<FoodItem>()) } // 화면 간에 공유할 FoodItem 목록 상태를 생성하고 초기화합니다. (Creates and initializes a state for a list of FoodItems to be shared between screens.)
                var takenPhoto by remember { mutableStateOf<Bitmap?>(null) } // 화면 간에 공유할 비트맵 사진 상태를 생성하고 초기화합니다. (Creates and initializes a state for a Bitmap photo to be shared between screens.)

                Surface( // 앱의 배경을 정의하는 Material3 Surface 컴포넌트입니다. (This is a Material3 Surface component that defines the app's background.)
                    modifier = Modifier.fillMaxSize(), // Modifier를 사용하여 컴포넌트가 부모의 공간을 모두 채우도록 합니다. (Uses a Modifier to make the component fill the entire parent space.)
                    color = MaterialTheme.colorScheme.background // MaterialTheme의 배경 색상을 Surface의 색상으로 설정합니다. (Sets the Surface's color to the background color from the MaterialTheme.)
                ) {
                    NavHost(navController = navController, startDestination = "chat_screen") { // 앱의 네비게이션 그래프를 설정합니다. 시작 화면은 "chat_screen"입니다. (Sets up the app's navigation graph. The start destination is "chat_screen".)
                        composable("chat_screen") { // "chat_screen" 경로에 대한 Composable을 정의합니다. (Defines the composable for the "chat_screen" route.)
                            GeminiChatScreen( // GeminiChatScreen 컴포저블을 호출합니다. (Calls the GeminiChatScreen composable.)
                                onNavigateToFoodCodeScreen = { foodItems -> // 네비게이션 로직을 람다 함수로 전달합니다. foodItems를 받아 foodItemsState를 업데이트하고 food_code_screen으로 이동합니다. (Passes the navigation logic as a lambda function. It takes foodItems, updates foodItemsState, and navigates to the food_code_screen.)
                                    foodItemsState = foodItems
                                    navController.navigate("food_code_screen")
                                },
                                onPhotoTaken = { photo ->
                                    takenPhoto = photo // 사진 촬영 로직을 람다 함수로 전달합니다. photo를 받아 takenPhoto 상태를 업데이트합니다. (Passes the photo-taking logic as a lambda function. It takes a photo and updates the takenPhoto state.)
                                }
                            )
                        }
                        composable("food_code_screen") { // "food_code_screen" 경로에 대한 Composable을 정의합니다. (Defines the composable for the "food_code_screen" route.)
                            FoodCodeScreen( // FoodCodeScreen 컴포저블을 호출합니다. (Calls the FoodCodeScreen composable.)
                                foodItems = foodItemsState,
                                takenPhoto = takenPhoto, // foodItemsState와 takenPhoto를 인자로 전달합니다. (Passes the foodItemsState and takenPhoto as arguments.)
                                onBack = { // 뒤로가기 로직을 람다 함수로 전달합니다. 이전 화면으로 돌아가고 상태를 초기화합니다. (Passes the back navigation logic as a lambda function. It pops the back stack and clears the state.)
                                    navController.popBackStack()
                                    foodItemsState = emptyList()
                                    takenPhoto = null // 돌아갈 때 상태를 초기화하여 이전 데이터를 지웁니다. (Clears the previous data by resetting the state when navigating back.)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}