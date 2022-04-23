package net.bandithemepark.bandicore.park.attractions.tracks

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.attractions.tracks.commands.*
import net.bandithemepark.bandicore.park.attractions.tracks.editing.TrackEditor
import net.bandithemepark.bandicore.park.attractions.tracks.editing.editors.TrackEditorNode
import net.bandithemepark.bandicore.park.attractions.tracks.runnables.TrackRunnable
import net.bandithemepark.bandicore.park.attractions.tracks.splines.SplineType
import net.bandithemepark.bandicore.util.FileManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.Vector

class TrackManager(val splineType: SplineType, val pointsPerMeter: Int, val frictionCoefficient: Double) {
    val loadedTracks = mutableListOf<TrackLayout>()
    val editor = TrackEditor()
    // TODO Vehicle manager

    fun setup() {
        registerSegments()
        registerTriggers()
        internalSetup()
        registerCommands()
        registerEditors()
        registerAttachments()
    }

    private fun registerSegments() {

    }

    private fun registerTriggers() {

    }

    private fun registerCommands() {
        TrackCommandHelp().register()
        TrackCommandList().register()
        TrackCommandCreate().register()
        TrackCommandLoad().register()
        TrackCommandUnload().register()
        TrackCommandEdit().register()
    }

    private fun registerEditors() {
        TrackEditorNode().register()
        BandiCore.instance.getServer().pluginManager.registerEvents(TrackEditorNode.Events(), BandiCore.instance)
    }

    private fun registerAttachments() {

    }

    private fun internalSetup() {
        loadTracks()
        TrackRunnable().runTaskTimerAsynchronously(BandiCore.instance, 0, 1)
    }

    private fun getLoadedTrackIds(): List<String> {
        return FileManager().getConfig("config.yml").get().getStringList("loadedTracks")
    }

    private fun loadTracks() {
        getLoadedTrackIds().forEach { loadInternally(it) }
    }

    fun loadTrack(id: String) {
        val fm = FileManager()

        val old = getLoadedTrackIds().toMutableList()
        old.add(id)
        fm.getConfig("config.yml").get().set("loadedTracks", old)
        fm.saveConfig("config.yml")

        loadInternally(id)
    }

    fun unloadTrack(id: String) {
        val fm = FileManager()

        val old = getLoadedTrackIds().toMutableList()
        old.remove(id)
        fm.getConfig("config.yml").get().set("loadedTracks", old)
        fm.saveConfig("config.yml")

        loadedTracks.removeIf { it.id == id }
    }

    private fun loadInternally(id: String) {
        // Getting the default stuff
        val fm = FileManager()
        val world = Bukkit.getWorld(fm.getConfig("tracks/$id.trck").get().getString("world")!!)

        // Origin
        val originX = fm.getConfig("tracks/$id.trck").get().getDouble("origin.x")
        val originY = fm.getConfig("tracks/$id.trck").get().getDouble("origin.y")
        val originZ = fm.getConfig("tracks/$id.trck").get().getDouble("origin.z")
        val origin = Vector(originX, originY, originZ)

        // Track Nodes
        val nodes = mutableListOf<TrackNode>()
        for(nodeId in fm.getConfig("tracks/$id.trck").get().getConfigurationSection("nodes")!!.getKeys(false)) {
            val x = fm.getConfig("tracks/$id.trck").get().getDouble("nodes.$nodeId.x")
            val y = fm.getConfig("tracks/$id.trck").get().getDouble("nodes.$nodeId.y")
            val z = fm.getConfig("tracks/$id.trck").get().getDouble("nodes.$nodeId.z")
            val strict = fm.getConfig("tracks/$id.trck").get().getBoolean("nodes.$nodeId.strict")
            val connectedTo = fm.getConfig("tracks/$id.trck").get().getString("nodes.$nodeId.connectedTo")
            nodes.add(TrackNode(nodeId, x, y, z, strict, connectedTo))
        }

        val rollNodes = mutableListOf<RollNode>()
        if(fm.getConfig("tracks/$id.trck").get().contains("rollNodes")) {
            for(rollNodeId in fm.getConfig("tracks/$id.trck").get().getConfigurationSection("rollNodes")!!.getKeys(false)) {
                val nodeId = fm.getConfig("tracks/$id.trck").get().getString("rollNodes.$rollNodeId.nodeId")
                val position = fm.getConfig("tracks/$id.trck").get().getInt("rollNodes.$rollNodeId.position")
                val roll = fm.getConfig("tracks/$id.trck").get().getDouble("rollNodes.$rollNodeId.roll")
                val node = nodes.find { it.id == nodeId }

                rollNodes.add(RollNode(node!!, position, roll))
            }
        }

        // TODO Load segment separators, triggers

        // Creating the track
        val track = TrackLayout(id, origin, world!!, nodes, rollNodes)
        loadedTracks.add(track)
    }

    fun createTrack(id: String, origin: Location) {
        val track = TrackLayout(id, origin.toVector(), origin.world!!, mutableListOf(TrackNode("0", 0.0, 0.0, 0.0, false)), mutableListOf())
        loadedTracks.add(track)

        val fm = FileManager()
        val old = getLoadedTrackIds().toMutableList()
        old.add(id)
        fm.getConfig("config.yml").get().set("loadedTracks", old)
        fm.saveConfig("config.yml")

        track.save()
    }
}