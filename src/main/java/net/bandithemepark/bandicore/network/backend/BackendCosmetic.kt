package net.bandithemepark.bandicore.network.backend

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.util.Util
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.entity.Player
import java.io.IOException

object BackendCosmetic {
    fun getAll(callback: (JsonArray) -> Unit) {
        val client = BandiCore.instance.okHttpClient

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/cosmetics?limit=10000")
            .method("GET", null)
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser().parse(response.body!!.string()).asJsonObject
                if(responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    val data = responseJson.getAsJsonArray("data")
                    callback.invoke(data)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at loading all cosmetics, but no data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    fun getOwned(player: Player, callback: (JsonArray) -> Unit) {
        val client = BandiCore.instance.okHttpClient

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/cosmetics/getPlayerCosmetics/${player.uniqueId}")
            .method("GET", null)
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser().parse(response.body!!.string()).asJsonObject
                if(responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    val data = responseJson.getAsJsonArray("data")
                    callback.invoke(data)
                } else {
                    BandiCore.instance.logger.severe("An attempt was made at loading owned cosmetics of player ${player.name}, but no data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    /**
     * Gives a cosmetic to a player.
     * @param player The player to give the cosmetic to
     * @param cosmetic The cosmetic to give
     * @param callback What to do when the cosmetic is given
     */
    fun give(player: Player, cosmetic: Cosmetic, callback: () -> Unit) {
        val client = BandiCore.instance.okHttpClient
        val mediaType = "application/json".toMediaTypeOrNull()

        val data = JsonObject()
        data.addProperty("cosmeticId", cosmetic.id.toString())

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/cosmetics/give/${player.uniqueId}")
            .method("POST", data.toString().toRequestBody(mediaType))
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

    /**
     * Removes a cosmetic from a player.
     * @param player The player to remove the cosmetic from
     * @param cosmetic The cosmetic to remove
     * @param callback What to do when the cosmetic is removed
     */
    fun remove(player: Player, cosmetic: Cosmetic, callback: () -> Unit) {
        val client = BandiCore.instance.okHttpClient
        val mediaType = "application/json".toMediaTypeOrNull()

        val data = JsonObject()
        data.addProperty("cosmeticId", cosmetic.id.toString())

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/cosmetics/remove/${player.uniqueId}")
            .method("POST", data.toString().toRequestBody(mediaType))
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                callback.invoke()
            }
        })
    }

    /**
     * Update info about a player's owned cosmetic
     * @param player The player to update the cosmetic for
     * @param cosmetic The cosmetic to update
     * @param equipped Whether the cosmetic is equipped (optional)
     * @param amount The amount of the cosmetic (optional)
     * @param color The color of the cosmetic (optional)
     * @param callback What to do when the cosmetic is updated
     */
    fun updateOwned(player: Player, cosmetic: Cosmetic, equipped: Boolean? = null, amount: Int? = null, color: Color? = null, callback: () -> Unit) {
        val client = BandiCore.instance.okHttpClient
        val mediaType = "application/json".toMediaTypeOrNull()

        val data = JsonObject()
        if(equipped != null) data.addProperty("equipped", equipped)
        if(amount != null) data.addProperty("amount", amount)
        if(color != null) data.addProperty("color", color.asRGB())

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/cosmetics/updateOwnedCosmetic/${player.uniqueId}/${cosmetic.id}")
            .method("PUT", data.toString().toRequestBody(mediaType))
            .header("Authorization", BandiCore.instance.server.apiKey)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                callback.invoke()
            }
        })
    }
}