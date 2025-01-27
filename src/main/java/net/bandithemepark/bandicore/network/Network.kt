package net.bandithemepark.bandicore.network

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

object Network {
    /**
     * Perform a GET call on the API
     * @param url The URL to call, such as `/warps`
     * @param callback The callback to run when the request is complete
     */
    fun get(url: String, callback: (Response) -> Unit) {
        fetch(Method.GET, url, null, callback)
    }

    /**
     * Perform a GET call on the API
     * @param url The URL to call, such as `/warps`
     * @param body The body to send with the request
     * @param callback The callback to run when the request is complete
     */
    fun post(url: String, body: JsonObject, callback: (Response) -> Unit) {
        fetch(Method.POST, url, body, callback)
    }

    /**
     * Perform a GET call on the API
     * @param url The URL to call, such as `/warps`
     * @param body The body to send with the request
     * @param callback The callback to run when the request is complete
     */
    fun put(url: String, body: JsonObject, callback: (Response) -> Unit) {
        fetch(Method.PUT, url, body, callback)
    }

    /**
     * Perform a GET call on the API
     * @param url The URL to call, such as `/warps`
     * @param body The body to send with the request
     * @param callback The callback to run when the request is complete
     */
    fun delete(url: String, callback: (Response) -> Unit) {
        fetch(Method.DELETE, url, null, callback)
    }

    val client = BandiCore.instance.okHttpClient
    val mediaType = "application/json".toMediaTypeOrNull()
    val apiKey = BandiCore.instance.server.apiKey

    private fun fetch(method: Method, url: String, body: JsonObject?, callback: (Response) -> Unit) {

        val request = Request.Builder()
            .url("https://api.bandithemepark.net$url")
            .method(method.name, body.toString().toRequestBody(mediaType))
            .header("Authorization", apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                callback.invoke(response)
            }
        })
    }

    enum class Method {
        GET,
        POST,
        PUT,
        DELETE
    }
}