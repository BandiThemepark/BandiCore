# Custom scope
To run async tasks, you need to use a custom scope. This is a scope that is used to run async tasks. This is useful for running tasks in the background, such as using the back-end. The BandiCore provides its own custom scope, using the `DEFAULT` dispatcher. You can use it like this:

```kotlin
pluginScope.launch {
    // Your suspend code here
}
```

`pluginScope` can be imported from the BandiCore class.