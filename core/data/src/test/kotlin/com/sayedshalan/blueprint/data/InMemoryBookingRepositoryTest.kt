package com.sayedshalan.blueprint.data

import com.sayedshalan.blueprint.model.Booking
import com.sayedshalan.blueprint.model.BookingStatus
import java.time.Instant
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryBookingRepositoryTest {
    @Test
    fun listSortsByStartTime() =
        runTest {
            val repository =
                InMemoryBookingRepository(
                    seed =
                        listOf(
                            booking("later", "2026-06-03T10:00:00Z"),
                            booking("sooner", "2026-06-02T10:00:00Z"),
                        ),
                )

            val result = repository.list()

            assertTrue(result is BookingPageResult.Ready)
            val ready = result as BookingPageResult.Ready
            assertEquals(listOf("sooner", "later"), ready.bookings.map { it.id })
        }

    @Test
    fun listReportsUnavailableWithoutThrowing() =
        runTest {
            val result = InMemoryBookingRepository(unavailable = true).list()
            assertTrue(result is BookingPageResult.Failed)
        }

    private fun booking(
        id: String,
        start: String,
    ) = Booking(
        id = id,
        title = id,
        customerName = "Ada",
        startAt = Instant.parse(start),
        status = BookingStatus.Scheduled,
    )
}
