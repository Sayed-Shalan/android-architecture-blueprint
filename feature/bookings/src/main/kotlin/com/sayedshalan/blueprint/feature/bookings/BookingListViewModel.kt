package com.sayedshalan.blueprint.feature.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.sayedshalan.blueprint.data.BookingPageResult
import com.sayedshalan.blueprint.data.BookingRepository
import com.sayedshalan.blueprint.data.LoadFailure
import com.sayedshalan.blueprint.model.Booking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BookingListUiState {
    data object Loading : BookingListUiState

    data class Ready(
        val bookings: List<Booking>,
    ) : BookingListUiState

    data class Error(
        val failure: LoadFailure,
    ) : BookingListUiState
}

class BookingListViewModel(
    private val repository: BookingRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<BookingListUiState>(BookingListUiState.Loading)
    val state: StateFlow<BookingListUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    companion object {
        fun factory(repository: BookingRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    @Suppress("UNCHECKED_CAST")
                    return BookingListViewModel(repository) as T
                }
            }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = BookingListUiState.Loading
            _state.value =
                when (val result = repository.list()) {
                    is BookingPageResult.Ready -> BookingListUiState.Ready(result.bookings)
                    is BookingPageResult.Failed -> BookingListUiState.Error(result.failure)
                }
        }
    }
}
