package com.simpleapp.currencyconverter

import androidx.room.*

@Dao
interface CurrencyDao {
    @Query(
        "SELECT * " +
                "FROM exchange_rates " +
                "WHERE baseCode = :base LIMIT 1")
    suspend fun getRates(base: String): CurrencyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(entity: CurrencyEntity)
}