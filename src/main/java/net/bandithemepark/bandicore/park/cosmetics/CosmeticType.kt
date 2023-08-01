package net.bandithemepark.bandicore.park.cosmetics

import com.google.gson.JsonObject
import org.bukkit.Color
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class CosmeticType(val id: String): Cloneable {
    lateinit var metadata: JsonObject

    abstract fun onMetadataLoad(metadata: JsonObject)
    abstract fun isColorable(): Boolean
    abstract fun onEquip(player: Player, color: Color?, cosmetic: Cosmetic)
    abstract fun onUnEquip(player: Player)
    abstract fun getDressingRoomItem(player: Player, color: Color?, cosmetic: Cosmetic): ItemStack

    fun register() {
        types.add(this)
    }

    override fun clone(): CosmeticType {
        return super.clone() as CosmeticType
    }

    companion object {
        val types = mutableListOf<CosmeticType>()

        fun getType(id: String): CosmeticType? {
            val type = types.find { it.id == id } ?: return null
            return type.clone()
        }
    }
}