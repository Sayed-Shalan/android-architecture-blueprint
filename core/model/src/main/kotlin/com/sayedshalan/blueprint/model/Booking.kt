package com.sayedshalan.blueprint.model

import java.time.Instant

enum class BookingStatus {
    Scheduled,
    Completed,
    Cancelled,
}

data class Booking(
    val id: String,
    val title: String,
    val customerName: String,
    val startAt: Instant,
    val status: BookingStatus,
)
