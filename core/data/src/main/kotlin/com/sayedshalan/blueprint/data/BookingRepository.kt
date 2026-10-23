package com.sayedshalan.blueprint.data

import com.sayedshalan.blueprint.model.Booking
import com.sayedshalan.blueprint.model.BookingStatus
import java.time.Instant

sealed interface LoadFailure {
    data object Unavailable : LoadFailure
}

sealed interface BookingPageResult {
    data class Ready(
        val bookings: List<Booking>,
    ) : BookingPageResult

    data class Failed(
        val failure: LoadFailure,
    ) : BookingPageResult
}

interface BookingRepository {
    suspend fun list(): BookingPageResult
}

class InMemoryBookingRepository(
    private val seed: List<Booking> = demoBookings(),
    private val unavailable: Boolean = false,
) : BookingRepository {
    override suspend fun list(): BookingPageResult {
        if (unavailable) {
            return BookingPageResult.Failed(LoadFailure.Unavailable)
        }
        return BookingPageResult.Ready(seed.sortedBy { it.startAt })
    }
}

fun demoBookings(now: Instant = Instant.parse("2026-06-01T09:00:00Z")): List<Booking> =
    listOf(
        Booking(
            id = "b2",
            title = "Site walkthrough",
            customerName = "Karim Adel",
            startAt = now.plusSeconds(2 * 24 * 3600),
            status = BookingStatus.Scheduled,
        ),
        Booking(
            id = "b1",
            title = "Studio review",
            customerName = "Noura Hassan",
            startAt = now.plusSeconds(24 * 3600),
            status = BookingStatus.Scheduled,
        ),
        Booking(
            id = "b3",
            title = "Design critique",
            customerName = "Lina Farouk",
            startAt = now.plusSeconds(3 * 24 * 3600),
            status = BookingStatus.Completed,
        ),
    )
