package net.bandithemepark.bandicore.util

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.material.Colorable
import java.lang.reflect.Field
import java.util.*

object ItemFactory {
    fun create(material: Material, amount: Int, customModelData: Int, name: Component, vararg lores: Component): ItemStack {
        val item = ItemStack(material, amount)
        val meta = item.itemMeta!!
        meta.displayName(name)
        meta.setCustomModelData(customModelData)
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        val lores2 = mutableListOf<Component>()
        lores2.addAll(lores)
        meta.lore(lores2)

        item.itemMeta = meta
        return item
    }

    fun create(material: Material, amount: Int, customModelData: Int, color: Color, name: Component, vararg lores: Component): ItemStack {
        val item = ItemStack(material, amount)
        val meta = item.itemMeta!! as LeatherArmorMeta
        meta.setColor(color)
        meta.displayName(name)
        meta.setCustomModelData(customModelData)
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        val lores2 = mutableListOf<Component>()
        lores2.addAll(lores)
        meta.lore(lores2)

        item.itemMeta = meta
        return item
    }

    fun create(material: Material, name: Component): ItemStack {
        return create(material, 1, 0, name)
    }

    fun create(texture: String, name: Component, vararg lores: Component): ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD, 1)
        val headMeta = head.itemMeta as SkullMeta
        headMeta.displayName(name)
        headMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        val lores2 = mutableListOf<Component>()
        lores2.addAll(lores)
        headMeta.lore(lores2)

        // APPLYING TEXTURE
        val profile = GameProfile(UUID.randomUUID(), null)
        val encodedData: ByteArray = Base64.getEncoder()
            .encode("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/$texture\"}}}".toByteArray())
        profile.properties.put("textures", Property("textures", String(encodedData)))
        val profileField: Field?
        try {
            profileField = headMeta.javaClass.getDeclaredField("profile")
            profileField.isAccessible = true
            profileField.set(headMeta, profile)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        head.itemMeta = headMeta
        return head
    }
}