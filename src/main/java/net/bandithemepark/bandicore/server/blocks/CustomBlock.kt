package net.bandithemepark.bandicore.server.blocks

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.FileManager
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.ItemFactory.Companion.getPersistentData
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class CustomBlock(val id: String, val name: String, val customModelData: Int, val instrument: String, val note: Int, val powered: Boolean) {
    /**
     * Places the block at the given location
     * @param location The location to place the block
     */
    fun placeAt(location: Location) {
        val block = location.world.getBlockAt(location)
        block.setType(Material.NOTE_BLOCK, false)

        val data = block.blockData as NoteBlock
        data.isPowered = powered
        data.instrument = Instrument.valueOf(instrument.uppercase())
        data.note = Note(note)
        block.setBlockData(data, false)

        block.state.update(true, false)
    }

    /**
     * Gets an ItemStack for this block that you can place it with
     * @return The ItemStack
     */
    fun getItemStack(): ItemStack {
        return ItemFactory(Material.PAPER).setDisplayName(Util.color("<!i><#FFFFFF>${name}")).setKeyInPersistentStorage("customblock", id).setCustomModelData(customModelData).build()
    }

    companion object {
        /**
         * Returns a block type from the given ID
         * @param id The ID of the block
         * @return The block type, or null if it doesn't exist
         */
        fun getType(id: String): CustomBlock? {
            return BandiCore.instance.customBlockManager.types.firstOrNull { it.id == id }
        }

        /**
         * Gets the placed custom block instance of a block at a location
         * @param location The location to check
         * @return The placed custom block instance, or null if it doesn't exist
         */
        fun getPlacedAt(location: Location): Placed? {
            return BandiCore.instance.customBlockManager.placed.firstOrNull { it.location == location }
        }
    }

    class Events: Listener {
        val lastPlaces = hashMapOf<Player, Long>()

        @EventHandler
        fun onInteract(event: PlayerInteractEvent) {
            if(event.hand != EquipmentSlot.HAND) return
            if(event.clickedBlock == null) return

            if(event.item != null
                && event.item!!.type == Material.PAPER
                && event.item!!.getPersistentData("customblock") != null
                && event.action == Action.RIGHT_CLICK_BLOCK
                && lastPlaces.getOrDefault(event.player, 0L) < System.currentTimeMillis() - 100) {

                event.setUseInteractedBlock(Event.Result.DENY)

                val toPlaceAt = event.clickedBlock!!.getRelative(event.blockFace).location
                if(toPlaceAt.block.type != Material.AIR) return
                if(event.player.location.block == toPlaceAt.block) return
                if(event.player.location.clone().add(0.0, 1.0, 0.0).block == toPlaceAt.block) return

                lastPlaces[event.player] = System.currentTimeMillis()
                val blockType = getType(event.item!!.getPersistentData("customblock")!!)!!
                event.player.playSound(Sound.sound(Key.key("block.wood.place"), Sound.Source.MASTER, 10F, 1F))
                blockType.placeAt(toPlaceAt)
                BandiCore.instance.customBlockManager.placed.add(Placed(toPlaceAt, blockType))
                BandiCore.instance.customBlockManager.savePlaced()
            } else {
                if(getPlacedAt(event.clickedBlock!!.location) != null && event.action == Action.RIGHT_CLICK_BLOCK) {
                    event.isCancelled = true
                }
            }
        }

        @EventHandler
        fun onBlockBreak(event: BlockBreakEvent) {
            val placed = getPlacedAt(event.block.location)
            if(placed != null) {
                BandiCore.instance.customBlockManager.placed.remove(placed)
                BandiCore.instance.customBlockManager.savePlaced()
            }

            if(event.block.getRelative(BlockFace.UP).type == Material.NOTE_BLOCK) {
                val placedAbove = getPlacedAt(event.block.getRelative(BlockFace.UP).location)

                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable {
                    placedAbove?.type?.placeAt(placedAbove.location)
                })
            }
        }
    }

    class Command: CommandExecutor, TabCompleter {
        override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
            if (!command.name.equals("bandikea", true)) return false
            if(sender !is Player) return false
            if (!sender.hasPermission("bandithemepark.crew")) {
                sender.sendTranslatedMessage(
                    "no-permission",
                    BandiColors.RED.toString()
                )
                return true
            }

            if(args.size == 1) {
                val block = getType(args[0])

                if(block == null) {
                    sender.sendTranslatedMessage("custom-block-not-found", BandiColors.RED.toString())
                } else {
                    sender.inventory.addItem(block.getItemStack())
                    sender.sendTranslatedMessage("custom-block-given", BandiColors.YELLOW.toString(), MessageReplacement("block", block.name))
                }
            } else {
                CustomBlockMenu(sender)
            }

            return false
        }

        override fun onTabComplete(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): MutableList<String>? {
            if(command.name.equals("bandikea", true)) {
                if(sender.hasPermission("bandithemepark.crew")) {
                    if(args.size == 1) {
                        return Util.getTabCompletions(args[0], BandiCore.instance.customBlockManager.types.map { it.id })
                    }
                }
            }
            return null
        }
    }

    class Manager {
        val types = mutableListOf<CustomBlock>()
        val placed = mutableListOf<Placed>()

        init {
            loadTypes()
        }

        fun loadTypes() {
            val fm = FileManager()

            types.clear()
            for(id in fm.getConfig("blocks.yml").get().getConfigurationSection("")!!.getKeys(false)) {
                val name = fm.getConfig("blocks.yml").get().getString("$id.name")!!
                val customModelData = fm.getConfig("blocks.yml").get().getInt("$id.custom-model-data")
                val instrument = fm.getConfig("blocks.yml").get().getString("$id.instrument")!!
                val note = fm.getConfig("blocks.yml").get().getInt("$id.note")
                val powered = fm.getConfig("blocks.yml").get().getBoolean("$id.powered")
                types.add(CustomBlock(id, name, customModelData, instrument, note, powered))
            }
        }

        fun loadPlaced() {
            val fm = FileManager()

            placed.clear()
            if(fm.getConfig("data/placed-blocks.yml").get().contains("blocks")) {
                for (id in fm.getConfig("data/placed-blocks.yml").get().getConfigurationSection("blocks")!!.getKeys(false)) {
                    val world = Bukkit.getWorld(fm.getConfig("data/placed-blocks.yml").get().getString("blocks.$id.world")!!)!!
                    val x = fm.getConfig("data/placed-blocks.yml").get().getDouble("blocks.$id.x")
                    val y = fm.getConfig("data/placed-blocks.yml").get().getDouble("blocks.$id.y")
                    val z = fm.getConfig("data/placed-blocks.yml").get().getDouble("blocks.$id.z")
                    val type = getType(fm.getConfig("data/placed-blocks.yml").get().getString("blocks.$id.type")!!)!!
                    placed.add(Placed(Location(world, x, y, z), type))
                    type.placeAt(Location(world, x, y, z))
                }
            }
        }

        fun savePlaced() {
            val fm = FileManager()

            fm.getConfig("data/placed-blocks.yml").get().set("blocks", "test")
            fm.saveConfig("data/placed-blocks.yml")

            for((index, placed) in placed.withIndex()) {
                fm.getConfig("data/placed-blocks.yml").get().set("blocks.$index.world", placed.location.world.name)
                fm.getConfig("data/placed-blocks.yml").get().set("blocks.$index.x", placed.location.x)
                fm.getConfig("data/placed-blocks.yml").get().set("blocks.$index.y", placed.location.y)
                fm.getConfig("data/placed-blocks.yml").get().set("blocks.$index.z", placed.location.z)
                fm.getConfig("data/placed-blocks.yml").get().set("blocks.$index.type", placed.type.id)
            }
            fm.saveConfig("data/placed-blocks.yml")
        }
    }

    class Placed(val location: Location, val type: CustomBlock) {

    }
}