# ADR 0003: StateFlow and viewModelScope

## Status

Accepted

## Context

The list screen needs a loading, ready, and error state that survives recomposition and is cancelled when the activity's view model store is cleared.

## Decision

`BookingListViewModel` exposes `StateFlow<BookingListUiState>`. `refresh` launches on `viewModelScope`. The activity collects with `collectAsStateWithLifecycle`, so collection stops when the UI is stopped. Domain failures are `BookingPageResult.Failed`, not thrown exceptions. Cancellation of the coroutine is separate: clearing the `ViewModelStore` cancels `viewModelScope`, which is covered by a unit test with a repository that suspends until cancelled.

## Consequences

The UI does not catch `CancellationException`. A repository that throws other exceptions will crash the coroutine; the in-memory implementation does not do that. There is no saved-state handle. Process death drops the in-memory list.

## Revisit when

The screen must restore state after process death, or a repository starts throwing for expected failures.
