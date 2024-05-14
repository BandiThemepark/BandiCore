package net.bandithemepark.bandicore.util.debug

interface Reloadable {
    fun reload()

    fun register(id: String) {
        reloadables[id] = this
    }

    companion object {
        val reloadables = hashMapOf<String, Reloadable>()

        fun getReloadable(id: String): Reloadable? {
            return reloadables[id]
        }
    }
}