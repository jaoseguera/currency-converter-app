package com.simpleapp.currencyconverter.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.simpleapp.currencyconverter.CurrencyViewModel
import com.simpleapp.currencyconverter.ui.components.AdvancedCurrencyPicker

@Composable
fun ConverterScreen(viewModel: CurrencyViewModel) {
    LaunchedEffect(Unit) {
        viewModel.fetchRates()
    }

    Column(modifier = Modifier.statusBarsPadding().padding(20.dp)) {
        TextField(
            value = viewModel.amount,
            onValueChange = { viewModel.amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AdvancedCurrencyPicker(
            label = "From",
            selected = viewModel.fromCurrency,
            currencies = viewModel.allCurrencies,
            onSelection = { viewModel.fromCurrency = it }
        )

        AdvancedCurrencyPicker(
            label = "To",
            selected = viewModel.toCurrency,
            currencies = viewModel.allCurrencies,
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

            val sourceRate = viewModel.rates[viewModel.fromCurrency] ?: 1.0
            val targetRate = viewModel.rates[viewModel.toCurrency] ?: 1.0
            val exchangeRate = if (sourceRate != 0.0) targetRate / sourceRate else 0.0

            Text(
                text = "1 ${viewModel.fromCurrency} = %.4f ${viewModel.toCurrency}".format(exchangeRate),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
