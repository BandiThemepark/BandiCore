package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop.RupsbaanRideOP
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop.*
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop.transfer.CanCanStorageRetrieveButton
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop.transfer.CanCanStorageSendButton
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop.transfer.CanCanTransferModeButton
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.segments.CanCanFinalBrakeSegment
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.segments.CanCanLiftSegment
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.segments.CanCanStationSegment
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.segments.CanCanStorageSegment
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOPPage
import net.bandithemepark.bandicore.park.attractions.rideop.events.RideOperateEvent
import net.bandithemepark.bandicore.park.attractions.rideop.events.RideStopOperatingEvent
import net.bandithemepark.bandicore.park.attractions.rideop.util.pages.RideOPCameraPage
import net.bandithemepark.bandicore.park.attractions.rideop.util.pages.RideOPHomePage
import net.bandithemepark.bandicore.park.attractions.rideop.util.pages.RideOPStoragePage
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.park.attractions.tracks.segments.SegmentSeparator
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.TrackVehicle
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.HarnessAttachment
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.debug.Debuggable
import net.bandithemepark.bandicore.util.track.TrackForkMerge
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CanCanRideOP: RideOP("cancancoaster", "cancanstation", Location(Bukkit.getWorld("world"), -161.0, -12.2, -30.5, -90f, -90f)) {
    val track = BandiCore.instance.trackManager.loadedTracks.find { it.id == "cancan" }!!
    val stationSegment = track.segmentSeparators.find { it.type is CanCanStationSegment }!!
    val liftSegment = track.segmentSeparators.find { it.type!! is CanCanLiftSegment }!!
    val storageSegments = track.segmentSeparators.filter { it.type is CanCanStorageSegment }

    val gatesButton = CanCanGatesButton()
    val harnessButton = CanCanHarnessButton()
    val pushDownAllButton = CanCanPushDownAllButton()

    val automaticRideOP = CanCanAutomaticRideOP(this)

    var transferMode = false
    var transferState = TransferState.NONE
    val storageFork = TrackForkMerge(track, listOf(
        track.nodes.find { it.id == "252" }!!,
        track.nodes.find { it.id == "251" }!!,
    ), track.nodes.find { it.id == "253" }!!)
    val transferFork = TrackForkMerge(track, listOf(
        track.nodes.find { it.id == "3" }!!,
        track.nodes.find { it.id == "255" }!!,
    ), track.nodes.find { it.id == "4" }!!)
    var dispatchedFromFinal = false

    override fun getPages(): List<RideOPPage> {
        return listOf(
            RideOPHomePage(listOf(
                gatesButton,
                harnessButton,
                pushDownAllButton,
                CanCanIndicator(),
                CanCanDispatchButton(),
                CanCanEStop(),
            )),
            RideOPCameraPage(listOf(

            )),
            RideOPStoragePage(listOf(
                CanCanTransferModeButton(),
                CanCanStorageSendButton(2, 0),
                CanCanStorageSendButton(3, 1),
                CanCanStorageRetrieveButton(5, 0),
                CanCanStorageRetrieveButton(6, 1)
            ))
        )
    }

    override fun onTick() {

    }

    override fun onSecond() {
        automaticRideOP.update()
    }

    override fun onServerStart() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
            BandiCore.instance.trackManager.vehicleManager.loadTrain("cancan", track, TrackPosition(track.nodes.find { it.id == "5" }!!, 0), 8.0)
            BandiCore.instance.trackManager.vehicleManager.loadTrain("cancan", track, TrackPosition(track.nodes.find { it.id == "0" }!!, 0), 8.0)
            BandiCore.instance.trackManager.vehicleManager.loadTrain("cancan", track, TrackPosition(track.nodes.find { it.id == "182" }!!, 0), 8.0)
            BandiCore.instance.trackManager.vehicleManager.loadTrain("cancan", track, TrackPosition(track.nodes.find { it.id == "120" }!!, 0), 8.0)
            BandiCore.instance.trackManager.vehicleManager.loadTrain("cancan", track, TrackPosition(track.nodes.find { it.id == "62" }!!, 0), 8.0)

        }, 10)

