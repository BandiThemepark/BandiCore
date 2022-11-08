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
        ServerMode("open", "§6§lBandiThemepark §7- §aOpen||§7Come and visit us right now!", true, true).register()
        ServerMode("vip", "§6§lBandiThemepark §7- §eVIPs only||§7Come and visit us right now! (If you're a VIP)", true, false).register()
        ServerMode("maintenance", "§6§lBandiThemepark §7- §cMaintenance||§7Check our Discord for more information", false, false).register()
        ServerMode("restart", "§6§lBandiThemepark §7- §cRestarting||§7You can still join our queue though!", false, false).register()

        val fm = FileManager()

        // Loading the server mode. It will get the server mode from before a restart if it is present.
        serverMode = if(fm.getConfig("config.yml").get().contains("preRestartMode")) {
            val serverMode = ServerMode.getFromId(fm.getConfig("config.yml").get().getString("preRestartMode")!!)!!
            fm.getConfig("config.yml").get().set("preRestartMode", null)
            fm.getConfig("config.yml").get().set("serverMode", serverMode.id)
            fm.saveConfig("config.yml")

            Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
                BandiCore.instance.mqttConnector.sendMessage("/proxy/mode/trigger", "update")
            }, 40)

            serverMode
        } else {
            ServerMode.getFromId(fm.getConfig("config.yml").get().getString("serverMode")!!)!!
        }

        apiKey = fm.getConfig("config.yml").get().getString("apiKey")!!

        queueServer = fm.getConfig("config.yml").get().getString("queueServer")!!
        queueServerHost = fm.getConfig("config.yml").get().getString("queueServerHost")!!
        queueServerPort = fm.getConfig("config.yml").get().getInt("queueServerPort")

        for(language in fm.getConfig("config.yml").get().getStringList("languages")) {
            languages.add(Language(language.split("-")[0], language.split("-")[1]))
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
        val fm = FileManager()
        fm.getConfig("config.yml").get().set("serverMode", mode.id)
        fm.saveConfig("config.yml")

        BackendSetting("serverMode").setValue(mode.id)
        BackendSetting("motd").setValue(mode.motd)

        BandiCore.instance.mqttConnector.sendMessage("/proxy/mode/trigger", "update")
    }
}