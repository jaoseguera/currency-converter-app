package com.simpleapp.currencyconverter

data class ExchangeResponse (
    val result: String,
    val base_code: String,
    val conversion_rates: Map<String, Double>
)
