package com.arabicrossword.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.arabicrossword.game.data.model.DifficultyLevel
import com.arabicrossword.game.ui.game.GameScreen
import com.arabicrossword.game.ui.menu.MainMenuScreen
import com.arabicrossword.game.ui.theme.ArabicCrosswordTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            ArabicCrosswordTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CrosswordApp()
                }
            }
        }
    }
}

@Composable
fun CrosswordApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "menu"
    ) {
        composable("menu") {
            MainMenuScreen(
                onStartGame = { difficulty ->
                    navController.navigate("game/${difficulty.name}")
                }
            )
        }
        
        composable("game/{difficulty}") { backStackEntry ->
            val difficultyName = backStackEntry.arguments?.getString("difficulty") ?: "EASY"
            val difficulty = try {
                DifficultyLevel.valueOf(difficultyName)
            } catch (e: IllegalArgumentException) {
                DifficultyLevel.EASY
            }
            
            GameScreen(
                difficulty = difficulty,
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}