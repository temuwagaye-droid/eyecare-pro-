package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ui.navigation.EyeCareNavGraph
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      var forceElegantDark by remember { mutableStateOf(true) }
      MyApplicationTheme(forceElegantDark = forceElegantDark) {
        EyeCareNavGraph(
          forceElegantDark = forceElegantDark,
          onToggleElegantDark = { forceElegantDark = it }
        )
      }
    }
  }
}

