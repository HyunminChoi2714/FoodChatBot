package com.example.foodchatbot

import android.graphics.Bitmap // 비트맵 이미지 처리를 위한 클래스를 임포트합니다. (Imports the class for handling Bitmap images.)
import androidx.compose.foundation.layout.* // Compose에서 레이아웃을 구성하기 위한 클래스들을 임포트합니다. (Imports classes for structuring the layout in Compose.)
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // LazyColumn과 그 항목들을 임포트합니다. 리스트를 효율적으로 표시하기 위한 Composable입니다. (Imports LazyColumn and its items. This is a Composable for efficiently displaying lists.)
import androidx.compose.foundation.Image // Compose에서 이미지를 표시하기 위한 클래스를 임포트합니다. (Imports the class for displaying an image in Compose.)
import androidx.compose.material3.* // Material3 디자인 시스템에서 UI 컴포넌트들을 임포트합니다. (Imports UI components from the Material3 design system.)
import androidx.compose.runtime.* // Compose에서 상태를 관리하기 위한 런타임 관련 함수들을 임포트합니다. (Imports runtime-related functions for managing state in Compose.)
import androidx.compose.ui.graphics.Color // 색상을 다루기 위한 Color 클래스를 임포트합니다. (Imports the Color class for handling colors.)
import androidx.compose.ui.graphics.asImageBitmap // 비트맵을 ImageBitmap으로 변환하기 위한 확장 함수를 임포트합니다. (Imports the extension function for converting a Bitmap to an ImageBitmap.)
import androidx.compose.ui.Modifier // UI 요소를 수정하기 위한 Modifier 클래스를 임포트합니다. (Imports the Modifier class for modifying UI elements.)
import androidx.compose.ui.platform.LocalContext // Composable 내에서 현재 Context를 얻기 위한 함수를 임포트합니다. (Imports the function to get the current Context within a Composable.)
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight // 폰트 두께를 설정하기 위한 FontWeight 클래스를 임포트합니다. (Imports the FontWeight class for setting font thickness.)
import androidx.compose.ui.unit.dp // Compose에서 UI 요소의 크기를 정의하기 위한 단위를 임포트합니다. (Imports units for defining the size of UI elements in Compose.)
import com.google.ai.client.generativeai.GenerativeModel // Google Generative AI 클라이언트 라이브러리에서 GenerativeModel 클래스를 임포트합니다. (Imports the GenerativeModel class from the Google Generative AI client library.)
import kotlinx.coroutines.launch // 코루틴 스코프 내에서 비동기 작업을 시작하기 위한 launch 함수를 임포트합니다. (Imports the launch function to start an asynchronous task within a coroutine scope.)
import java.io.BufferedReader // 파일을 읽기 위한 BufferedReader 클래스를 임포트합니다. (Imports the BufferedReader class for reading files.)
import java.io.InputStreamReader // InputStream으로부터 데이터를 읽기 위한 InputStreamReader 클래스를 임포트합니다. (Imports the InputStreamReader class for reading data from an InputStream.)
import java.nio.charset.StandardCharsets // 문자 인코딩을 위한 StandardCharsets 클래스를 임포트합니다. (Imports the StandardCharsets class for character encoding.)

data class DynamicInputInfo( // 사용자에게 필요한 추가 입력을 저장하기 위한 데이터 클래스를 정의합니다. (Defines a data class to hold additional input required from the user.)
    val label: String = "", // 첫 번째 입력 필드의 라벨입니다. (The label for the first input field.)
    val value: String = "", // 첫 번째 입력 필드의 값입니다. (The value for the first input field.)
    val label2: String = "", // 두 번째 입력 필드의 라벨입니다. (The label for the second input field.)
    val value2: String = "", // 두 번째 입력 필드의 값입니다. (The value for the second input field.)
    val imageResId: Int? = null
)

