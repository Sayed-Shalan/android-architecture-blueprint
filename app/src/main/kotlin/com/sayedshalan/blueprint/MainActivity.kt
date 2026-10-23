package com.sayedshalan.blueprint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sayedshalan.blueprint.data.InMemoryBookingRepository
import com.sayedshalan.blueprint.feature.bookings.BookingListScreen
import com.sayedshalan.blueprint.feature.bookings.BookingListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = InMemoryBookingRepository()
        setContent {
            MaterialTheme {
                val viewModel: BookingListViewModel =
                    viewModel(factory = BookingListViewModel.factory(repository))
                val state by viewModel.state.collectAsStateWithLifecycle()
                BookingListScreen(state = state, onRetry = viewModel::refresh)
            }
        }
    }
}
