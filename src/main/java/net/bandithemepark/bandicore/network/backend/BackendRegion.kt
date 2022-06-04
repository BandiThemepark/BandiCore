package net.bandithemepark.bandicore.network.backend

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class BackendRegion() {
    fun getWithName(name: String, callback: (JsonObject) -> Unit) {
        val client = BandiCore.instance.okHttpClient

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/regions?name=$name")
            .method("GET", null)
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser.parseString(response.body!!.string()).asJsonObject
                if (responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    val returnData = responseJson.getAsJsonObject("data")
                    callback.invoke(returnData)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at loading data of region ${name}, but no response data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    fun getWithId(id: String, callback: (JsonObject) -> Unit) {
        val client = BandiCore.instance.okHttpClient

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/regions?id=$id")
            .method("GET", null)
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser.parseString(response.body!!.string()).asJsonObject
                if (responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    val returnData = responseJson.getAsJsonObject("data")
                    callback.invoke(returnData)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at loading data of region ${id}, but no response data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    fun create(name: String, callback: (JsonObject) -> Unit) {
        val data = JsonObject()
        data.addProperty("name", name)
        data.addProperty("displayName", name)
        data.addProperty("priority", 0)

        val client = BandiCore.instance.okHttpClient
        val mediaType = "application/json".toMediaTypeOrNull()

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/regions")
            .method("POST", data.toString().toRequestBody(mediaType))
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser.parseString(response.body!!.string()).asJsonObject
                if (responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    val returnData = responseJson.getAsJsonObject("data")
                    callback.invoke(returnData)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at creating a region named ${name}, but no response data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    fun deleteWithId(id: String, callback: () -> Unit) {
        val client = BandiCore.instance.okHttpClient

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/regions/$id")
            .method("DELETE", null)
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                callback.invoke()
            }
        })
    }

    fun deleteWithName(name: String, callback: () -> Unit) {
        val client = BandiCore.instance.okHttpClient

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/regions/$name")
            .method("DELETE", null)
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                callback.invoke()
            }
        })
    }

    fun getAll(callback: (JsonArray) -> Unit) {
        val client = BandiCore.instance.okHttpClient

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/regions")
            .method("GET", null)
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser.parseString(response.body!!.string()).asJsonObject
                if (responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    val returnData = responseJson.getAsJsonArray("data")
                    callback.invoke(returnData)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at loading all regions, but no response data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    fun save(name: String, displayName: String, priority: Int, areas: JsonObject, callback: (JsonObject) -> Unit) {
        val data = JsonObject()
        data.addProperty("displayName", displayName)
        data.addProperty("priority", priority)
        data.add("areas", areas)

        val client = BandiCore.instance.okHttpClient
        val mediaType = "application/json".toMediaTypeOrNull()

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/regions/$name")
            .method("PUT", data.toString().toRequestBody(mediaType))
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser.parseString(response.body!!.string()).asJsonObject
                if (responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    val returnData = responseJson.getAsJsonObject("data")
                    callback.invoke(returnData)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at updating a region named ${name}, but no response data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }
}