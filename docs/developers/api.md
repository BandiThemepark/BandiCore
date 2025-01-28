# API
To make calls to the API, we have multiple utility functions. This is how you use them:

```kotlin
// Notice how this method has been suspended
suspend fun backendMethod() {
    // Create a JSON object to send as the body
    val json = JsonObject()

    // Make the call to the API
    val response = Network.post(
        "/endpoint/url",
        json,
    )

    // Handle response
}
```

The methods available are:

- `Network.get(url: String)`: Makes a GET request to the given URL.
- `Network.post(url: String, body: JsonObject)`: Makes a POST request to the given URL with the given body.
- `Network.put(url: String, body: JsonObject)`: Makes a PUT request to the given URL with the given body.
- `Network.delete(url: String)`: Makes a DELETE request to the given URL.