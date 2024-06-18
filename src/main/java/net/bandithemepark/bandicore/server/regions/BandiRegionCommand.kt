package net.bandithemepark.bandicore.server.regions

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.regions.Polygonal2DRegion
import com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedMessage
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class BandiRegionCommand: CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!command.name.equals("region", true)) return false
        if (!sender.hasPermission("bandithemepark.crew")) {
            sender.sendTranslatedMessage("no-permission", BandiColors.RED.toString())
            return true
        }

        if(args.size == 1) {
            if(args[0].equals("list", true)) {
                sender.sendTranslatedMessage("region-command-list", BandiColors.YELLOW.toString(), MessageReplacement("regions", BandiCore.instance.regionManager.regions.joinToString(", ") { it.name }))
            } else {
                sendHelp(sender)
            }
        } else if(args.size == 2) {
            if(args[0].equals("create", true)) {
                val region = BandiCore.instance.regionManager.getFromId(args[1])

                if(region == null) {
                    BandiCore.instance.regionManager.createNew(args[1]) {
                        sender.sendTranslatedMessage("region-command-created", BandiColors.YELLOW.toString(), MessageReplacement("region", args[1]))
                    }
                } else {
                    sender.sendTranslatedMessage("region-command-region-already-exists", BandiColors.RED.toString(), MessageReplacement("region", args[1]))
                }
            } else if(args[0].equals("info", true)) {
                val region = BandiCore.instance.regionManager.getFromId(args[1])

                if(region != null) {
                    sender.sendTranslatedMessage("region-command-info-header", BandiColors.YELLOW.toString(), MessageReplacement("region", region.name))
                    sender.sendTranslatedMessage("region-command-info-displayname", BandiColors.YELLOW.toString(), MessageReplacement("displayname", region.displayName))
                    sender.sendTranslatedMessage("region-command-info-priority", BandiColors.YELLOW.toString(), MessageReplacement("priority", region.priority.toString()))
                    sender.sendTranslatedMessage("region-command-info-areas", BandiColors.YELLOW.toString(), MessageReplacement("areas", region.areas.size.toString()))
                } else {
                    sender.sendTranslatedMessage("region-command-region-does-not-exist", BandiColors.RED.toString(), MessageReplacement("region", args[1]))
                }
            } else if(args[0].equals("addarea", true)) {
                val region = BandiCore.instance.regionManager.getFromId(args[1])

                if(region != null) {
                    addRegionArea(sender, region)
                } else {
                    sender.sendTranslatedMessage("region-command-region-does-not-exist", BandiColors.RED.toString(), MessageReplacement("region", args[1]))
                }
            } else if(args[0].equals("delete", true)) {
                val region = BandiCore.instance.regionManager.getFromId(args[1])

                if(region != null) {
                    BandiCore.instance.regionManager.deleteRegion(region) {
                        sender.sendTranslatedMessage("region-command-deleted", BandiColors.YELLOW.toString(), MessageReplacement("region", args[1]))
                    }
                } else {
                    sender.sendTranslatedMessage("region-command-region-does-not-exist", BandiColors.RED.toString(), MessageReplacement("region", args[1]))
                }
            } else {
                sendHelp(sender)
            }
        } else if(args.size == 3) {
            if(args[0].equals("removearea", true)) {
                val region = BandiCore.instance.regionManager.getFromId(args[1])

                if(region != null) {
                    try {
                        val index = args[2].toInt()
                        removeRegionArea(sender, region, index)
                    } catch (ex: NumberFormatException) {
                        sender.sendTranslatedMessage("not-a-number", BandiColors.RED.toString())
                    }
                } else {
                    sender.sendTranslatedMessage("region-command-region-does-not-exist", BandiColors.RED.toString(), MessageReplacement("region", args[1]))
                }
            } else if(args[0].equals("setarea", true)) {
                val region = BandiCore.instance.regionManager.getFromId(args[1])

                if(region != null) {
                    try {
                        val index = args[2].toInt()
                        setRegionArea(sender, region, index)
                    } catch (ex: NumberFormatException) {
                        sender.sendTranslatedMessage("not-a-number", BandiColors.RED.toString())
                    }
                } else {
                    sender.sendTranslatedMessage("region-command-region-does-not-exist", BandiColors.RED.toString(), MessageReplacement("region", args[1]))
                }
            } else if(args[0].equals("selectarea", true)) {
                val region = BandiCore.instance.regionManager.getFromId(args[1])

                if(region != null) {
                    try {
                        val index = args[2].toInt()
                        selectRegionArea(sender, region, index)
                    } catch (ex: NumberFormatException) {
                        sender.sendTranslatedMessage("not-a-number", BandiColors.RED.toString())
                    }
                } else {
                    sender.sendTranslatedMessage("region-command-region-does-not-exist", BandiColors.RED.toString(), MessageReplacement("region", args[1]))
                }
            } else if(args[0].equals("setpriority", true)) {
                val region = BandiCore.instance.regionManager.getFromId(args[1])

                if(region != null) {
                    try {
                        val index = args[2].toInt()
                        region.priority = index
                        BandiCore.instance.regionManager.saveRegion(region) {
                            sender.sendTranslatedMessage("region-command-priority-set", BandiColors.YELLOW.toString(), MessageReplacement("region", region.name), MessageReplacement("priority", region.priority.toString()))
                        }
                    } catch (ex: NumberFormatException) {
                        sender.sendTranslatedMessage("not-a-number", BandiColors.RED.toString())
                    }
                } else {
                    sender.sendTranslatedMessage("region-command-region-does-not-exist", BandiColors.RED.toString(), MessageReplacement("region", args[1]))
                }
            } else if(args[0].equals("settext", true)) {
                val region = BandiCore.instance.regionManager.getFromId(args[1])

                if(region != null) {
                    val text = args.drop(2).joinToString(" ")
                    region.displayName = text
                    BandiCore.instance.regionManager.saveRegion(region) {
                        sender.sendTranslatedMessage("region-command-text-set", BandiColors.YELLOW.toString(), MessageReplacement("region", region.name), MessageReplacement("text", text))
                    }
                } else {
                    sender.sendTranslatedMessage("region-command-region-does-not-exist", BandiColors.RED.toString(), MessageReplacement("region", args[1]))
                }
            } else {
                sendHelp(sender)
            }
        } else {
            if(args.size > 2 && args[0].equals("settext", true)) {
                val region = BandiCore.instance.regionManager.getFromId(args[1])

                if(region != null) {
                    val text = args.drop(2).joinToString(" ")
                    region.displayName = text
                    BandiCore.instance.regionManager.saveRegion(region) {
                        sender.sendTranslatedMessage("region-command-text-set", BandiColors.YELLOW.toString(), MessageReplacement("region", region.name), MessageReplacement("text", text))
                    }
                } else {
                    sender.sendTranslatedMessage("region-command-region-does-not-exist", BandiColors.RED.toString(), MessageReplacement("region", args[1]))
                }
            } else {
                sendHelp(sender)
            }
        }

        return false
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage(Util.color("<${BandiColors.RED}>/region help"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/region list"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/region create <id>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/region info <id>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/region addarea <id>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/region delete <id>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/region removearea <id> <index>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/region setarea <id> <index>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/region selectarea <id> <index>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/region settext <id> <text>"))
        sender.sendMessage(Util.color("<${BandiColors.RED}>/region setpriority <id> <priority>"))
    }

    private fun addRegionArea(sender: CommandSender, region: BandiRegion) {
        if(sender !is Player) {
            sender.sendTranslatedMessage("not-a-player", BandiColors.RED.toString())
            return
        }

        val worldEdit = WorldEdit.getInstance()
        val session = worldEdit.sessionManager.get(BukkitAdapter.adapt(sender))

        if(session == null || session.selectionWorld == null) {
            sender.sendTranslatedMessage("region-command-no-selection", BandiColors.RED.toString())
            return
        }

        val selectedRegion = session.getRegionSelector(BukkitAdapter.adapt(sender.world)).region

        if(selectedRegion !is Polygonal2DRegion) {
            sender.sendTranslatedMessage("region-command-not-a-polygonal-region", BandiColors.RED.toString())
            return
        }

        region.areas.add(selectedRegion)
        BandiCore.instance.regionManager.saveRegion(region) {
            sender.sendTranslatedMessage("region-command-area-added", BandiColors.YELLOW.toString(), MessageReplacement("region", region.name))
        }
    }

    private fun removeRegionArea(sender: CommandSender, region: BandiRegion, index: Int) {
        if(region.areas.size > index) {
            sender.sendTranslatedMessage("region-command-area-does-not-exist", BandiColors.RED.toString(), MessageReplacement("region", region.name), MessageReplacement("index", index.toString()))
            return
        }

        region.areas.removeAt(index-1)
        BandiCore.instance.regionManager.saveRegion(region) {
            sender.sendTranslatedMessage("region-command-area-removed", BandiColors.YELLOW.toString(), MessageReplacement("region", region.name), MessageReplacement("index", index.toString()))
        }
    }

    private fun setRegionArea(sender: CommandSender, region: BandiRegion, index: Int) {
        if(sender !is Player) {
            sender.sendTranslatedMessage("not-a-player", BandiColors.RED.toString())
            return
        }

        val worldEdit = WorldEdit.getInstance()
        val session = worldEdit.sessionManager.get(BukkitAdapter.adapt(sender))

        if(session == null || session.selectionWorld == null) {
            sender.sendTranslatedMessage("region-command-no-selection", BandiColors.RED.toString())
            return
        }

        val selectedRegion = session.getRegionSelector(BukkitAdapter.adapt(sender.world)).region

        if(selectedRegion !is Polygonal2DRegion) {
            sender.sendTranslatedMessage("region-command-not-a-polygonal-region", BandiColors.RED.toString())
            return
        }

        if(region.areas.size > index) {
            sender.sendTranslatedMessage("region-command-area-does-not-exist", BandiColors.RED.toString(), MessageReplacement("region", region.name), MessageReplacement("index", index.toString()))
            return
        }

        region.areas[index-1] = selectedRegion
        BandiCore.instance.regionManager.saveRegion(region) {
            sender.sendTranslatedMessage("region-command-area-set", BandiColors.YELLOW.toString(), MessageReplacement("region", region.name), MessageReplacement("index", index.toString()))
        }
    }

    private fun selectRegionArea(sender: CommandSender, region: BandiRegion, index: Int) {
        if(sender !is Player) {
            sender.sendTranslatedMessage("not-a-player", BandiColors.RED.toString())
            return
        }

        if(region.areas.size > index) {
            sender.sendTranslatedMessage("region-command-area-does-not-exist", BandiColors.RED.toString(), MessageReplacement("region", region.name), MessageReplacement("index", index.toString()))
            return
        }

        val area = region.areas[index-1]
        val worldEdit = WorldEdit.getInstance()
        val session = worldEdit.sessionManager.get(BukkitAdapter.adapt(sender))
        val selector = Polygonal2DRegionSelector(area.world, area.points, area.minimumPoint.y, area.maximumPoint.y)
        session.setRegionSelector(area.world, selector)

        sender.sendTranslatedMessage("region-command-area-selected", BandiColors.YELLOW.toString(), MessageReplacement("region", region.name), MessageReplacement("index", index.toString()))
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        if(!command.name.equals("region", true)) return null

        if(args.size == 1) {
            return Util.getTabCompletions(args[0], listOf("help", "list", "create", "info", "addarea", "delete", "removearea", "setarea", "selectarea", "settext", "setpriority"))
        } else if(args.size == 2) {
            val list = listOf("help", "list", "create")
            if(!list.contains(args[0].lowercase())) {
                return Util.getTabCompletions(args[1], BandiCore.instance.regionManager.getAllIds())
            }
        } else if(args.size == 3) {
            val list = listOf("setarea", "selectarea", "removearea")
            if(list.contains(args[0].lowercase())) {
                val region = BandiCore.instance.regionManager.getFromId(args[1])
                if(region != null) {
                    return Util.getTabCompletions(args[2], List(region.areas.size) { index -> "${index+1}" })
                }
            }
        }
        
        return null
    }
}