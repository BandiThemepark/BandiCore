package net.bandithemepark.bandicore.server.mode

class ServerMode(val id: String, val motd: String, val vipsCanJoin: Boolean, val playersCanJoin: Boolean) {
    fun register() {
        types.add(this)
    }

    companion object {
        val types = mutableListOf<ServerMode>()

        fun getFromId(id: String): ServerMode? {
            return types.find { it.id == id }
        }
    }
}