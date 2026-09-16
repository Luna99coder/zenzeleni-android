package com.example.zenzeleni

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// A single shared Retrofit instance for the whole app,
// configured once and reused for every API call.
object RetrofitClient {

    private const val BASE_URL = "https://open.er-api.com/v6/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
