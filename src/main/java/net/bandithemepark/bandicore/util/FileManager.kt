package net.bandithemepark.bandicore.util

import net.bandithemepark.bandicore.BandiCore
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.*
import java.util.*

class FileManager(private val plugin: JavaPlugin) {
    constructor() :this(BandiCore.instance)

    private val configs = HashMap<String, Config?>()

    /**
     * Get the config by the name(Don't forget the .yml)
     *
     * @param name
     * @return
     */
    fun getConfig(name: String): Config {
        if (!configs.containsKey(name)) configs[name] = Config(name)
        return configs[name]!!
    }

    /**
     * Save the config by the name(Don't forget the .yml)
     *
     * @param name
     * @return
     */
    fun saveConfig(name: String): Config {
        return getConfig(name).save()
    }

    /**
     * Reload the config by the name(Don't forget the .yml)
     *
     * @param name
     * @return
     */
    fun reloadConfig(name: String): Config {
        return getConfig(name).reload()
    }

    inner class Config(private val name: String) {
        private var file: File? = null
        private var config: YamlConfiguration? = null

        /**
         * Saves the config as long as the config isn't empty
         *
         * @return
         */
        fun save(): Config {
            if (config == null || file == null) return this
            try {
                config!!.save(file!!)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            return this
        }

        /**
         * Gets the config as a YamlConfiguration
         *
         * @return
         */
        fun get(): YamlConfiguration {
            if (config == null) reload()
            return config!!
        }

        /**
         * Saves the default config(Will overwrite anything in the current config's file)
         *
         *
         * Don't forget to reload after!
         *
         * @return
         */
        fun saveDefaultConfig(): Config {
            file = File(plugin.dataFolder, name)
            plugin.saveResource(name, false)
            return this
        }

        /**
         * Reloads the config
         *
         * @return
         */
        fun reload(): Config {
            if (file == null) file = File(plugin.dataFolder, name)
            config = YamlConfiguration.loadConfiguration(file!!)
            val defConfigStream: Reader
            try {
                defConfigStream = InputStreamReader(plugin.getResource(name)!!, "UTF8")
                val defConfig = YamlConfiguration.loadConfiguration(defConfigStream)
                config!!.setDefaults(defConfig)
            } catch (_: UnsupportedEncodingException) {
            } catch (_: NullPointerException) {
            }
            return this
        }

        /**
         * Copies the config from the resources to the config's default settings.
         *
         *
         * Force = true ----> Will add any new values from the default file
         *
         *
         * Force = false ---> Will NOT add new values from the default file
         *
         * @param force
         * @return
         */
        fun copyDefaults(force: Boolean): Config {
            get().options().copyDefaults(force)
            return this
        }

        /**
         * An easy way to set a value into the config
         *
         * @param key
         * @param value
         * @return
         */
        operator fun set(key: String?, value: Any?): Config {
            get()[key!!] = value
            return this
        }

        /**
         * An easy way to get a value from the config
         *
         * @param key
         * @return
         */
        operator fun get(key: String?): Any {
            return get()[key!!]!!
        }

    }

}