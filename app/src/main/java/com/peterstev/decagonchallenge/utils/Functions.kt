package com.peterstev.decagonchallenge.utils

import java.util.*
import java.util.concurrent.TimeUnit

fun longToDate(value: Long): String = Date(TimeUnit.SECONDS.toMillis(value)).toString()