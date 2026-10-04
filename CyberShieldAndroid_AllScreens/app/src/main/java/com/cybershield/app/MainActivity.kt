package com.cybershield.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cybershield.app.ui.CyberShieldApp
import com.cybershield.app.ui.theme.CyberShieldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CyberShieldTheme {
                Surface(Modifier.fillMaxSize()) { CyberShieldApp() }
            }
        }
    }
}
