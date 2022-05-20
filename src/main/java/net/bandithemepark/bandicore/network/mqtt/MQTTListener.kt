package net.bandithemepark.bandicore.network.mqtt

import net.bandithemepark.bandicore.BandiCore

abstract class MQTTListener(val topic: String) {
    abstract fun onMessage(message: String)

    /**
     * Registers this listener, making the client subscribe to the topic.
     */
    fun register() {
        registered.add(this)
        BandiCore.instance.mqttConnector.registerListener(this)
    }

    companion object {
        val registered = mutableListOf<MQTTListener>()
    }
}