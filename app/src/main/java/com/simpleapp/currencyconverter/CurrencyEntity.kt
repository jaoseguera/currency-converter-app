package com.simpleapp.currencyconverter

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class CurrencyEntity(
    @PrimaryKey val baseCode: String,
    val rates: Map<String, Double>,
    val lastUpdated: Long
)
