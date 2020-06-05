package com.peterstev.decagonchallenge.models

data class UserData(
    val id: Number?,
    val username: String?,
    val about: String?,
    val submitted: Number?,
    val updated_at: String?,
    val submission_count: Number?,
    val comment_count: Number?,
    val created_at: Long?
)