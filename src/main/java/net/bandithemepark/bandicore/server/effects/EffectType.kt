package net.bandithemepark.bandicore.server.effects

import com.google.gson.JsonObject
import org.bukkit.entity.Player

abstract class EffectType(val id: String): Cloneable {
    var debug = false

    fun register() {
        types.add(this)
    }

    public override fun clone(): EffectType {
        return super.clone() as EffectType
    }

    abstract fun loadSettings(json: JsonObject)
    abstract fun onPlay(players: List<Player>?)
    abstract fun onTick()
    abstract fun onEffectEnd()

    companion object {
        val types = mutableListOf<EffectType>()

        fun getType(id: String): EffectType? {
            return types.find { it.id == id }
        }
    }
}