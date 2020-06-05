package com.peterstev.decagonchallenge.utils

import android.content.Context
import com.peterstev.decagonchallenge.network.ApiInterface
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun longToDate(value: Long): String {
    val date = Date(TimeUnit.SECONDS.toMillis(value))
    val dateFormat = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())
    return dateFormat.format(date)
}

fun getRetrofit(context: Context): ApiInterface = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(getClient(context))
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(ApiInterface::class.java)

fun getClient(context: Context): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(ChuckInterceptor(context))
    .build()