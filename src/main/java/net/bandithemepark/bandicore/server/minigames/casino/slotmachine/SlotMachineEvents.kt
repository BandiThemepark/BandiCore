package net.bandithemepark.bandicore.server.minigames.casino.slotmachine

import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.Util.getText
import net.bandithemepark.bandicore.util.Util.sendColoredActionBar
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.block.Sign
import org.bukkit.block.sign.Side
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class SlotMachineEvents:Listener {
    @EventHandler
    fun onSignChange(event: SignChangeEvent) {
        val firstLineText = event.line(0)?.getText()
        if(!firstLineText.equals("[slotmachine]", true)) return

        val amount = event.line(1)?.getText()?.toIntOrNull() ?: 0

        event.line(0, Util.color(""))
        event.line(1, Util.color("<b>Slot Machine"))
        event.line(2, Util.color("<i>${amount}"))
        event.line(3, Util.color("Click to play"))
    }

    @EventHandler
    fun onSignClick(event: PlayerInteractEvent) {
        if(event.hand != EquipmentSlot.HAND) return
        if(event.action != Action.RIGHT_CLICK_BLOCK) return
        if(event.clickedBlock?.state !is Sign) return

        val sign = event.clickedBlock?.state as Sign
        if(!sign.getSide(Side.FRONT).line(1).getText().contains("Slot Machine")) return
        event.isCancelled = true

        val amount = sign.getSide(Side.FRONT).line(2).getText().toIntOrNull() ?: 0
        if(amount == 0) return

        // Sign is a wall sign, get block sign is attached to, so behind it
        val originLocation = sign.location.clone().add(sign.location.direction.multiply(1))

        if(SlotMachine.activeOrigins.contains(originLocation)) {
            event.player.sendColoredActionBar("<${BandiColors.RED}>Slot machine is already in use")
            return
        }

        val slotMachine = SlotMachine(amount, originLocation)
        slotMachine.play(event.player)
    }
}