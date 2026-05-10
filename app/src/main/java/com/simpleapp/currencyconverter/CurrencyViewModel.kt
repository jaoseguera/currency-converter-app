package com.simpleapp.currencyconverter

import android.app.Application
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CurrencyViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val currencyDao = db.currencyDao()
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
                val cached = db.currencyDao().getRates(fromCurrency)
                val oneDayInMs = 24 * 60 * 60 * 1000
                val currentTime = System.currentTimeMillis()

                if (cached != null && (currentTime - cached.lastUpdated < oneDayInMs)) {
                    rates = cached.rates
                } else {
                    val response = RetrofitClient.apiService.getExchangeRates(fromCurrency)
                    val newEntity = CurrencyEntity(
                        baseCode = fromCurrency,
                        rates = response.conversion_rates,
                        lastUpdated = System.currentTimeMillis()
                    )
                    db.currencyDao().insertRates(newEntity)
                    rates = response.conversion_rates
                }
                allCurrencies = rates.keys.toList().sorted()
                errorMessage = null
            } catch (e: Exception) {
                val fallbackData = currencyDao.getRates(fromCurrency)
                if (fallbackData != null) {
                    rates = fallbackData.rates
                    errorMessage = "Offline: Showing old data."
                } else {
                    errorMessage = "Error: No available data. ${e.message}"
                }
            }
            isLoading = false
        }
    }
}