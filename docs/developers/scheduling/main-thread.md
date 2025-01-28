# Main thread scheduling from async tasks
Some Bukkit API methods require that you are on the main thread. These include world and entity updates. To easily switch to the main thread while on an async task, we provide the following utility:

```kotlin
pluginScope.launch {
    // Run async code
    withContext(BukkitDispatcher) {
        // Run code on the main thread
    }
}
```