package de.bollich.fitnessboy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.bollich.fitnessboy.ui.FitnessBoyApp
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitnessBoyTheme {
                FitnessBoyApp()
            }
        }
    }
}
