package com.simpleapp.currencyconverter;

import retrofit2.http.GET;
import retrofit2.http.Path;

interface ApiService {
    @GET("v6/6a2ebb6d1391365cca26e1da/latest/{base}")
    suspend fun getExchangeRates(@Path("base") base: String): ExchangeResponse
}
