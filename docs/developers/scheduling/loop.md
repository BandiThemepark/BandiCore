# Looping async tasks
The BandiCore has utilities for scheduling looped tasks. It is important that you use these instead of BukkitRunnables, since our option is more performant due to its usage of Kotlin Coroutines.

## Creating a loop
You can use the following code to create a loop:

```kotlin
Scheduler.loopAsync(500L) {
    // Code to run every 500 milliseconds
}
```

## Creating a loop with a delay
You can use the following code to create a loop with a delay:

```kotlin
Scheduler.loopAsyncDelayed(500L, 1000L) {
    // Code to run every 500 milliseconds, with a 1 second delay before the first run
}
```

## Stopping a loop
You can use the following code to stop a loop:

```kotlin
val loop = Scheduler.loopAsync(500L) { // This functions returns a Job instance
    // Code to run every 500 milliseconds
}

loop.cancel() // Stops the loop
```