package com.peterstev.decagonchallenge.network

import com.peterstev.decagonchallenge.models.DataModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("article_users/search")
    fun getUsers(@Query("page") page: Int): Call<DataModel>
}