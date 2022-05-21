package net.bandithemepark.bandicore.network.mqtt

abstract class MQTTListener(val topic: String) {
    abstract fun onMessage(message: String)

    /**
     * Registers this listener, making the client subscribe to the topic.
     */
    fun register() {
        registered.add(this)
    }

    companion object {
        val registered = mutableListOf<MQTTListener>()
    }
}