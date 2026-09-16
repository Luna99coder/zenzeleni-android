package com.example.zenzeleni

import retrofit2.Call
import retrofit2.http.GET

// Defines the REST API endpoints this app can call.
// Retrofit generates the actual networking code behind this interface automatically.
interface ApiService {

    // Fetches the latest exchange rates with South African Rand (ZAR) as the base currency
    @GET("latest?base=ZAR")
    fun getExchangeRates(): Call<ExchangeRateResponse>
}