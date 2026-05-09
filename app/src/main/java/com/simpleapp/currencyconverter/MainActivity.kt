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
import androidx.compose.ui.tooling.preview.Preview
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
@Preview
@Composable
fun ConverterScreen() {
    var amount by remember { mutableStateOf("1.0") }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("EUR") }
    var rates by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(fromCurrency) {
        isLoading = true
        try {
            val response = RetrofitClient.apiService.getExchangeRates(fromCurrency)
            rates = response.conversion_rates
        }
        catch(e: Exception) {
            errorMessage = "Error: ${e.message}"
            println(errorMessage)
        } finally {
            isLoading = false
        }
    }

    val rate = rates[toCurrency] ?: 1.0
    val amountNumber = amount.toDoubleOrNull() ?: 0.0
    val result = amountNumber * rate

    val myCurrencies = listOf("USD", "EUR", "GBP", "CAD", "MXN")

    Column(modifier = Modifier.statusBarsPadding().padding(20.dp)) {
        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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

        if(isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Result: %.2f $toCurrency".format(result),
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF15803D)
            )
            Text(
                text = "1 $fromCurrency = $rate $toCurrency",
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