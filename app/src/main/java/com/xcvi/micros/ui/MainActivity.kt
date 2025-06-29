package com.xcvi.micros.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.xcvi.micros.ui.destinations.Destinations
import com.xcvi.micros.ui.theme.MicrosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MicrosTheme(dynamicColor = false) {
                Destinations()
            }
        }
    }
}