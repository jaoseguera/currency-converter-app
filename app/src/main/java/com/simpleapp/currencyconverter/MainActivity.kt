package com.simpleapp.currencyconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simpleapp.currencyconverter.ui.theme.CurrencyConverterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurrencyConverterTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ConverterScreen()
                }
            }
        }
    }
}

@Composable
fun ConverterScreen() {
    var amount by remember { mutableStateOf("1.0") }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("EUR") }

    val myCurrencies = listOf("USD", "EUR", "GBP", "CAD", "MXN")

    Column(modifier = Modifier.statusBarsPadding().padding(20.dp)) {
        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        SimpleCurrencyPicker(
            label = "From",
            selected = fromCurrency,
            currencies = myCurrencies,
            onSelection = { fromCurrency = it }
        )

        SimpleCurrencyPicker(
            label = "To",
            selected = toCurrency,
            currencies = myCurrencies,
            onSelection = { toCurrency = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Result: $amount $fromCurrency = ?? $toCurrency",
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
fun SimpleCurrencyPicker(label: String, selected: String, currencies: List<String>, onSelection: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .border(1.dp, Color.Gray)
                .padding(16.dp)
        ) {
            Text(text = selected)

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencies.forEach { currencyCode ->
                    DropdownMenuItem(
                        text = { Text(currencyCode) },
                        onClick = {
                            onSelection(currencyCode)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}