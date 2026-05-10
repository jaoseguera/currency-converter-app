package com.simpleapp.currencyconverter.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simpleapp.currencyconverter.CurrencyViewModel
import com.simpleapp.currencyconverter.ui.theme.CurrencyConverterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CurrencyConverterTheme {
                val viewModel: CurrencyViewModel = viewModel()
                Surface(modifier = Modifier.fillMaxSize()) {
                    ConverterScreen(viewModel)
                }
            }
        }
    }
}