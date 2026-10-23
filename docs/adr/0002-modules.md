# ADR 0002: Four modules

## Status

Accepted

## Context

A single module would compile, but it would not show a boundary that can be tested without the Android SDK. An empty `core` module would not.

## Decision

- `:core:model` is a Kotlin JVM module. It holds `Booking` and nothing Android-specific.
- `:core:data` is a Kotlin JVM module. It holds `BookingRepository` and the in-memory implementation. Its tests run on the JVM.
- `:feature:bookings` is an Android library. It holds the view model, the Compose screen, and the view-model tests.
- `:app` is the composition root and the only application module.

`:feature:bookings` depends on `:core:model` and `:core:data`. `:core:data` exposes `:core:model` with `api`, because `Booking` appears in the repository signatures. An `implementation` dependency would hide that type from the feature. The feature does not depend on `:app`.

## Consequences

Repository tests do not need an Android SDK. The feature cannot reach into the activity. Adding a network implementation belongs in `:core:data` or a new JVM module, not in Compose.

## Revisit when

A type in `:core:model` needs an Android API, or the feature split stops matching the package boundaries.
