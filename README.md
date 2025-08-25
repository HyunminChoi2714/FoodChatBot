# FoodChatBot (HCAIL)
This Android application helps users find the correct food codes for ingredients based on their food title and description. It leverages Google's Gemini API to process user input and suggest relevant food items with their corresponding food codes.

# Precise Process of the Application
The application follows a simple, two-screen process to guide the user from input to result.

## 1. User Input and Photo
The user enters the name of a dish, along with its main ingredients, cooking method, and sauces on the GeminiChatScreen. They then take a photo of the food. Once the user clicks the "Convert Food Title" button, the app collects all the provided information and sends it to the Gemini API.

## 2. API Call and Food Code Suggestions
The application sends a detailed prompt to the Gemini API, including all the user's text input and the CSV content for reference. The API is instructed to respond only with a JSON array of suggested food names and their corresponding codes.

## 3. Displaying Food Codes
The app receives the JSON response, parses it, and displays the suggested food items as a list of buttons on the FoodCodeScreen. When the user clicks on a food item, the app makes a second API call to get more detailed information about that specific item's category.

## 4. Dynamic Input
If the food item's category requires additional details (e.g., volume for grains, dimensions for tofu), the application displays dynamic input fields to prompt the user for more information.

# Technology Used for Each Process

The application is built using modern Android development practices and several key technologies.

## User Interface
The UI is built entirely with Jetpack Compose, a declarative UI framework for Android. This allows for a reactive and efficient way to create the application's screens and components.

## Navigation
Jetpack Navigation is used to manage the flow between the GeminiChatScreen and the FoodCodeScreen. It handles transitioning between screens and managing the back stack.

## API Integration
The application uses the Gemini API for its core functionality of converting food titles to food codes. It communicates with the API to process natural language input and return structured data.

## Data Handling
The app reads food code data from a local CSV file stored in the assets folder. It uses standard Java I/O streams to read and parse this data before sending it to the Gemini API. The API's JSON response is then parsed using the org.json library.

## Camera
The ActivityResultContracts.TakePicturePreview() contract is used to launch the device's camera and retrieve a Bitmap of the photo taken by the user.
