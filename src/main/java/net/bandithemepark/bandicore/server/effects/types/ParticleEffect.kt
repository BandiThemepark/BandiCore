package net.bandithemepark.bandicore.server.effects.types

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.server.effects.EffectType
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class ParticleEffect: EffectType("particle") {
    var loop = false
    lateinit var particle: Particle
    var interval = 1
    var count = 1
    var duration = 0
    lateinit var location: Location
    var color: Color? = null
    var size = 1
    var spreadRange: Vector? = null
    var velocity = Vector()
    var velocitySpread: Vector? = null
    var blocks: List<Material>? = null

    override fun loadSettings(json: JsonObject) {
        if(json.has("loop")) loop = json.get("loop").asBoolean
        particle = Particle.valueOf(json.get("particle").asString)
        if(json.has("interval")) interval = json.get("interval").asInt
        if(json.has("count")) count = json.get("count").asInt

        val locationJson = json.getAsJsonObject("location")
        val world = locationJson.get("world").asString
        val x = locationJson.get("x").asDouble
        val y = locationJson.get("y").asDouble
        val z = locationJson.get("z").asDouble
        location = Location(Bukkit.getWorld(world), x, y, z)

        if(json.has("color")) {
            val colorJson = json.getAsJsonObject("color")
            val red = colorJson.get("red").asInt
            val green = colorJson.get("green").asInt
            val blue = colorJson.get("blue").asInt
            color = Color.fromRGB(red, green, blue)
        }

        if(json.has("spread_range")) {
            val spreadRangeJson = json.getAsJsonObject("spread_range")
            val spreadX = spreadRangeJson.get("x").asDouble
            val spreadY = spreadRangeJson.get("y").asDouble
            val spreadZ = spreadRangeJson.get("z").asDouble
            spreadRange = Vector(spreadX, spreadY, spreadZ)
        }

        if(json.has("velocity")) {
            val velocityJson = json.getAsJsonObject("velocity")
            val velocityX = velocityJson.get("x").asDouble
            val velocityY = velocityJson.get("y").asDouble
            val velocityZ = velocityJson.get("z").asDouble
            velocity = Vector(velocityX, velocityY, velocityZ)
        }

        if(json.has("velocity_spread")) {
            val velocitySpreadJson = json.getAsJsonObject("velocity_spread")
            val velocitySpreadX = velocitySpreadJson.get("x").asDouble
            val velocitySpreadY = velocitySpreadJson.get("y").asDouble
            val velocitySpreadZ = velocitySpreadJson.get("z").asDouble
            velocitySpread = Vector(velocitySpreadX, velocitySpreadY, velocitySpreadZ)
        }

        if(json.has("blocks")) {
            val blocksJson = json.getAsJsonArray("blocks")
            blocks = blocksJson.map { Material.valueOf(it.asString) }
        }

        if(json.has("size")) size = json.get("size").asInt
    }

    fun showParticles(players: List<Player>?) {
        val toShowTo = players ?: Bukkit.getOnlinePlayers()

        for(i in 0 until count) {
            val spawnLocation = location.clone()
            if(spreadRange != null) {
                spawnLocation.add(
                    Math.random() * spreadRange!!.x - spreadRange!!.x / 2,
                    Math.random() * spreadRange!!.y - spreadRange!!.y / 2,
                    Math.random() * spreadRange!!.z - spreadRange!!.z / 2
                )
            }

            val velocity = velocity.clone()
            if(velocitySpread != null) {
                velocity.add(
                    Vector(
                        Math.random() * velocitySpread!!.x - velocitySpread!!.x / 2,
                        Math.random() * velocitySpread!!.y - velocitySpread!!.y / 2,
                        Math.random() * velocitySpread!!.z - velocitySpread!!.z / 2
                    )
                )
            }

            var dustOptions: Particle.DustOptions? = null
            if(particle === Particle.DUST && color != null) {
                dustOptions = Particle.DustOptions(color!!, size.toFloat())
            }

            if(debug) {
                Util.debug(
                    "ParticleEffect",
                    "Showing particle $particle at $spawnLocation with velocity $velocity"
                )
            }

            toShowTo.forEach { player ->
                if(dustOptions != null) {
                    player.spawnParticle(particle, spawnLocation, 0, velocity.x, velocity.y, velocity.z, dustOptions)
                } else if(blocks != null) {
                    val randomMaterial = blocks!!.random()
                    val itemStack = ItemStack(randomMaterial)
                    player.spawnParticle(Particle.ITEM, spawnLocation, 0, velocity.x, velocity.y, velocity.z, itemStack)
                } else {
                    player.spawnParticle(
                        particle, spawnLocation, 0, velocity.x, velocity.y, velocity.z
                    )
                }
            }
        }
    }

    var playing = false
    var ticks = 0
    override fun onPlay(players: List<Player>?) {
        if(loop) {
            playing = true
            ticks = 0
        } else {
            showParticles(players)
        }
    }

    override fun onTick() {
        if(!playing) return
        ticks++
        if(ticks % interval == 0) showParticles(null)

        if(duration != 0 && ticks >= duration) {
            playing = false
            ticks = 0
        }
    }

    override fun onEffectEnd() {
        playing = false
        ticks = 0
    }
}