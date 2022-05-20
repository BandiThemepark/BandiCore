package net.bandithemepark.bandicore.util.menu

import net.bandithemepark.bandicore.util.Util
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory

class MenuUtil {
    companion object {
        const val FILLED_45 = "<#FFFFFF>\uE002\uE001"
        const val GENERIC_9 = "<#FFFFFF>\uE002\uE005"
        const val GENERIC_18 = "<#FFFFFF>\uE002\uE006"
        const val GENERIC_27 = "<#FFFFFF>\uE002\uE007"
        const val GENERIC_36 = "<#FFFFFF>\uE002\uE008"
        const val GENERIC_45 = "<#FFFFFF>\uE002\uE009"
        const val GENERIC_54 = "<#FFFFFF>\uE002\uE004"

        fun createInventory(size: Int): Inventory {
            when(size) {
                9 -> {
                    return Bukkit.createInventory(null, size, Util.color(GENERIC_9))
                }
                18 -> {
                    return Bukkit.createInventory(null, size, Util.color(GENERIC_18))
                }
                27 -> {
                    return Bukkit.createInventory(null, size, Util.color(GENERIC_27))
                }
                36 -> {
                    return Bukkit.createInventory(null, size, Util.color(GENERIC_36))
                }
                45 -> {
                    return Bukkit.createInventory(null, size, Util.color(GENERIC_45))
                }
                54 -> {
                    return Bukkit.createInventory(null, size, Util.color(GENERIC_54))
                }
                else -> {
                    throw IllegalArgumentException("Invalid slot amount. Use either 9, 18, 27, 36, 45 or 54")
                }
            }
        }
    }
}