package com.peterstev.decagonchallenge.models

data class UserData(
    val id: Int?,
    val username: String?,
    val about: String?,
    val submitted: Int?,
    val updated_at: String?,
    val submission_count: Int?,
    val comment_count: Int?,
    val created_at: Long?
)