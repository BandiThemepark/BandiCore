package net.bandithemepark.bandicore.park.npc

import com.mojang.authlib.properties.Property
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.Util.isAlexSkin
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.UUID

class ThemeParkNPCSkin(val ownerUUID: UUID, val textureProperty: Property, val texture: String, val slim: Boolean) {
    val asCustomPlayerSkin = CustomPlayerSkin(ownerUUID, texture, slim)

    companion object {
        fun getFromPlayer(player: Player): ThemeParkNPCSkin {
            val textureProperty = (player as CraftPlayer).handle.gameProfile.properties.get("textures").iterator().next()
            val texture = CustomPlayerSkin.convertProfileTexture(textureProperty.value)

            return ThemeParkNPCSkin(
                player.uniqueId,
                textureProperty,
                texture,
                player.isAlexSkin()
            )
        }
    }

    class Caching {
        val cache = mutableListOf<ThemeParkNPCSkin>()
        val usedSkins = mutableListOf<ThemeParkNPCSkin>()

        fun getRandomUnusedSkin(): ThemeParkNPCSkin {
            val availableSkins = cache.filter { !usedSkins.contains(it) }

            return if(availableSkins.isNotEmpty()) {
                val skin = availableSkins.random()
                usedSkins.add(skin)
                skin
            } else {
                usedSkins.clear()
                getRandomUnusedSkin()
            }
        }

        fun loadCache() {
            cache.clear()
            val fm = FileManager()

            if(!fm.getConfig("cached-skins.yml").get().contains("skins")) return
            for(textureProperty in fm.getConfig("cached-skins.yml").get().getConfigurationSection("skins")!!.getKeys(false)) {
                val ownerUUID = UUID.fromString(fm.getConfig("cached-skins.yml").get().getString("skins.$textureProperty.ownerUUID"))
                val texture = fm.getConfig("cached-skins.yml").get().getString("skins.$textureProperty.texture")!!
                val slim = fm.getConfig("cached-skins.yml").get().getBoolean("skins.$textureProperty.slim")

                val property = Property("textures", textureProperty)

                cache.add(ThemeParkNPCSkin(ownerUUID, property, texture, slim))
            }
        }

        fun addSkinToCacheAndSave(player: Player) {
            val skin = getFromPlayer(player)
            cache.add(skin)

            val fm = FileManager()
            val textureProperty = skin.textureProperty.value

            fm.getConfig("cached-skins.yml").get().set("skins.$textureProperty.ownerUUID", skin.ownerUUID.toString())
            fm.getConfig("cached-skins.yml").get().set("skins.$textureProperty.texture", skin.texture)
            fm.getConfig("cached-skins.yml").get().set("skins.$textureProperty.slim", skin.slim)
            fm.saveConfig("cached-skins.yml")
        }

        fun isCached(player: Player): Boolean {
            val textureProperty = (player as CraftPlayer).handle.gameProfile.properties.get("textures").iterator().next()
            return cache.any { it.textureProperty.value == textureProperty.value }
        }

        class Events: Listener {
            @EventHandler
            fun onJoin(event: PlayerJoinEvent) {
                if(!BandiCore.instance.server.themePark.themeParkNPCManager.cache.isCached(event.player)) {
                    BandiCore.instance.server.themePark.themeParkNPCManager.cache.addSkinToCacheAndSave(event.player)
                }
            }
        }
    }
}