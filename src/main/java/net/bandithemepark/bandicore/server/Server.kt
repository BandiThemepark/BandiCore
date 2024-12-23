package net.bandithemepark.bandicore.server

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.network.backend.BackendSetting
import net.bandithemepark.bandicore.park.ThemePark
import net.bandithemepark.bandicore.park.attractions.ridecounter.RidecounterManager
import net.bandithemepark.bandicore.server.achievements.AchievementManager
import net.bandithemepark.bandicore.server.animation.rig.RigManager
import net.bandithemepark.bandicore.server.custom.player.animation.CustomPlayerAnimationManager
import net.bandithemepark.bandicore.server.essentials.ranks.RankManager
import net.bandithemepark.bandicore.server.essentials.ranks.scoreboard.BandiScoreboard
import net.bandithemepark.bandicore.server.essentials.warps.WarpManager
import net.bandithemepark.bandicore.server.mode.ServerMode
import net.bandithemepark.bandicore.server.translations.Language
import net.bandithemepark.bandicore.util.FileManager
import org.bukkit.Bukkit

class Server {
    var serverMode: ServerMode
    val languages = mutableListOf<Language>()
    val apiKey: String

    val queueServer: String
    val queueServerHost: String
    val queueServerPort: Int

    val rankManager = RankManager()
    val scoreboard = BandiScoreboard()
    val warpManager = WarpManager()
    val customPlayerAnimationManager = CustomPlayerAnimationManager()
    val rigManager = RigManager()
    val ridecounterManager = RidecounterManager()
    val achievementManager = AchievementManager()

    val themePark = ThemePark(Bukkit.getWorld("world")!!)

    init {
        ServerMode("open", "          <color:#ffc738><b>BandiThemepark</b></color> <color:#404040>-</color> <color:#77aa61>We are open!</color><br>               <color:#94a9b3>Come and visit us right now!</color>", true, true).register()
        ServerMode("vip", "        <color:#ffc738><b>BandiThemepark</b></color> <color:#404040>-</color> <color:#ffe37d>Only open for VIPs</color><br>          <color:#94a9b3>Go and visit shop.bandithemepark.net</color>", true, false).register()
        ServerMode("maintenance", "     <color:#ffc738><b>BandiThemepark</b></color> <color:#404040>-</color> <color:#ff5b3b>Closed for maintenance</color><br>             <color:#94a9b3>Check our Discord for updates!</color>", false, false).register()
        ServerMode("restart", "       <color:#ffc738><b>BandiThemepark</b></color> <color:#404040>-</color> <color:#5789ff>We are restarting...</color><br>         <color:#94a9b3>Join our queue to be the first one in!</color>", false, false).register()

        // Loading the server mode. It will get the server mode from before a restart if it is present.
        serverMode = if(BandiCore.instance.config.json.has("preRestartMode")) {
            val serverMode = ServerMode.getFromId(BandiCore.instance.config.json.get("preRestartMode").asString)!!
            BandiCore.instance.config.json.remove("preRestartMode")
            BandiCore.instance.config.json.addProperty("serverMode", serverMode.id)
            BandiCore.instance.config.save()

            Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
                BandiCore.instance.mqttConnector.sendMessage("/proxy/mode/trigger", "update")
            }, 40)

            serverMode
        } else {
            ServerMode.getFromId(BandiCore.instance.config.json.get("serverMode").asString)!!
        }

        apiKey = BandiCore.instance.config.json.get("apiKey").asString

        queueServer = BandiCore.instance.config.json.get("queueServer").asString
        queueServerHost = BandiCore.instance.config.json.get("queueServerHost").asString
        queueServerPort = BandiCore.instance.config.json.get("queueServerPort").asInt

        for(language in BandiCore.instance.config.json.getAsJsonArray("languages")) {
            languages.add(Language(language.asString.split("-")[0], language.asString.split("-")[1]))
        }
    }

    /**
     * Gets a language from an id
     * @param id The id of the language
     * @return The language, null if not found or loaded
     */
    fun getLanguage(id: String): Language? {
        return languages.find { it.id == id }
    }

    /**
     * Gets a language from a language code (ShortenedId). This is the format that is stored in the database
     * @param shortenedId The language code
     * @return The language, null if not found or loaded
     */
    fun getShortenedLanguage(shortenedId: String): Language? {
        return languages.find { it.shortenedId == shortenedId }
    }

    /**
     * Changes the server mode
     * @param mode The new server mode
     */
    fun changeServerMode(mode: ServerMode) {
        serverMode = mode
        BandiCore.instance.config.json.addProperty("serverMode", mode.id)
        BandiCore.instance.config.save()

        if(!BandiCore.instance.devMode) {
            BackendSetting("serverMode").setValue(mode.id)
            BackendSetting("motd").setValue(mode.motd)
        }

        BandiCore.instance.mqttConnector.sendMessage("/proxy/mode/trigger", "update")
    }
}