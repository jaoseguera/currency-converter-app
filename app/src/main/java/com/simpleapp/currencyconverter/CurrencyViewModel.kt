package com.simpleapp.currencyconverter

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CurrencyViewModel : ViewModel () {
    var amount by mutableStateOf("1.0")
    var fromCurrency by mutableStateOf("USD")
    var toCurrency by mutableStateOf("EUR")
    var rates by mutableStateOf<Map<String, Double>>(emptyMap())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var allCurrencies by mutableStateOf<List<String>>(emptyList())

    val result: Double
        get() {
            val rate = rates[toCurrency] ?: 1.0
            val amountNumber = amount.toDoubleOrNull() ?: 0.0
            return amountNumber * rate
        }

    fun fetchRates() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.apiService.getExchangeRates(fromCurrency)
                rates = response.conversion_rates
                allCurrencies = response.conversion_rates.keys.toList().sorted()
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}