private fun getDynamicInputForFoodItems(foodItems: List<FoodItem>): DynamicInputInfo? {
    val foodDetail = foodItems.mapNotNull { it.geminiResponse }
    return when {
        foodDetail.any { it.contains("낱알류") } ->
            DynamicInputInfo(
                label = "사용한 곡물의 양을 mL 단위로 입력하세요.",
                value = "",
                label2 = "곡물을 불렸는지, 끓였는지의 여부를 입력하세요.",
                value2 = "",
                imageResId = R.drawable.grain
            )
        // 면 종류 선택에 따른 수정 필요
        foodDetail.any { it.contains("면류 및 밀가공품") } ->
            DynamicInputInfo(
                label = "사용한 면이 생면인지, 삶은 면인지, 마른 면인지, 인스턴트 면인지 고르시오.",
                value = "",
                label2 = "면의 중량을 확인하여 그램 단위로 기록하세요.",
                value2 = "",
                imageResId = R.drawable.noodles_wheat_product
            )

        foodDetail.any { it.contains("빵, 떡") } ->
            DynamicInputInfo(
                label = "빵이나 떡의 가로, 세로, 높이를 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.bread_rice_cake
            )

        foodDetail.any { it.contains("시리얼류") } ->
            DynamicInputInfo(
                label = "시리얼의 부피를 mL 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.cereal
            )

        foodDetail.any { it.contains("묵, 두부") } ->
            DynamicInputInfo(
                label = "묵이나 두부의 가로, 세로, 높이를 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.tofu
            )

        foodDetail.any { it.contains("옥수수") } ->
            DynamicInputInfo(
                label = "옥수수 알갱이들의 부피를 mL 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.corn
            )

        foodDetail.any { it.contains("만두, 바람떡") } ->
            DynamicInputInfo(
                label = "만두나 바람떡을 반원기둥으로 생각해서 반지름과 높이를 각각 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.baramtteok
            )

        foodDetail.any { it.contains("양배추, 양상추") } ->
            DynamicInputInfo(
                label = "양배추나 양상추를 구로 취급했을 때 그 반지름을 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.cabbage
            )

        foodDetail.any { it.contains("통마늘") } ->
            DynamicInputInfo(
                label = "통마늘의 갯수를 입력하세요.",
                value = "",
                label2 = "통마늘을 원기둥으로 취급하여 그 반지름과 높이를 cm 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.whole_garlic
            )

        foodDetail.any { it.contains("편마늘") } ->
            DynamicInputInfo(
                label = "편마늘이 총 몇 조각인지 갯수를 입력하세요.",
                value = "",
                label2 = "각 편마늘의 밑바닥 면적과 두께를 각각 cm^2과 cm 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.sliced_garlic
            )

        foodDetail.any { it.contains("마늘") or it.contains("생강") } ->
            DynamicInputInfo(
                label = "다진 마늘을 넣은 양을 그램 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.ginger_garlic
            )

        foodDetail.any { it.contains("상추, 깻잎") } ->
            DynamicInputInfo(
                label = "낱장으로 사용하신 경우, 섭취한 장수와 면적을 cm^2 단위로 입력하세요.",
                value = "",
                label2 = "다량으로 사용하신 경우, 부피를 mL 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.lettuce
            )

        foodDetail.any { it.contains("구형") } ->
            DynamicInputInfo(
                label = "원재료 형태로 사용하신 경우, 사용하신 구형 채소의 반지름을 입력하세요.",
                value = "",
                label2 = "썰은 형태로 사용하신 경우, 부피를 mL 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.spherical_onion
            )

        foodDetail.any { it.contains("쑥갓") } ->
            DynamicInputInfo(
                label = "사용한 쑥갓의 길이를 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.mugwort
            )

        foodDetail.any { it.contains("삶은 것") } ->
            DynamicInputInfo(
                label = "물기를 제거한 상태의 부피를 mL 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.boiled_veggies
            )

        foodDetail.any { it.contains("김치류") } ->
            DynamicInputInfo(
                label = "사용한 김치의 부피를 mL 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.gimchi
            )

        foodDetail.any { it.contains("양송이버섯") } ->
            DynamicInputInfo(
                label = "사용한 양송이버섯의 개수, 그리고 구로 취급했을 때 그 반지름을 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.button_mushroom
            )

        foodDetail.any { it.contains("느타리버섯") } ->
            DynamicInputInfo(
                label = "사용한 느타리버섯의 부피를 mL 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.oyster_mushroom
            )

        foodDetail.any { it.contains("그 외 버섯류") } ->
            DynamicInputInfo(
                label = "버섯의 갓 부분을 반구로 취급하여 반지름을 cm 단위로 입력하세요.",
                value = "",
                label2 = "버섯의 기둥 부분을 원기둥으로 취급하여 반지름과 높이를 각각 cm 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.other_mushroom
            )

        foodDetail.any { it.contains("마른 김, 마른 다시마") } ->
            DynamicInputInfo(
                label = "김의 가로와 세로 길이를 cm 단위로 각각 입력하세요.",
                value = "",
                imageResId = R.drawable.dried_seaweed
            )

        foodDetail.any { it.contains("파래, 미역줄기, 매생이") } ->
            DynamicInputInfo(
                label = "사용한 부피를 mL 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.maesaengi
            )

        foodDetail.any { it.contains("땅콩, 아몬드, 캐슈넛") } ->
            DynamicInputInfo(
                label = "사용한 견과류의 개수를 입력하세요.",
                value = "",
                imageResId = R.drawable.peanut
            )

        foodDetail.any { it.contains("밤") } ->
            DynamicInputInfo(
                label = "사용한 밤의 개수, 그리고 한 알을 반구로 취급했을 때 그 반지름을 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.chestnut
            )

        foodDetail.any { it.contains("호두") } ->
            DynamicInputInfo(
                label = "사용한 호두의 개수, 그리고 한 알을 구로 취급했을 때 그 반지름을 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.chestnut
            )

        foodDetail.any { it.contains("믹스넛") } ->
            DynamicInputInfo(
                label = "사용한 믹스넛의 총 중량을 g 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.mixed_nuts
            )

        foodDetail.any { it.contains("구형 과일") } ->
            DynamicInputInfo(
                label = "사용한 과일의 개수, 그리고 과일을 구로 취급했을 때 그 반지름을 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.spherical_fruit
            )

        foodDetail.any { it.contains("단감, 연시, 대봉, 참외, 키위") } ->
            DynamicInputInfo(
                label = "사용한 과일을 원기둥으로 취급했을 때, 밑면의 반지름과 높이를 각각 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.sweetpersimmon_kiwi
            )

        foodDetail.any { it.contains("바나나") } ->
            DynamicInputInfo(
                label = "사용한 바나나의 개수, 그리고 바나나를 원기둥으로 취급했을 때 밑면의 반지름과 높이를 각각 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.banana
            )

        foodDetail.any { it.contains("딸기, 대추") } ->
            DynamicInputInfo(
                label = "낱개로 사용하신 경우, 개수와 원기둥으로 취급했을 때 밑면의 반지름과 높이를 각각 cm 단위로 입력하세요.",
                value = "",
                label2 = "다량으로 사용하신 경우, 부피를 mL 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.strawberry_jujube
            )

        foodDetail.any { it.contains("슬라이스 햄") } ->
            DynamicInputInfo(
                label = "사용한 슬라이스 햄의 개수를 입력하세요.",
                value = "",
                label2 = "슬라이스 햄의 가로와 세로를 각각 cm 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.sliced_ham
            )

        foodDetail.any { it.contains("그 외 육류") } ->
            DynamicInputInfo(
                label = "사용한 육류의 양을 그램 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.other_meat
            )

        foodDetail.any { it.contains("일반 어류") } ->
            DynamicInputInfo(
                label = "사용한 마리 수를 입력하세요.",
                value = "",
                label2 = "생선을 직육면체로 취급하여 각각 가로, 세로, 그리고 높이를 cm 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.normal_fish
            )

        foodDetail.any { it.contains("멸치") } ->
            DynamicInputInfo(
                label = "작은 멸치를 사용한 경우 용기에 담은 부피를 mL 단위로 입력하세요.",
                value = "",
                label2 = "대멸치를 사용한 경우 개수와 길이를 cm 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.anchovy
            )

        foodDetail.any { it.contains("미꾸라지") } ->
            DynamicInputInfo(
                label = "용기에 담겨있는 경우 부피를 mL 단위로 입력하세요.",
                value = "",
                label2 = "부피를 측정하지 못한 경우 중량을 근 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.loach
            )

        foodDetail.any { it.contains("게") } ->
            DynamicInputInfo(
                label = "사용한 게의 마리 수를 입력하세요.",
                value = "",
                label2 = "게를 직육면체로 취급하여 가로, 세로, 그리고 높이를 각각 cm 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.crab
            )

        foodDetail.any { it.contains("새우") } ->
            DynamicInputInfo(
                label = "사용한 새우의 마리 수를 입력하세요.",
                value = "",
                label2 = "새우를 원기둥으로 취급했을 때, 밑면의 반지름과 높이를 각각 cm 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.shrimp
            )

        foodDetail.any { it.contains("조개 종류") } ->
            DynamicInputInfo(
                label = "사용한 조개의 부피를 mL 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.types_of_clam
            )

        foodDetail.any { it.contains("가리비") } ->
            DynamicInputInfo(
                label = "사용한 가리비의 부피를 mL 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.scallop
            )

        foodDetail.any { it.contains("바지락, 홍합, 미더덕, 멍게") } ->
            DynamicInputInfo(
                label = "사용한 가식부의 부피를 mL 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.clam
            )

        foodDetail.any { it.contains("조개관자") } ->
            DynamicInputInfo(
                label = "관자를 원기둥으로 취급하여 밑면의 반지름과 높이를 각각 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.clam_scallop
            )

        foodDetail.any { it.contains("밋조개") } ->
            DynamicInputInfo(
                label = "사용한 맛조개의 개수를 입력하세요.",
                value = "",
                label2 = "맛조개를 직육면체로 취급했을 때, 가로와 세로, 높이를 각각 cm 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.razor_clam
            )

        foodDetail.any { it.contains("오징어") } ->
            DynamicInputInfo(
                label = "용기에 담겨있는 경우, 부피를 mL 단위로 입력하세요.",
                value = "",
                label2 = "용기에 담겨있지 않은 경우, 가로와 세로, 높이를 각각 cm 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.squid
            )

        foodDetail.any { it.contains("마른 오징어") } ->
            DynamicInputInfo(
                label = "마른 오징어의 몸통의 가로, 세로 길이를 cm 단위로 입력하세요.",
                value = "",
                label2 = "마른 오징어의 다리의 총 길이를 cm 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.dried_squid
            )

        foodDetail.any { it.contains("낙지") } ->
            DynamicInputInfo(
                label = "낙지를 용기에 담았을 때의 부피를 mL 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.nakji
            )

        foodDetail.any { it.contains("문어") } ->
            DynamicInputInfo(
                label = "문어의 몸통의 부피를 mL 형태로 입력하세요.",
                value = "",
                label2 = "다리는 개당 전체 크기를 직육면체로 취급했을 때 가로, 세로, 그리고 높이를 각각 cm 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.octopus
            )

        foodDetail.any { it.contains("쥐포") } ->
            DynamicInputInfo(
                label = "쥐포의 가로, 세로 길이를 각각 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.dried_filefish
            )

        foodDetail.any { it.contains("채 형태와 자건품") } ->
            DynamicInputInfo(
                label = "사용한 자건품의 부피를 mL 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.dried_goods
            )

        foodDetail.any { it.contains("명란젓") } ->
            DynamicInputInfo(
                label = "사용한 명란젓을 직육면체로 취급하여 가로, 세로, 그리고 높이를 각각 cm 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.pollack_roe
            )

        foodDetail.any { it.contains("그 외의 젓갈류") } ->
            DynamicInputInfo(
                label = "사용한 젓갈의 부피를 mL 단위로 입력하세요.",
                value = "",
                imageResId = R.drawable.other_jeotgal
            )

        foodDetail.any { it.contains("어묵") } ->
            DynamicInputInfo(
                label = "사각어묵의 경우, 가로와 세로 길이를 각각 cm 단위로 입력하세요.",
                value = "",
                label2 = "원형어묵의 경우, 밑면의 반지름과 높이를 각각 cm 단위로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.fish_cake
            )

        foodDetail.any { it.contains("난류") } ->
            DynamicInputInfo(
                label = "사용한 계란의 개수를 입력하세요.",
                value = "",
                imageResId = R.drawable.egg
            )

        foodDetail.any { it.contains("조미료류") } ->
            DynamicInputInfo(
                label = "사용한 양의 부피를 mL로 입력하세요.",
                value = "",
                imageResId = R.drawable.spices
            )

        foodDetail.any { it.contains("티백") } ->
            DynamicInputInfo(
                label = "티백이 분말 형태인지, 침출액 형태인지 입력하세요.",
                value = "",
                label2 = "사용한 티백의 부피를 mL로 입력하세요.",
                value2 = "",
                imageResId = R.drawable.teabag
            )
        else -> null
    }
}

