package com.firas.carcareai.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat(
        "dd/MM/yyyy",
        Locale.FRANCE
    )

    return formatter.format(Date(timestamp))
}