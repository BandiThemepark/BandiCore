package net.bandithemepark.bandicore.server.essentials.ranks

import net.bandithemepark.bandicore.BandiCore
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachment

class Rank(val id: String, val name: String, val color: String, val textColor: String, val scoreboardName: String, val permissions: HashMap<String, Boolean>) {
    val permissionAttachments = hashMapOf<Player, PermissionAttachment>()

    /**
     * Applies the permission of this rank to a given player
     * @param player The player to apply the permissions to
     */
    fun applyPermissions(player: Player) {
        for ((permission, value) in permissions) {
            if(!permissionAttachments.containsKey(player)) {
                val attachment = player.addAttachment(BandiCore.instance)
                attachment.setPermission(permission, value)
                permissionAttachments[player] = attachment
            } else {
                val attachment = permissionAttachments[player]!!
                attachment.setPermission(permission, value)
            }
        }
    }

    /**
     * Removes the permissions of this rank from a given player
     * @param player The player to remove the permissions from
     */
    fun removePermissions(player: Player) {
        val attachment = permissionAttachments[player]

        if(attachment != null) {
            for (permission in permissions.keys) attachment.unsetPermission(permission)
        }
    }
}