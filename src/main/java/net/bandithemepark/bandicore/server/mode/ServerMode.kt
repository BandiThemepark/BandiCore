package net.bandithemepark.bandicore.server.mode

class ServerMode(val id: String, val motd: String, val vipsCanJoin: Boolean, val playersCanJoin: Boolean) {
    fun register() {
        types.add(this)
    }

    companion object {
        val types = mutableListOf<ServerMode>()

        /**
         * Gets the server mode with the given id.
         * @param id The id of the server mode.
         * @return The server mode with the given id, null if not found
         */
        fun getFromId(id: String): ServerMode? {
            return types.find { it.id == id }
        }

        /**
         * Gets all the available server modes' IDs
         * @return All the available server modes' IDs
         */
        fun getAllIds(): List<String> {
            return types.map { it.id }
        }
    }
}