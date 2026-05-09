package com.simpleapp.currencyconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.simpleapp.currencyconverter.ui.theme.CurrencyConverterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = CurrencyViewModel()

        enableEdgeToEdge()
        setContent {
            CurrencyConverterTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ConverterScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun ConverterScreen(viewModel: CurrencyViewModel) {
    LaunchedEffect(viewModel.fromCurrency) {
        viewModel.fetchRates()
    }

    val myCurrencies = listOf("USD", "EUR", "GBP", "CAD", "MXN")

    Column(modifier = Modifier.statusBarsPadding().padding(20.dp)) {
        TextField(
            value = viewModel.amount,
            onValueChange = { viewModel.amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SimpleCurrencyPicker(
            label = "From",
            selected = viewModel.fromCurrency,
            currencies = myCurrencies,
            onSelection = { viewModel.fromCurrency = it }
        )

        SimpleCurrencyPicker(
            label = "To",
            selected = viewModel.toCurrency,
            currencies = myCurrencies,
            onSelection = { viewModel.toCurrency = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if(viewModel.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Result: %.2f ${viewModel.toCurrency}".format(viewModel.result),
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF15803D)
            )
            Text(
                text = "1 ${viewModel.fromCurrency} = ${viewModel.rates[viewModel.toCurrency]} ${viewModel.toCurrency}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
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