package com.example.foodchatbot

import android.graphics.Bitmap // 비트맵 이미지 처리를 위한 클래스를 임포트합니다. (Imports the class for handling Bitmap images.)
import android.Manifest // Android Manifest에서 카메라 접근 권한을 임포트합니다. (Imports the camera access permission from the Android Manifest.)
import android.content.pm.PackageManager // 안드로이드 패키지 매니저에서 PackageManager 클래스를 임포트합니다. (Imports the PackageManager class from the Android package manager.)
import androidx.activity.compose.rememberLauncherForActivityResult // Compose에서 ActivityResultLauncher를 사용하기 위한 헬퍼 함수를 임포트합니다. (Imports the helper function to use ActivityResultLauncher in Compose.)
import androidx.activity.result.contract.ActivityResultContracts // 액티비티 결과 계약을 임포트하여 카메라 앱을 실행하고 결과를 받습니다. (Imports the activity result contract to launch the camera app and get a result.)
import androidx.compose.foundation.layout.* // Composable 함수 내에서 레이아웃을 구성하기 위한 클래스들을 임포트합니다. (Imports classes for structuring the layout within a Composable function.)
import androidx.compose.foundation.rememberScrollState // 스크롤 상태를 기억하기 위한 헬퍼 함수를 임포트합니다. (Imports the helper function to remember the scroll state.)
import androidx.compose.foundation.shape.RoundedCornerShape // 둥근 모서리 모양을 위한 클래스를 임포트합니다. (Imports the class for a rounded corner shape.)
import androidx.compose.foundation.text.KeyboardActions // 키보드 작업(예: Enter 키)을 처리하기 위한 클래스를 임포트합니다. (Imports the class for handling keyboard actions (e.g., Enter key).)
import androidx.compose.foundation.text.KeyboardOptions // 키보드 옵션(예: 키보드 유형)을 설정하기 위한 클래스를 임포트합니다. (Imports the class for setting keyboard options (e.g., keyboard type).)
import androidx.compose.foundation.verticalScroll // 수직 스크롤 가능하도록 설정하기 위한 Modifier 헬퍼 함수를 임포트합니다. (Imports the Modifier helper function to enable vertical scrolling.)
import androidx.compose.material3.* // Material3 디자인 시스템에서 UI 컴포넌트들을 임포트합니다. (Imports UI components from the Material3 design system.)
import androidx.compose.runtime.* // Compose에서 상태를 관리하기 위한 런타임 관련 함수들을 임포트합니다. (Imports runtime-related functions for managing state in Compose.)
import androidx.compose.ui.Alignment // Composable에서 컴포넌트의 정렬을 설정하기 위한 클래스를 임포트합니다. (Imports the class for setting the alignment of a component in a Composable.)
import androidx.compose.ui.Modifier // UI 요소를 수정하기 위한 Modifier 클래스를 임포트합니다. (Imports the Modifier class for modifying UI elements.)
import androidx.compose.ui.graphics.Color // 색상을 다루기 위한 Color 클래스를 임포트합니다. (Imports the Color class for handling colors.)
import androidx.compose.ui.platform.LocalContext // Composable 내에서 현재 Context(예: 애플리케이션 컨텍스트)를 얻기 위한 함수를 임포트합니다. (Imports the function to get the current Context (e.g., application context) within a Composable.)
import androidx.compose.ui.text.font.FontWeight // 폰트 두께를 설정하기 위한 FontWeight 클래스를 임포트합니다. (Imports the FontWeight class for setting font thickness.)
import androidx.compose.ui.text.input.KeyboardType // 키보드 유형을 설정하기 위한 KeyboardType 클래스를 임포트합니다. (Imports the KeyboardType class for setting the keyboard type.)
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Compose에서 UI 요소의 크기를 정의하기 위한 단위를 임포트합니다. (Imports units for defining the size of UI elements in Compose.)
import androidx.core.content.ContextCompat // 컨텍스트에서 권한을 확인하기 위한 헬퍼 함수를 임포트합니다. (Imports the helper function for checking permissions in a context.)
import androidx.navigation.ActivityNavigatorExtras // 네비게이션을 위한 ActivityNavigatorExtras 클래스를 임포트합니다. (참고: 이 클래스는 이 코드에서 사용되지 않음) (Imports the ActivityNavigatorExtras class for navigation. (Note: this class is not used in the provided code))
import com.google.ai.client.generativeai.GenerativeModel // Google Generative AI 클라이언트 라이브러리에서 GenerativeModel 클래스를 임포트합니다. (Imports the GenerativeModel class from the Google Generative AI client library.)
import kotlinx.coroutines.launch // 코루틴 스코프 내에서 비동기 작업을 시작하기 위한 launch 함수를 임포트합니다. (Imports the launch function to start an asynchronous task within a coroutine scope.)
import org.json.JSONArray // JSON 배열을 파싱하기 위한 JSONArray 클래스를 임포트합니다. (Imports the JSONArray class for parsing a JSON array.)
import java.io.BufferedReader // 파일을 읽기 위한 BufferedReader 클래스를 임포트합니다. (Imports the BufferedReader class for reading files.)
import java.io.InputStreamReader // InputStream으로부터 데이터를 읽기 위한 InputStreamReader 클래스를 임포트합니다. (Imports the InputStreamReader class for reading data from an InputStream.)
import java.nio.charset.StandardCharsets // 문자 인코딩을 위한 StandardCharsets 클래스를 임포트합니다. (Imports the StandardCharsets class for character encoding.)

