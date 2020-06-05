package com.peterstev.decagonchallenge.models

data class DataModel(
    val page: String?, val per_page: Number?,
    val total: Number?, val total_pages: Number?,
    val data: List<UserData>?
)