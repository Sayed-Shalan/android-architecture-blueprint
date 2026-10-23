# ADR 0001: Manual composition instead of Hilt

## Status

Accepted

## Context

The app has one screen and one repository. Hilt would add a compiler, a Gradle plugin, and generated components for a graph that is a single constructor call.

## Decision

`MainActivity` constructs `InMemoryBookingRepository` and passes it to `BookingListViewModel.factory`. The view model does not call a service locator. Tests construct the view model with a repository double.

## Consequences

There is no DI framework to learn in this repository. A second feature that needs scopes, assisted injection, or a test/prod switch larger than a constructor argument should revisit Hilt or a hand-written app graph extracted from the activity.

## Revisit when

A second data source or a second feature needs the same object graph.
