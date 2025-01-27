package net.bandithemepark.bandicore.network

import com.google.gson.JsonObject
import kotlinx.coroutines.suspendCancellableCoroutine
import net.bandithemepark.bandicore.BandiCore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object Network {
    /**
     * Perform a GET call on the API
     * @param url The URL to call, such as `/warps`
     */
    suspend fun get(url: String): Response {
        return fetch(Method.GET, url, null)
    }

    /**
     * Perform a GET call on the API
     * @param url The URL to call, such as `/warps`
     * @param body The body to send with the request
     */
    suspend fun post(url: String, body: JsonObject): Response {
        return fetch(Method.POST, url, body)
    }

    /**
     * Perform a GET call on the API
     * @param url The URL to call, such as `/warps`
     * @param body The body to send with the request
     */
    suspend fun put(url: String, body: JsonObject): Response {
        return fetch(Method.PUT, url, body)
    }

    /**
     * Perform a GET call on the API
     * @param url The URL to call, such as `/warps`
     */
    suspend fun delete(url: String): Response {
        return fetch(Method.DELETE, url, null)
    }

    val client = BandiCore.instance.okHttpClient
    val mediaType = "application/json".toMediaTypeOrNull()
    val apiKey = BandiCore.instance.server.apiKey

    private suspend fun fetch(method: Method, url: String, body: JsonObject?): Response {
        val requestBody = body?.toString()?.toRequestBody(mediaType)
        val request = Request.Builder()
            .url("https://api.bandithemepark.net$url")
            .method(method.name, requestBody)
            .header("Authorization", apiKey)
            .build()

        return suspendCancellableCoroutine { continuation ->
            val call = client.newCall(request)

            continuation.invokeOnCancellation {
                call.cancel()
            }

            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(e)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isActive) {
                        continuation.resume(response)
                    }
                }
            })
        }
    }

    enum class Method {
        GET,
        POST,
        PUT,
        DELETE
    }
}