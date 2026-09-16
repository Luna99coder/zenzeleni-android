package com.example.zenzeleni

// Represents the exchange rate data returned by the external REST API.
// Field names match the JSON keys returned by the API exactly,
// so Retrofit/Gson can automatically convert JSON into this object.
data class ExchangeRateResponse(
    val base: String,
    val rates: Map<String, Double>
)