@OptIn(ExperimentalMaterial3Api::class) // ExperimentalMaterial3Api를 사용하여 Material3의 실험적 API를 사용할 수 있도록 합니다. (Uses ExperimentalMaterial3Api to enable the use of experimental APIs from Material3.)
@Composable
fun FoodCodeScreen( // FoodCodeScreen Composable 함수를 정의합니다. (Defines the FoodCodeScreen Composable function.)
    foodItems: List<FoodItem>, // 이전 화면에서 전달된 FoodItem 목록입니다. (The list of FoodItems passed from the previous screen.)
    takenPhoto: Bitmap?, // 이전 화면에서 촬영된 비트맵 사진입니다. (The Bitmap photo taken on the previous screen.)
    onBack: () -> Unit // 뒤로 가기 버튼을 눌렀을 때 실행되는 람다 함수입니다. (A lambda function that is executed when the back button is pressed.)
) {
    var foodItemsState by remember { mutableStateOf(foodItems) } // foodItems의 상태를 저장하는 mutableStateOf 변수입니다. (A mutableStateOf variable to store the state of foodItems.)
    var selectedFoodItem by remember { mutableStateOf<FoodItem?>(null) } // 현재 선택된 FoodItem을 저장하는 nullable mutableStateOf 변수입니다. (A nullable mutableStateOf variable to store the currently selected FoodItem.)
    var errorMessage by remember { mutableStateOf<String?>(null) } // 에러 메시지를 저장하는 nullable mutableStateOf 변수입니다. (A nullable mutableStateOf variable to store error messages.)
    var isLoadingButton by remember { mutableStateOf(false) } // 버튼의 로딩 상태를 저장하는 mutableStateOf 변수입니다. (A mutableStateOf variable to store the loading state of the button.)

    var dynamicInputForCode by remember { mutableStateOf<String?>(null) }  // 다이나믹 입력이 필요한 FoodItem 코드를 저장하는 nullable mutableStateOf 변수입니다. (A nullable mutableStateOf variable to store the FoodItem code that requires dynamic input.)
    var dynamicInputInfo by remember { mutableStateOf<DynamicInputInfo?>(null) } // 다이나믹 입력 필드의 정보를 저장하는 nullable mutableStateOf 변수입니다. (A nullable mutableStateOf variable to store the information for dynamic input fields.)

    val coroutineScope = rememberCoroutineScope() // 코루틴 스코프를 생성하고 기억합니다. (Creates and remembers a coroutine scope.)
    val context = LocalContext.current // 현재 Composable의 컨텍스트를 얻습니다. (Gets the context of the current Composable.)

    var csvContent2 by remember { mutableStateOf("") } // CSV 파일의 내용을 저장하는 mutableStateOf 변수입니다. (A mutableStateOf variable to store the content of the CSV file.)
    LaunchedEffect(Unit) { // 컴포저블이 처음 실행될 때만 실행되는 블록입니다. (A block that runs only when the composable is first launched.)
        try {
            context.assets.open("foodclassification.csv").use { inputStream -> // assets 폴더에서 "foodclassification.csv" 파일을 엽니다. (Opens the "foodclassification.csv" file from the assets folder.)
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader -> // UTF-8 인코딩으로 InputStream을 읽기 위한 BufferedReader를 생성합니다. (Creates a BufferedReader to read the InputStream with UTF-8 encoding.)
                    csvContent2 = reader.readText() // 파일의 모든 텍스트를 읽어 csvContent2 변수에 저장합니다. (Reads all the text from the file and stores it in the csvContent2 variable.)
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error reading foodcode.csv: ${e.message}" // 파일 읽기 중 에러가 발생하면 에러 메시지를 설정하고 스택 트레이스를 출력합니다. (If an error occurs while reading the file, set the error message and print the stack trace.)
            e.printStackTrace()
        }
    }

    val generativeModel = remember { // Gemini 모델 인스턴스를 생성하고 기억합니다. (Creates and remembers a Gemini model instance.)
        try {
            GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = "AIzaSyAWb-u5X8FyEuj3_jYA7tmpKiphVaUH0Us"
            ) // "gemini-2.5-flash" 모델과 API 키를 사용하여 GenerativeModel을 초기화합니다. (Initializes the GenerativeModel with the "gemini-2.5-flash" model and API key.)
        } catch (e: Exception) {
            errorMessage = "API configuration error: ${e.message}" // API 구성 중 에러가 발생하면 에러 메시지를 설정하고 null을 반환합니다. (If an error occurs during API configuration, set the error message and return null.)
            null
        }
    }

    LaunchedEffect(Unit) { // 컴포저블이 처음 실행될 때 다이나믹 입력 정보가 필요한지 확인합니다. (Checks if dynamic input information is required when the composable is first launched.)
        val requiredInput = getDynamicInputForFoodItems(foodItemsState)
        if (requiredInput != null) {
            dynamicInputInfo = requiredInput // 필요하면 dynamicInputInfo를 업데이트합니다. (If required, updates the dynamicInputInfo.)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) { // UI 레이아웃을 정의하는 Column 컴포저블입니다. (A Column composable that defines the UI layout.)
        if (takenPhoto != null) {
            Image(
                bitmap = takenPhoto.asImageBitmap(),
                contentDescription = "Taken photo of food",
                modifier = Modifier.fillMaxWidth().height(200.dp).padding(bottom = 16.dp)
            ) // takenPhoto가 null이 아니면 이미지를 표시합니다. (If takenPhoto is not null, displays the image.)
        }
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Go Back")
        } // 뒤로 가기 버튼입니다. (The "Go Back" button.)

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) { // 식품 코드 버튼 목록을 효율적으로 표시하는 LazyColumn입니다. (A LazyColumn that efficiently displays the list of food code buttons.)
            items(foodItemsState, key = { it.foodCode }) { item -> // foodItemsState의 각 항목에 대해 반복하여 UI를 생성합니다. (Iterates over each item in foodItemsState to generate UI.)
                val isSelected = (selectedFoodItem?.foodCode == item.foodCode) // 현재 항목이 선택되었는지 확인합니다. (Checks if the current item is selected.)
                val showDynamicInput = dynamicInputForCode == item.foodCode && dynamicInputInfo != null // 이 항목에 다이나믹 입력 양식을 표시해야 하는지 결정합니다. (Determines if this item should show the dynamic input form.)

                Card( // 각 FoodItem에 대한 클릭 가능한 Card입니다. (A clickable Card for each FoodItem.)
                    onClick = {
                        if (isSelected && !showDynamicInput) {
                            selectedFoodItem = null
                            dynamicInputForCode = null
                            dynamicInputInfo = null // 선택된 항목이 이미 선택되었고 다이나믹 입력이 표시되지 않으면 선택을 해제합니다. (If the selected item is already selected and dynamic input is not shown, unselect it.)
                        } else if (!isLoadingButton) {
                            selectedFoodItem = item
                            isLoadingButton = true // 버튼이 로딩 중이 아니면 항목을 선택하고 로딩을 시작합니다. (If the button is not loading, select the item and start loading.)

                            coroutineScope.launch { // 카테고리 세부 정보를 가져오기 위해 API 호출을 합니다. (Makes an API call to get the category detail.)
                                try {
                                    val newPrompt = """
                                        음식 이름: ${item.foodName}
                                        
                                        $csvContent2
                                        위에 제공된 데이터를 바탕으로, 음식의 이름을 **반드시** **항목에 있는 내용 그대로**만 분류해서 출력해줘.
                                    """.trimIndent() // Gemini API에 보낼 새 프롬프트를 구성합니다. (Constructs a new prompt to be sent to the Gemini API.)
                                    val geminiResponse = generativeModel?.generateContent(newPrompt)

                                    val updatedList = foodItemsState.map {
                                        if (it.foodCode == item.foodCode) {
                                            it.copy(geminiResponse = geminiResponse?.text) // 항목의 geminiResponse를 업데이트합니다. (Updates the item's geminiResponse.)
                                        } else {
                                            it
                                        }
                                    }
                                    foodItemsState = updatedList

                                    val requiredInput = getDynamicInputForFoodItems(listOf(updatedList.find { it.foodCode == item.foodCode }!!)) // 이 항목에 다이나믹 입력이 필요한지 확인하고 그에 따라 상태를 설정합니다. (Checks if this item requires dynamic input and sets the state accordingly.)
                                    if (requiredInput != null) {
                                        dynamicInputForCode = item.foodCode
                                        dynamicInputInfo = requiredInput
                                    }

                                } catch (e: Exception) {
                                    errorMessage = "Error fetching details: ${e.message}" // 예외가 발생하면 에러 메시지를 설정합니다. (If an exception occurs, set the error message.)
                                } finally {
                                    isLoadingButton = false // 작업이 완료되면 로딩 상태를 false로 설정합니다. (When the task is complete, set the loading state to false.)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(item.foodName, fontWeight = FontWeight.Bold)
                        Text(item.foodCode, color = Color.Gray) // 식품 이름과 코드를 표시합니다. (Displays the food name and code.)

                        if (isSelected) { // 상태에 따라 조건부로 표시합니다. (Conditionally displays based on the state.)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (isLoadingButton) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                Text("Loading details...") // 로딩 중이면 진행 표시기를 표시합니다. (If loading, display a progress indicator.)
                            } else if (showDynamicInput) {
                                Text(item.geminiResponse ?: "No details found.", color = Color.Gray)

                                dynamicInputInfo?.imageResId?.let { resId ->
                                    Image(
                                        painter = painterResource(id = resId),
                                        contentDescription = "Food input example",
                                        modifier = Modifier.fillMaxWidth().height(150.dp).padding(vertical = 16.dp)
                                    )
                                }

                                OutlinedTextField(
                                    value = dynamicInputInfo!!.value,
                                    onValueChange = { newValue ->
                                        dynamicInputInfo = dynamicInputInfo!!.copy(value = newValue)
                                    },
                                    label = { Text(dynamicInputInfo!!.label) },
                                    modifier = Modifier.fillMaxWidth()
                                ) // 다이나믹 입력 양식을 표시합니다. (Displays the dynamic input form.)
                                if (dynamicInputInfo!!.label2 != "") {
                                    OutlinedTextField(
                                        value = dynamicInputInfo!!.value2,
                                        onValueChange = { newValue ->
                                            dynamicInputInfo = dynamicInputInfo!!.copy(value2 = newValue)
                                        },
                                        label = { Text(dynamicInputInfo!!.label2) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) // 두 번째 입력 필드가 필요하면 표시합니다. (If a second input field is needed, display it.)
                                }
                                Button(
                                    onClick = {
                                        // 다이나믹 입력 제출을 처리하는 로직입니다.
                                        // Logic to handle the submission of dynamic input.
                                        // 예: 두 번째 API 호출을 하고 dynamicInputForCode를 null로 재설정합니다.
                                        // e.g., Make a second API call, then reset dynamicInputForCode to null.
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                ) {
                                    Text("Submit")
                                } // 제출 버튼입니다. (The submit button.)
                            } else {
                                Text(item.geminiResponse ?: "No details found.")
                            } // Gemini 응답 텍스트를 표시합니다. (Displays the Gemini response text.)
                        }
                    }
                }
            }
        }
    }
}