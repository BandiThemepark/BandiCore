package net.bandithemepark.bandicore.server.effects.types

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.effects.EffectType
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.entity.event.SeatExitEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration

class DarkOverlayEffect: EffectType("dark_overlay"), Listener {
    var stayTime = 20
    var fadeInTime = 20
    var fadeOutTime = 20

    override fun loadSettings(json: JsonObject) {
        if(json.has("stay_time")) stayTime = json.get("stay_time").asInt
        if(json.has("fade_in_time")) fadeInTime = json.get("fade_in_time").asInt
        if(json.has("fade_out_time")) fadeOutTime = json.get("fade_out_time").asInt
    }

    var showingFor: List<Player>? = null
    override fun onPlay(players: List<Player>?) {
        if(players == null) return

        if(debug) {
            Util.debug("DarkOverlayEffect", "Playing dark overlay effect for the following players: ${players.joinToString(", ") { it.name }}")
        }

        showingFor = players
        tick = 0
        players.forEach {
            it.showTitle(
                Title.title(
                    Component.text("\uE021"), Util.color(""), Title.Times.times(
                        Duration.ofMillis(fadeInTime * 50L), Duration.ofMillis(stayTime * 50L), Duration.ofMillis(fadeOutTime * 50L))))
        }

        BandiCore.instance.getServer().pluginManager.registerEvents(this, BandiCore.instance)
        active.add(this)
    }

    var tick = 0
    override fun onTick() {
        tick++
    }

    override fun onEffectEnd() {
        if(showingFor == null) return
        if(tick >= (fadeInTime + stayTime)) return

        showingFor!!.forEach {
            it.showTitle(
                Title.title(
                    Component.text("\uE021"), Util.color(""), Title.Times.times(
                        Duration.ofMillis(0L), Duration.ofMillis(0L), Duration.ofMillis(fadeOutTime * 50L))))
        }

        showingFor = null
        SeatExitEvent.getHandlerList().unregister(this)
        active.remove(this)
    }

    @EventHandler
    fun onSeatExit(event: SeatExitEvent) {
        if(showingFor == null) return
        if(tick >= (fadeInTime + stayTime)) return

        if(showingFor!!.contains(event.player)) {
            event.player.showTitle(
                Title.title(
                    Component.text("\uE021"), Util.color(""), Title.Times.times(
                        Duration.ofMillis(0L), Duration.ofMillis(0L), Duration.ofMillis(fadeOutTime * 50L))))
            showingFor = showingFor!!.filter { it != event.player }
        }
    }

    companion object {
        val active = mutableListOf<DarkOverlayEffect>()
    }
}