data class FoodItem( // 식품 항목에 대한 데이터 클래스를 정의합니다. (Defines a data class for a food item.)
    val foodName: String, // 식품 이름을 저장하는 변수입니다. (A variable to store the food name.)
    val foodCode: String, // 식품 코드를 저장하는 변수입니다. (A variable to store the food code.)
    var geminiResponse: String? = null // Gemini 모델의 응답을 저장하는 nullable 변수입니다. (A nullable variable to store the Gemini model's response.)
)

data class FoodInputState( // 사용자 입력 상태를 위한 데이터 클래스를 정의합니다. (Defines a data class for the user input state.)
    val name: String = "", // 음식 이름을 저장하는 변수입니다. 기본값은 빈 문자열입니다. (A variable to store the food name. The default value is an empty string.)
    val ingredients: String = "", // 재료를 저장하는 변수입니다. 기본값은 빈 문자열입니다. (A variable to store ingredients. The default value is an empty string.)
    val method: String = "", // 조리법을 저장하는 변수입니다. 기본값은 빈 문자열입니다. (A variable to store the cooking method. The default value is an empty string.)
    val sauces: String = "" // 소스를 저장하는 변수입니다. 기본값은 빈 문자열입니다. (A variable to store sauces. The default value is an empty string.)
)

@OptIn(ExperimentalMaterial3Api::class) // ExperimentalMaterial3Api를 사용하여 Material3의 실험적 API를 사용할 수 있도록 합니다. (Uses ExperimentalMaterial3Api to enable the use of experimental APIs from Material3.)
@Composable // GeminiChatScreen Composable 함수를 정의합니다. (Defines the GeminiChatScreen Composable function.)
fun GeminiChatScreen(
    onNavigateToFoodCodeScreen: (List<FoodItem>) -> Unit, // FoodItem 목록을 인자로 받아 다른 화면으로 네비게이션하는 람다 함수입니다. (A lambda function that takes a list of FoodItems and navigates to another screen.)
    onPhotoTaken: (Bitmap?) -> Unit // Bitmap을 인자로 받아 촬영된 사진을 처리하는 람다 함수입니다. (A lambda function that takes a Bitmap and handles the taken photo.)
) {
    var inputState by remember { mutableStateOf(FoodInputState()) } // 사용자 입력 상태를 저장하는 mutableStateOf 변수입니다. (A mutableStateOf variable to store the user input state.)
    var responseText by remember { mutableStateOf("Gemini's response will appear here.") } // Gemini 모델의 응답 텍스트를 저장하는 mutableStateOf 변수입니다. (A mutableStateOf variable to store the response text from the Gemini model.)
    var isLoading by remember { mutableStateOf(false) } // API 호출 중 로딩 상태를 저장하는 mutableStateOf 변수입니다. (A mutableStateOf variable to store the loading state during an API call.)
    var errorMessage by remember { mutableStateOf<String?>(null) } // 에러 메시지를 저장하는 nullable mutableStateOf 변수입니다. (A nullable mutableStateOf variable to store error messages.)
    var dynamicInputInfo by remember { mutableStateOf<DynamicInputInfo?>(null) } // 이 변수는 코드에서 사용되지 않지만 선언되어 있습니다. (This variable is declared but not used in the code.)

    val coroutineScope = rememberCoroutineScope() // 코루틴 스코프를 생성하고 기억합니다. (Creates and remembers a coroutine scope.)
    val context = LocalContext.current // 현재 Composable의 컨텍스트를 얻습니다. (Gets the context of the current Composable.)

    var csvContent by remember { mutableStateOf("") } // CSV 파일 내용을 저장하는 mutableStateOf 변수입니다. (A mutableStateOf variable to store the content of the CSV file.)
    LaunchedEffect(Unit) { // 컴포저블이 처음 실행될 때만 실행되는 블록입니다. (A block that runs only when the composable is first launched.)
        try {
            context.assets.open("foodcode.csv").use { inputStream -> // assets 폴더에서 "foodcode.csv" 파일을 엽니다. (Opens the "foodcode.csv" file from the assets folder.)
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader -> // UTF-8 인코딩으로 InputStream을 읽기 위한 BufferedReader를 생성합니다. (Creates a BufferedReader to read the InputStream with UTF-8 encoding.)
                    csvContent = reader.readText() // 파일의 모든 텍스트를 읽어 csvContent 변수에 저장합니다. (Reads all the text from the file and stores it in the csvContent variable.)
                }
            }
        }
        catch (e: Exception) {
            errorMessage = "Error reading foodcode.csv: ${e.message}" // 파일 읽기 중 에러가 발생하면 에러 메시지를 설정합니다. (If an error occurs while reading the file, set the error message.)
            e.printStackTrace() // 에러 스택 트레이스를 출력합니다. (Prints the error stack trace.)
        }
    }

    val generativeModel = remember { // Gemini 모델 인스턴스를 생성하고 기억합니다. (Creates and remembers a Gemini model instance.)
        try {
            GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = "AIzaSyAWb-u5X8FyEuj3_jYA7tmpKiphVaUH0Us" // "gemini-2.5-flash" 모델과 API 키를 사용하여 GenerativeModel을 초기화합니다. (Initializes the GenerativeModel with the "gemini-2.5-flash" model and API key.)
            )
        }
        catch (e: Exception) {
            errorMessage = "API configuration error: ${e.message}" // API 구성 중 에러가 발생하면 에러 메시지를 설정합니다. (If an error occurs during API configuration, set the error message.)
            null // null을 반환하여 모델이 초기화되지 않았음을 나타냅니다. (Returns null to indicate that the model was not initialized.)
        }
    }

    val takePhotoLauncher = rememberLauncherForActivityResult( // 사진 촬영을 위한 ActivityResultLauncher를 생성하고 기억합니다. (Creates and remembers an ActivityResultLauncher for taking a photo.)
        contract = ActivityResultContracts.TakePicturePreview(), // ActivityResultContracts.TakePicturePreview()는 미리보기 비트맵을 반환하는 계약입니다. (ActivityResultContracts.TakePicturePreview() is the contract that returns a preview bitmap.)
        onResult = { photoBitmap -> // 결과가 반환될 때 실행되는 람다 함수입니다. (A lambda function that is executed when a result is returned.)
            onPhotoTaken(photoBitmap) // 촬영된 사진을 onPhotoTaken 람다를 통해 처리합니다. (Processes the taken photo via the onPhotoTaken lambda.)
            if (photoBitmap != null) { // photoBitmap이 null이 아니면 코루틴을 실행합니다. (If photoBitmap is not null, execute the coroutine.)
                if (inputState.name.isBlank()) { // 음식 이름이 비어 있는지 확인합니다. (Checks if the food name is blank.)
                    errorMessage = "Please enter a food name" // 비어 있으면 에러 메시지를 설정하고 로딩을 중지합니다. (If it is, set an error message and stop loading.)
                    isLoading = false // 람다 실행을 중단합니다. (Stops the lambda execution.)
                    return@rememberLauncherForActivityResult
                }
                if (generativeModel == null) { // GenerativeModel이 null인지 확인합니다. (Checks if the GenerativeModel is null.)
                    errorMessage = "API not configured properly" // null이면 에러 메시지를 설정하고 로딩을 중지합니다. (If it is, set an error message and stop loading.)
                    isLoading = false // 람다 실행을 중단합니다. (Stops the lambda execution.)
                    return@rememberLauncherForActivityResult
                }

                coroutineScope.launch { // 코루틴 스코프 내에서 비동기 작업을 시작합니다. (Starts an asynchronous task within the coroutine scope.)
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
                        """.trimIndent() // Gemini API에 보낼 전체 프롬프트를 구성합니다. (Constructs the full prompt to be sent to the Gemini API.)

                        val response = generativeModel.generateContent(fullPrompt) // Gemini 모델에 프롬프트를 전달하고 응답을 받습니다. (Sends the prompt to the Gemini model and gets the response.)
                        val rawText = response.text ?: "[]" // 응답 텍스트를 가져오거나 null이면 빈 배열 문자열을 사용합니다. (Gets the response text or uses an empty array string if it's null.)
                        val cleanedJsonText = rawText.trim() // 응답 텍스트의 양쪽 공백을 제거합니다. (Trims leading and trailing whitespace from the response text.)

                        val jsonArray = JSONArray(cleanedJsonText) // 정리된 텍스트를 JSONArray로 파싱합니다. (Parses the cleaned text into a JSONArray.)
                        val foodItems = mutableListOf<FoodItem>() // FoodItem 객체를 담을 리스트를 생성합니다. (Creates a list to hold FoodItem objects.)
                        for (i in 0 until jsonArray.length()) { // JSON 배열의 각 요소를 반복 처리합니다. (Iterates through each element of the JSON array.)
                            val jsonObject = jsonArray.getJSONObject(i) // JSON 배열에서 JSON 객체를 가져옵니다. (Gets the JSON object from the JSON array.)
                            foodItems.add(
                                FoodItem(
                                    foodName = jsonObject.getString("food_name"),
                                    foodCode = jsonObject.getString("food_code")
                                )
                            ) // JSONObject의 값으로 FoodItem 객체를 생성하고 리스트에 추가합니다. (Creates a FoodItem object with values from the JSONObject and adds it to the list.)
                        }
                        onNavigateToFoodCodeScreen(foodItems) // FoodCodeScreen으로 네비게이션하며 foodItems 목록을 전달합니다. (Navigates to the FoodCodeScreen, passing the list of foodItems.)
                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.message}" // 예외가 발생하면 에러 메시지를 설정합니다. (If an exception occurs, set the error message.)
                        e.printStackTrace() // 스택 트레이스를 출력합니다. (Prints the stack trace.)
                    } finally {
                        isLoading = false // 작업이 완료되면 로딩 상태를 false로 설정합니다. (When the task is complete, set the loading state to false.)
                    }
                }
            } else {
                isLoading = false // photoBitmap이 null이면 (사진 촬영 취소 등), 로딩 상태를 false로 설정합니다. (If photoBitmap is null (e.g., photo taking was canceled), set the loading state to false.)
            }
        }
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult( // 권한 요청을 위한 ActivityResultLauncher를 생성하고 기억합니다. (Creates and remembers an ActivityResultLauncher for requesting permission.)
        contract = ActivityResultContracts.RequestPermission(), // 권한 요청 계약을 사용합니다. (Uses the permission request contract.)
        onResult = { isGranted: Boolean -> // 권한 요청 결과가 반환될 때 실행되는 람다 함수입니다. (A lambda function that is executed when the permission request result is returned.)
            if (isGranted) { // 권한이 부여되었는지 확인합니다. (Checks if the permission was granted.)
                takePhotoLauncher.launch(null) // 권한이 부여되면 takePhotoLauncher를 실행하여 사진 촬영을 시작합니다. (If the permission is granted, launch the takePhotoLauncher to start taking a photo.)
            }
            else {
                errorMessage = "Camera permission is required to take a photo."
                isLoading = false // 권한이 거부되면 에러 메시지를 설정하고 로딩을 중지합니다. (If the permission is denied, set an error message and stop loading.)
            }
        }
    )

    Column( // UI 레이아웃을 정의하는 Column 컴포저블입니다. (A Column composable that defines the UI layout.)
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Modifier를 사용하여 화면을 채우고 패딩을 추가하며 중앙에 정렬합니다. (Uses Modifier to fill the screen, add padding, and center the content.)
    ) {
        Text(
            text = "Food Name Conversion",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp)
        )  // "Food Name Conversion" 텍스트를 표시하는 Text 컴포저블입니다. (A Text composable that displays the "Food Name Conversion" text.)

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) { // 스크롤 가능한 입력 필드를 담는 Column 컴포저블입니다. (A Column composable that holds the scrollable input fields.)
            OutlinedTextField(
                value = inputState.name,
                onValueChange = { inputState = inputState.copy(name = it) },
                label = { Text("Enter your food title here...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) // 음식 이름 입력을 위한 OutlinedTextField입니다. (An OutlinedTextField for the food name input.)

            OutlinedTextField(
                value = inputState.ingredients,
                onValueChange = { inputState = inputState.copy(ingredients = it) },
                label = { Text("Enter the main/sub ingredients") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) // 재료 입력을 위한 OutlinedTextField입니다. (An OutlinedTextField for the ingredients input.)

            OutlinedTextField(
                value = inputState.method,
                onValueChange = { inputState = inputState.copy(method = it) },
                label = { Text("Enter the cooking method (ex. 볶는다, 끓인다.)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) // 조리법 입력을 위한 OutlinedTextField입니다. (An OutlinedTextField for the cooking method input.)

            OutlinedTextField(
                value = inputState.sauces,
                onValueChange = { inputState = inputState.copy(sauces = it) },
                label = { Text("Enter seasonings/dressings/sauces") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) // 소스 입력을 위한 OutlinedTextField입니다. (An OutlinedTextField for the sauces input.)

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            } // errorMessage가 null이 아니면 에러 메시지를 표시하는 Text 컴포저블입니다. (A Text composable that displays the error message if errorMessage is not null.)
        }

        Button( // 버튼 컴포넌트입니다. (A Button component.)
            onClick = { // 클릭 시 로직을 실행하는 람다 함수입니다. (A lambda function that executes logic on click.)
                isLoading = true // 로딩 상태를 true로 설정합니다. (Sets the loading state to true.)
                val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) // 카메라 권한이 부여되었는지 확인합니다. (Checks if camera permission is granted.)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    takePhotoLauncher.launch(null) // 권한이 부여되었으면 takePhotoLauncher를 실행합니다. (If permission is granted, launch the takePhotoLauncher.)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA) // 권한이 없으면 권한 요청을 시작합니다. (If permission is not available, start the permission request.)
                }
            },
            enabled = !isLoading, // 로딩 중이 아니면 버튼을 활성화합니다. (Enables the button if not in a loading state.)
            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            ) // Modifier를 사용하여 버튼의 크기, 모양 및 색상을 설정합니다. (Uses Modifier to set the button's size, shape, and colors.)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                ) // 로딩 중이면 CircularProgressIndicator를 표시합니다. (If in a loading state, display a CircularProgressIndicator.)
            } else {
                Text("Convert Food Title", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            } // 로딩 중이 아니면 버튼 텍스트를 표시합니다. (If not in a loading state, display the button text.)
        }
    }
}