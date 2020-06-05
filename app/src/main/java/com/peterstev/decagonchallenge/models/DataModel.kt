package com.peterstev.decagonchallenge.models

data class DataModel(
    val page: String?, val per_page: Int?,
    val total: Int?, val total_pages: Int?,
    val data: List<UserData>?
)