//        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
//            BandiCore.instance.trackManager.vehicleManager.loadTrain("cancan", track, TrackPosition(track.nodes.find { it.id == "24" }!!, 0), 8.0)
//        }, 20)
    }

    override fun onServerStop() {

    }

    fun isTrainInStation(): Boolean {
        if(stationSegment.vehicles.isEmpty()) return false
        if(!(stationSegment.type as CanCanStationSegment).pastMiddle) return false
        if((stationSegment.type as CanCanStationSegment).dispatched) return false
        return true
    }

    fun getCurrentTrain(): TrackVehicle? {
        return stationSegment.vehicles.firstOrNull()
    }

    fun getPlayersInStation(): List<Player> {
        if(getCurrentTrain() == null) return listOf()
        return getCurrentTrain()!!.getPlayerPassengers()
    }

    fun canDispatch(): Boolean {
        if(!isTrainInStation()) return false
        if(!(liftSegment.type as CanCanLiftSegment).available) return false
        if(track.eStop) return false
        if(harnessButton.open) return false
        if(gatesButton.open) return false

        for(harness in getAllHarnesses()) {
            if(harness.harnessPosition != 0.0) return false
        }

        return true
    }

    var lastDispatch = System.currentTimeMillis()
    fun dispatch() {
        lastDispatch = System.currentTimeMillis()
        (stationSegment.type as CanCanStationSegment).dispatch()
    }

    fun getPlayerPassengers(): List<Player> {
        val passengers = mutableListOf<Player>()

        for(vehicle in track.getVehicles()) {
            for(attachment in vehicle.getAllAttachments().filter { it.type is SeatAttachment }) {
                passengers.addAll((attachment.type as SeatAttachment).seat!!.getPassengers().filterIsInstance<Player>())
            }
        }

        return passengers
    }

    fun canSendIntoStorage(id: Int): Boolean {
        if(!transferMode) return false
        if(dispatchedFromFinal) return false
        if(transferState != TransferState.NONE) return false
        if(!isTrainInStation()) return false

        val storageSegment = getStorageSegment(id)
        if(storageSegment.vehicles.isNotEmpty()) return false

        return true
    }

    fun canRetrieveFromStorage(id: Int): Boolean {
        if(!transferMode) return false
        if(dispatchedFromFinal) return false
        if(transferState != TransferState.NONE) return false
        if(isTrainInStation()) return false

        val storageSegment = getStorageSegment(id)
        if(storageSegment.vehicles.isEmpty()) return false

        return true
    }

    fun sendIntoStorage(id: Int) {
        transferFork.switchTo(1)
        storageFork.switchTo(id)
        (stationSegment.type as CanCanStationSegment).sendBackwards()
        transferState = TransferState.SENDING
    }

    fun getStorageSegment(id: Int): SegmentSeparator {
        return storageSegments.find { it.type!!.metadata[0].toInt() == id }!!
    }

    fun retrieveFromStorage(id: Int) {
        transferFork.switchTo(1)
        storageFork.switchTo(id)
        (getStorageSegment(id).type!! as CanCanStorageSegment).retrieve()
        transferState = TransferState.RETRIEVING
    }

    fun resetTransferState() {
        transferState = TransferState.NONE
        transferFork.switchTo(0)
        updateMenu()
    }

    fun canEnableTransferMode(): Boolean {
        if(getPlayersInStation().isNotEmpty()) return false

        return true
    }

    fun canDisableTransferMode(): Boolean {
        if(transferState != TransferState.NONE) return false

        return true
    }

    fun enableTransferMode() {
        transferMode = true
        transferState = TransferState.NONE

        if(isTrainInStation()) {
            harnessesLocked = true
            updateLockedState()
            for(harness in getAllHarnesses()) {
                if(harness.harnessPosition != 0.0 && harness.currentProgress >= 30) {
                    harness.startDownwardsInterpolation()
                }
            }
        }

        updateMenu()
    }

    fun disableTransferMode() {
        transferMode = false
        transferState = TransferState.NONE
        transferFork.switchTo(0)
        updateMenu()
    }

    fun getAmountOfTrainsOnTrack(): Int {
        return track.getVehicles().size - storageSegments.filter { it.vehicles.isNotEmpty() }.size
    }

    var harnessesLocked = true
    fun updateLockedState() {
        getAllHarnesses().forEach {
            it.harnessesLocked = harnessesLocked
        }
    }

    fun getAllHarnesses(): List<HarnessAttachment> {
        return getCurrentTrain()?.getAllAttachments()?.filter { it.type is HarnessAttachment }?.map { it.type as HarnessAttachment } ?: listOf()
    }

    enum class TransferState {
        SENDING, RETRIEVING, NONE
    }

    class Events: Listener {
        @EventHandler
        fun onOperate(event: RideOperateEvent) {
            if(event.rideOP.id != "cancancoaster") return

            val canCanRideOP = event.rideOP as CanCanRideOP
            if(!canCanRideOP.harnessesLocked) return

            canCanRideOP.getAllHarnesses().forEach {
                if(it.spawned) it.markFor(event.player)
            }
        }

        @EventHandler
        fun onStopOperating(event: RideStopOperatingEvent) {
            if(event.rideOP.id != "cancancoaster") return

            val canCanRideOP = event.rideOP as CanCanRideOP
            if(!canCanRideOP.harnessesLocked) return

            canCanRideOP.getAllHarnesses().forEach {
                if(it.spawned) it.unMarkFor(event.player)
            }
        }
    }

    class Debug: Debuggable {
        override fun debug(sender: CommandSender) {
            val rideOP: CanCanRideOP = get("cancancoaster")!! as CanCanRideOP
            val stationSegment = rideOP.stationSegment.type as CanCanStationSegment
            val finalBrakeSegment = rideOP.track.segmentSeparators.find { it.type is CanCanFinalBrakeSegment }!!.type as CanCanFinalBrakeSegment

            sender.sendMessage(Util.color("<${BandiColors.YELLOW}>CanCan Coaster debugging"))
            sender.sendMessage(Util.color("Transfer mode: ${rideOP.transferMode}"))
            sender.sendMessage(Util.color("Transfer state: ${rideOP.transferState}"))
            sender.sendMessage(Util.color("Train in station: ${rideOP.isTrainInStation()}"))
            sender.sendMessage(Util.color("Station currentTrain: ${stationSegment.currentVehicle?.id}"))
            sender.sendMessage(Util.color("Station dispatched: ${stationSegment.dispatched}"))
            sender.sendMessage(Util.color("Station amount of vehicles: ${stationSegment.parent.vehicles.size}"))
            sender.sendMessage(Util.color("FinalBrake should stop: ${finalBrakeSegment.shouldStop}"))
            sender.sendMessage(Util.color("FinalBrake stopped: ${finalBrakeSegment.stopped}"))
            sender.sendMessage(Util.color("FinalBrake released: ${finalBrakeSegment.released}"))
            sender.sendMessage(Util.color("FinalBrake next block clear: ${finalBrakeSegment.isNextBlockClear(true)}"))
            sender.sendMessage(Util.color("FinalBrake amount of vehicles: ${finalBrakeSegment.parent.vehicles.size}"))
        }
    }
}