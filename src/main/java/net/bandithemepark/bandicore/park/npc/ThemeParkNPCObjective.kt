package net.bandithemepark.bandicore.park.npc

abstract class ThemeParkNPCObjective: Cloneable {
    lateinit var npc: ThemeParkNPC

    abstract fun onCreate()
    abstract fun onUpdate()

    fun register() {
        types.add(this)
    }

    fun finish() {
        npc.currentObjective = null
    }

    override fun clone(): ThemeParkNPCObjective {
        return super.clone() as ThemeParkNPCObjective
    }

    companion object {
        val types = mutableListOf<ThemeParkNPCObjective>()

        fun getNewObjective(npc: ThemeParkNPC): ThemeParkNPCObjective {
            val type = types.random()
            val newType = type.clone()
            newType.npc = npc
            newType.onCreate()
            return newType
        }
    }
}