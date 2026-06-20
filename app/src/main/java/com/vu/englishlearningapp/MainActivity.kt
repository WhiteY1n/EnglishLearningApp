package com.vu.englishlearningapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.vu.englishlearningapp.ui.navigation.AppNavGraph
import com.vu.englishlearningapp.ui.navigation.Screen
import com.vu.englishlearningapp.ui.theme.EnglishLearningAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get the DI container from the Application class
        val appContainer = (application as EnglishLearningApp).container

        setContent {
            EnglishLearningAppTheme {
                // Check if user has a saved token to decide the start screen
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    val token = appContainer.tokenManager.getAccessToken()
                    startDestination = if (token.isNullOrEmpty()) {
                        Screen.Login.route
                    } else {
                        Screen.Home.route
                    }
                }

                if (startDestination != null) {
                    val navController = rememberNavController()
                    AppNavGraph(
                        navController = navController,
                        appContainer = appContainer,
                        startDestination = startDestination!!
                    )
                } else {
                    // Show a loading indicator while checking token
                    LoadingScreen()
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}