//package net.bandithemepark.bandicore.server.armorstandtools
//
//import me.partypronl.themeparkcore.util.Messages
//import org.bukkit.command.Command
//import org.bukkit.command.CommandExecutor
//import org.bukkit.command.CommandSender
//import org.bukkit.entity.Player
//
//class ArmorStandEditorCommand: CommandExecutor {
//    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
//        if(command.name.equals("ast", true)) {
//            if(sender is Player) {
//                if(sender.hasPermission("phantasiamc.crew")) {
//                    if(ArmorStandEditor.getSession(sender) == null) {
//                        ArmorStandEditor.startSession(sender)
//                        Messages.ARMOR_STAND_EDITOR_STARTED.send(sender)
//                    } else {
//                        ArmorStandEditor.getSession(sender)!!.finishSession()
//                        Messages.ARMOR_STAND_EDITOR_STOPPED.send(sender)
//                    }
//                } else {
//                    Messages.NO_PERMISSIONS.send(sender)
//                }
//            }
//        }
//
//        return false
//    }
//}