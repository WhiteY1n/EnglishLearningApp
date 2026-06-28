package com.vu.englishlearningapp.ui.navigation

/**
 * Defines all navigation routes in the app.
 * Using a sealed class keeps routes type-safe and in one place.
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")

    // Flashcard flow
    data object FlashcardCollections : Screen("flashcard_collections")
    data object FlashcardStudy : Screen("flashcard_study/{collectionId}") {
        fun createRoute(collectionId: Int) = "flashcard_study/$collectionId"
    }

    // Quiz flow
    data object QuizList : Screen("quiz_list")
    data object QuizTaking : Screen("quiz_taking/{testId}") {
        fun createRoute(testId: Int) = "quiz_taking/$testId"
    }
    data object QuizResult : Screen("quiz_result")

    // Profile flow
    data object Profile : Screen("profile")
    data object EditProfile : Screen("edit_profile")

    // Admin Collection Management flow
    data object AdminDashboard : Screen("admin_dashboard")
    data object AdminUserManagement : Screen("admin_user_management")
    data object AdminCollectionList : Screen("admin_collection_list")
    data object AdminCollectionDetail : Screen("admin_collection_detail/{id}") {
        fun createRoute(id: Int) = "admin_collection_detail/$id"
    }
    data object AdminCreateCollection : Screen("admin_create_collection")
    data object AdminEditCollection : Screen("admin_edit_collection/{id}") {
        fun createRoute(id: Int) = "admin_edit_collection/$id"
    }
}
