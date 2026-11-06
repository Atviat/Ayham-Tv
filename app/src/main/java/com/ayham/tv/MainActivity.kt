package com.ayham.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.ayham.tv.ui.AyhamTheme
import com.ayham.tv.ui.HomeScreen

class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AyhamTheme {
                // A launcher is the root screen: Back must not close it.
                BackHandler(enabled = true) { }
                HomeScreen(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-scan so newly installed or removed apps show up.
        viewModel.refresh()
    }
}
