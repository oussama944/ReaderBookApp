package com.projet.appreader.utils

import com.google.firebase.Timestamp
import java.text.DateFormat

fun formatDate(timestamp: Timestamp): String {
    return DateFormat
        .getInstance()
        .format(timestamp.toDate())
        .toString().split(",")[0]
}