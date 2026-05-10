package com.simpleapp.currencyconverter

import android.app.Application
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simpleapp.currencyconverter.data.local.AppDatabase
import com.simpleapp.currencyconverter.data.local.CurrencyEntity
import com.simpleapp.currencyconverter.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class CurrencyViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val currencyDao = db.currencyDao()
    var amount by mutableStateOf("1.0")
    var fromCurrency by mutableStateOf("CAD")
    var toCurrency by mutableStateOf("MXN")
    var rates by mutableStateOf<Map<String, Double>>(emptyMap())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var allCurrencies by mutableStateOf<List<String>>(emptyList())

    val result: Double
        get() {
            val amountNumber = amount.toDoubleOrNull() ?: 0.0

            val rateFromUSDToSource = rates[fromCurrency] ?: return 1.0
            val rateFromUSDToTarget = rates[toCurrency] ?: return 1.0

            return if (rateFromUSDToSource != 0.0) {
                (amountNumber / rateFromUSDToSource) * rateFromUSDToTarget
            } else 0.0
        }

    private val TAG = "CurrencyAppDebug"
    fun fetchRates() {
        viewModelScope.launch {
            isLoading = true
            Log.d(TAG, "Start fetchRates for USD")
            try {
                val cached = db.currencyDao().getRates("USD")
                val oneDayInMs = 24 * 60 * 60 * 1000
                val currentTime = System.currentTimeMillis()

                if (cached != null && (currentTime - cached.lastUpdated < oneDayInMs)) {
                    val ageInHours = (currentTime - cached.lastUpdated) / (1000 * 60 * 60)
                    Log.d(TAG, "Cached data valid. Last Updated: $ageInHours hours ago.")
                    rates = cached.rates
                } else {
                    Log.d(TAG, "Cached data invalid. Calling API...")
                    val response = RetrofitClient.apiService.getExchangeRates("USD")
                    val newEntity = CurrencyEntity(
                        baseCode = "USD",
                        rates = response.conversion_rates,
                        lastUpdated = System.currentTimeMillis()
                    )
                    db.currencyDao().insertRates(newEntity)
                    rates = response.conversion_rates
                }
                allCurrencies = rates.keys.toList().sorted()
                errorMessage = null
            } catch (e: Exception) {
                val fallbackData = currencyDao.getRates("USD")
                if (fallbackData != null) {
                    rates = fallbackData.rates
                    errorMessage = "Offline: Showing old data."
                } else {
                    errorMessage = "Error: No available data. ${e.message}"
                }
                Log.d(TAG, errorMessage ?: "Empty error message.")
            }
            isLoading = false
        }
    }
}