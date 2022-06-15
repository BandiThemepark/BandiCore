package net.bandithemepark.bandicore.park.attractions.tracks

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.segments.*
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.switch.LogFlumePreSwitchSegment
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.switch.LogFlumeSwitchSegment
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.transferone.LogFlumePreTransferSegment
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.transferone.LogFlumeTransferSegment
import net.bandithemepark.bandicore.park.attractions.tracks.commands.*
import net.bandithemepark.bandicore.park.attractions.tracks.editing.TrackEditor
import net.bandithemepark.bandicore.park.attractions.tracks.editing.editors.TrackEditorNode
import net.bandithemepark.bandicore.park.attractions.tracks.editing.editors.TrackEditorRoll
import net.bandithemepark.bandicore.park.attractions.tracks.editing.editors.TrackEditorSegment
import net.bandithemepark.bandicore.park.attractions.tracks.editing.editors.TrackEditorTrigger
import net.bandithemepark.bandicore.park.attractions.tracks.runnables.TrackRunnable
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentSeparator
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentType
import net.bandithemepark.bandicore.park.attractions.tracks.segments.types.TestBrakeSegment
import net.bandithemepark.bandicore.park.attractions.tracks.segments.types.TestLiftHillSegment
import net.bandithemepark.bandicore.park.attractions.tracks.segments.types.TestSegment
import net.bandithemepark.bandicore.park.attractions.tracks.splines.SplineType
import net.bandithemepark.bandicore.park.attractions.tracks.triggers.TrackTrigger
import net.bandithemepark.bandicore.park.attractions.tracks.triggers.TrackTriggerType
import net.bandithemepark.bandicore.park.attractions.tracks.triggers.types.EjectTrigger
import net.bandithemepark.bandicore.park.attractions.tracks.triggers.types.TestTrigger
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicleManager
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.ModelAttachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import net.bandithemepark.bandicore.util.FileManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.Vector

class TrackManager(val splineType: SplineType, val pointsPerMeter: Int, val frictionCoefficient: Double) {
    val loadedTracks = mutableListOf<TrackLayout>()
    val editor = TrackEditor()
    val vehicleManager = TrackVehicleManager()

    fun setup() {
        registerSegments()
        registerTriggers()
        internalSetup()
        registerCommands()
        registerEditors()
        registerAttachments()
    }

    private fun registerSegments() {
        TestSegment().register()
        TestBrakeSegment().register()
        TestLiftHillSegment().register()

        WaterSegment().register()
        LogflumeLiftSegment().register()
        LogflumeDropSegment().register()
        LogflumeStationSegment().register()
        LogFlumeTransferSegment().register()
        LogFlumePreTransferSegment().register()
        LogflumeBackwardsLiftSegment().register()
        LogFlumeSwitchSegment().register()
        LogFlumePreSwitchSegment().register()
    }

    private fun registerTriggers() {
        TestTrigger().register()
        EjectTrigger().register()
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
        TrackEditorRoll().register()
        TrackEditorSegment().register()
        TrackEditorTrigger().register()
    }

    private fun registerAttachments() {
        ModelAttachment().register()
        SeatAttachment().register()
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

    /**
     * Loads a track (only do if the track used is not loaded already)
     * @param id The id of the track to load
     */
    fun loadTrack(id: String) {
        val fm = FileManager()

        val old = getLoadedTrackIds().toMutableList()
        old.add(id)
        fm.getConfig("config.yml").get().set("loadedTracks", old)
        fm.saveConfig("config.yml")

        loadInternally(id)
    }

    /**
     * Unloads a loaded track
     * @param id The id of the track to unload
     */
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

        // Roll nodes
        val rollNodes = mutableListOf<RollNode>()
        if(fm.getConfig("tracks/$id.trck").get().contains("rollNodes")) {
            for(rollNodeId in fm.getConfig("tracks/$id.trck").get().getConfigurationSection("rollNodes")!!.getKeys(false)) {
                val nodeId = fm.getConfig("tracks/$id.trck").get().getString("rollNodes.$rollNodeId.nodeId")
                val position = fm.getConfig("tracks/$id.trck").get().getInt("rollNodes.$rollNodeId.position")
                val roll = fm.getConfig("tracks/$id.trck").get().getDouble("rollNodes.$rollNodeId.roll")
                val node = nodes.find { it.id == nodeId }

                rollNodes.add(RollNode(TrackPosition(node!!, position), roll))
            }
        }

        // Segments
        val segmentSeparators = mutableListOf<SegmentSeparator>()
        if(fm.getConfig("tracks/$id.trck").get().contains("segments")) {
            for(segmentId in fm.getConfig("tracks/$id.trck").get().getConfigurationSection("segments")!!.getKeys(false)) {
                val nodeId = fm.getConfig("tracks/$id.trck").get().getString("segments.$segmentId.nodeId")
                val position = fm.getConfig("tracks/$id.trck").get().getInt("segments.$segmentId.position")
                val node = nodes.find { it.id == nodeId }!!

                val segmentSeparator = SegmentSeparator(TrackPosition(node, position), null)

                if(fm.getConfig("tracks/$id.trck").get().contains("segments.$segmentId.type")) {
                    val typeId = fm.getConfig("tracks/$id.trck").get().getString("segments.$segmentId.type")!!
                    val metadata = fm.getConfig("tracks/$id.trck").get().getStringList("segments.$segmentId.metadata")
                    val type = SegmentType.getNew(typeId, segmentSeparator, metadata)
                    segmentSeparator.type = type
                }

                segmentSeparators.add(segmentSeparator)
            }
        }

        // Triggers
        val triggers = mutableListOf<TrackTrigger>()
        if(fm.getConfig("tracks/$id.trck").get().contains("triggers")) {
            for(triggerId in fm.getConfig("tracks/$id.trck").get().getConfigurationSection("triggers")!!.getKeys(false)) {
                val nodeId = fm.getConfig("tracks/$id.trck").get().getString("triggers.$triggerId.nodeId")
                val position = fm.getConfig("tracks/$id.trck").get().getInt("triggers.$triggerId.position")
                val node = nodes.find { it.id == nodeId }!!

                val trigger = TrackTrigger(TrackPosition(node, position), null)

                val typeId = fm.getConfig("tracks/$id.trck").get().getString("triggers.$triggerId.type")!!
                val metadata = fm.getConfig("tracks/$id.trck").get().getStringList("triggers.$triggerId.metadata")
                val type = TrackTriggerType.getNew(typeId, trigger, metadata)
                trigger.type = type

                triggers.add(trigger)
            }
        }

        // Creating the track
        val track = TrackLayout(id, origin, world!!, nodes, rollNodes, segmentSeparators, triggers)
        loadedTracks.add(track)
    }

    /**
     * Creates a new track
     * @param id The id to name the track to
     * @param origin The origin of the track
     */
    fun createTrack(id: String, origin: Location) {
        val track = TrackLayout(id, origin.toVector(), origin.world!!, mutableListOf(TrackNode("0", 0.0, 0.0, 0.0, false)), mutableListOf(), mutableListOf(), mutableListOf())
        loadedTracks.add(track)

        val fm = FileManager()
        val old = getLoadedTrackIds().toMutableList()
        old.add(id)
        fm.getConfig("config.yml").get().set("loadedTracks", old)
        fm.saveConfig("config.yml")

        track.save()
    }
}