package com.sayedshalan.blueprint.feature.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import com.sayedshalan.blueprint.data.BookingPageResult
import com.sayedshalan.blueprint.data.BookingRepository
import com.sayedshalan.blueprint.data.InMemoryBookingRepository
import com.sayedshalan.blueprint.data.LoadFailure
import com.sayedshalan.blueprint.data.demoBookings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookingListViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun refreshPublishesSortedBookings() =
        runTest(dispatcher) {
            val viewModel = BookingListViewModel(InMemoryBookingRepository(seed = demoBookings()))
            dispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue(state is BookingListUiState.Ready)
            assertEquals(
                listOf("b1", "b2", "b3"),
                (state as BookingListUiState.Ready).bookings.map { it.id },
            )
        }

    @Test
    fun refreshPublishesUnavailable() =
        runTest(dispatcher) {
            val viewModel = BookingListViewModel(InMemoryBookingRepository(unavailable = true))
            dispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue(state is BookingListUiState.Error)
            assertEquals(LoadFailure.Unavailable, (state as BookingListUiState.Error).failure)
        }

    @Test
    fun clearingTheStoreCancelsAnInFlightLoad() =
        runTest(dispatcher) {
            val repository = SuspendedRepository()
            val store = ViewModelStore()
            val provider =
                ViewModelProvider(
                    store,
                    object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(
                            modelClass: Class<T>,
                            extras: CreationExtras,
                        ): T {
                            @Suppress("UNCHECKED_CAST")
                            return BookingListViewModel(repository) as T
                        }
                    },
                )

            provider[BookingListViewModel::class.java]
            dispatcher.scheduler.runCurrent()
            assertTrue(repository.started)

            store.clear()
            dispatcher.scheduler.advanceUntilIdle()
            assertTrue(repository.cancelled)
        }
}

private class SuspendedRepository : BookingRepository {
    var started: Boolean = false
    var cancelled: Boolean = false

    override suspend fun list(): BookingPageResult {
        started = true
        try {
            awaitCancellation()
        } finally {
            cancelled = true
        }
    }
}
