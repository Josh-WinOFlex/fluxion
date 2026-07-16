package com.winoflex.fluxion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.winoflex.fluxion.navigation.FluxionNavGraph
import com.winoflex.fluxion.ui.theme.FluxionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FluxionTheme {
                FluxionNavGraph()
            }
        }
    }
}
