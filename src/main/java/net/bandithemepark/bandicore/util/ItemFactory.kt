package net.bandithemepark.bandicore.util

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Field
import java.util.*


class ItemFactory {

    private var itemStack: ItemStack
    private var itemMeta: ItemMeta?

    /**
     * Edit an existing [ItemStack]
     * @param itemStack ItemStack you want to edit
     */
    constructor(itemStack: ItemStack) {
        this.itemStack = itemStack
        itemMeta = itemStack.itemMeta
        setAttributesHidden(true)
    }

    /**
     * Build an ItemStack from Material
     * @param material material for the new [ItemStack]
     */
    constructor(material: Material?) {
        itemStack = ItemStack(material!!)
        itemMeta = itemStack.itemMeta
    }

    /**
     * Set the DisplayName
     * @param name new displayname
     */
    private fun setDisplayName(name: Component?): ItemFactory {
        itemMeta!!.displayName(name)
        itemStack.itemMeta = itemMeta
        return this
    }

    /**
     * Set the amount
     * @param amount new amount of items
     */
    private fun setAmount(amount: Int): ItemFactory {
        itemStack.amount = amount
        return this
    }

    /**
     * Set a new Lore
     * @param lore new lore. Allows "\n"
     */
    private fun setLore(lore: MutableList<Component>): ItemFactory {
        itemMeta!!.lore(lore)
        return this
    }

    /**
     * Change a specific line of the lore.
     * @param line Index of line to change. If this line doesn't exist, spacer Lines will be added.
     * @param lore new lore line. Allows `\n`. Set to `null` if you want the line to be removed.
     */
    private fun setLore(line: Int, lore: Component?): ItemFactory {
        if (lore == null) return removeLore(line)
        val l = if (itemMeta!!.lore() == null) ArrayList() else itemMeta!!.lore()!!
        if (l.size <= line) {
            for (i in 0..line - (l.size - 1)) {
                l.add(Component.space())
            }
        }
        l[line] = lore
        itemMeta!!.lore(l)
        return this
    }

    /**
     * Add a new line to a lore
     * @param lore new lore line
     */
    private fun addLore(lore: Component?): ItemFactory {
        val l = if (itemMeta!!.lore() == null) ArrayList() else itemMeta!!.lore()!!
        return setLore(l.size, lore)
    }

    /**
     * Remove a line from a lore
     * @param line index of line
     */
    private fun removeLore(line: Int): ItemFactory {
        val l = if (itemMeta!!.lore() == null) ArrayList() else itemMeta!!.lore()!!
        if (l.size < line) throw ArrayIndexOutOfBoundsException("The given Line index is bigger than the Lore Index")
        l.removeAt(line)
        itemMeta!!.lore(l)
        return this
    }

    /**
     * Removes the whole lore
     */
    private fun clearLore(): ItemFactory {
        itemMeta!!.lore(null)
        return this
    }

    /**
     * Set the color of a leather armor piece
     * @param color
     */
    private fun setArmorColor(color: Color?): ItemFactory {
        require(itemMeta is LeatherArmorMeta) { "The given ItemStack is not a leather armor" }
        val armorMeta = itemMeta as LeatherArmorMeta
        armorMeta.setColor(color)
        itemMeta = armorMeta
        return this
    }

    /**
     * Set if the Item can break or not
     * @param bool
     */
    private fun setUnbreakable(bool: Boolean): ItemFactory {
        itemMeta!!.isUnbreakable = bool
        return this
    }

    /**
     * Set custom model data
     * @param integer
     */
    private fun setCustomModelData(integer: Int?): ItemFactory {
        itemMeta!!.setCustomModelData(integer)
        return this
    }

    private fun setAttributesHidden(bool: Boolean) {
        if (bool) itemMeta!!.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        else itemMeta!!.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES)
    }

    private fun setSkullTexture(texture: String) {
        require(itemStack.type == Material.PLAYER_HEAD) { "You can not apply a skull texture to a Item that is not a skull" }
        val skullMeta = itemMeta as SkullMeta

        val profile = GameProfile(UUID.randomUUID(), null)
        val encodedData: ByteArray = Base64.getEncoder()
            .encode("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/$texture\"}}}".toByteArray())
        profile.properties.put("textures", Property("textures", String(encodedData)))
        val profileField: Field?
        try {
            profileField = skullMeta.javaClass.getDeclaredField("profile")
            profileField.isAccessible = true
            profileField.set(skullMeta, profile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        itemMeta = skullMeta
    }

    /**
     * Finish the ItemStack
     * @return returns the finished ItemStack
     */
    private fun build(): ItemStack {
        itemStack.itemMeta = itemMeta
        return itemStack
    }

    /**
     * reset the ItemBuilder to a default ItemStack
     * @return a new instance of an ItemBuilder
     */
    fun reset(): ItemFactory {
        return ItemFactory(itemStack.type)
    }

    companion object {
        /**
         * Create a new Instance from an existing ItemStack
         * @param itemStack
         * @return
         */
        @Deprecated("", ReplaceWith("ItemFactory(itemStack)", "net.bandithemepark.bandicore.util.ItemFactory"))
        fun of(itemStack: ItemStack): ItemFactory {
            return ItemFactory(itemStack)
        }

        /**
         * @return
         */
        @Deprecated("")
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

        /**
         * @return
         */
        @Deprecated("")
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

        /**
         * @return
         */
        @Deprecated("")
        fun create(material: Material, name: Component): ItemStack {
            return create(material, 1, 0, name)
        }

        /**
         * @return
         */
        @Deprecated("")
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
}