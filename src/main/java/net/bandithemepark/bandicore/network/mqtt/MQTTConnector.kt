package net.bandithemepark.bandicore.network.mqtt

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.mode.ServerMode
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.Util
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.*

class MQTTConnector {
    var adress: String

    private val publisherId = UUID.randomUUID().toString().substring(0, 10)
    private var client: MqttClient? = null
    private val memoryPersistence = MemoryPersistence()

    private var reconnecting = false

    init {
        adress = BandiCore.instance.config.json.get("mqtt-address").asString

        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
            connect()
        }, 10)
    }

    /**
     * Connects to the MQTT server using the specified data in the config.json
     */
    private fun connect() {
        client = MqttClient(adress, publisherId, memoryPersistence)
        reconnect()
    }

    /**
     * Reconnects the client to the MQTT server
     */
    fun reconnect() {
        if(client!!.isConnected) client!!.disconnect()

        // Create options for the connection
        val options = MqttConnectOptions()
        options.isCleanSession = true
        options.maxInflight = 32768
        options.isAutomaticReconnect = true
        options.connectionTimeout = 60

        // Connect to the MQTT server
        try {
            reconnecting = false
            client!!.connect(options)
            sendMessage("/core/connection", "connected")
            Util.debug("MQTT", "Connected to MQTT server, and sent out a connection message.")

            // Start registered listeners
            for(listener in MQTTListener.registered) registerListener(listener)
        } catch (e: Exception) {
            BandiCore.instance.logger.severe("A failed attempt was made to connect to the MQTT server. The following message was sent back: ${e.message}. The server will now go into maintenance.")
            BandiCore.instance.server.changeServerMode(ServerMode.getFromId("maintenance")!!)
        }
    }

    /**
     * Disconnects the client from the MQTT server, if connected
     */
    fun disconnect() {
        if(client == null) return
        if(!client!!.isConnected) return

        client!!.disconnect()
        Util.debug("MQTT", "Disconnected from the MQTT server.")
    }

    /**
     * Publishes a message to the specified topic
     *
     * @param topic The topic to publish to
     * @param message The message to publish
     */
    fun sendMessage(topic: String, message: String) {
        if(client != null && client!!.isConnected) {
            try {
                client!!.publish(topic, MqttMessage(message.toByteArray()))
            } catch (e: Exception) {
                BandiCore.instance.logger.severe("There was an attempt at sending a message to the MQTT server, but an error occured. The client will now reconnect...")
                if(!reconnecting) {
                    reconnecting = true
                    reconnect()
                }
            }
        } else {
            if(client != null && !client!!.isConnected) {
                BandiCore.instance.logger.severe("There was an attempt at sending a message to the MQTT server, but the client was not connected. The client will now reconnect...")
                try {
                    throw Exception("test for stacktrace")
                } catch (e: Exception) {
                     e.printStackTrace()
                }
                if(!reconnecting) {
                    reconnecting = true
                    reconnect()
                }
            }
        }
    }

    /**
     * Starts a listener
     * @param listener The listener to use
     */
    fun registerListener(listener: MQTTListener) {
        client?.subscribe(listener.topic, 0) { topic, message ->
            listener.onMessage(topic, message.toString())
        }
    }
}