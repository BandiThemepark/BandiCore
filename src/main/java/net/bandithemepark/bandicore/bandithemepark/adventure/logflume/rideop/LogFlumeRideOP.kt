package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.segments.LogflumeStationSegment
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.switch.LogFlumeStorageSegment
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.switch.LogFlumeSwitch
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.switch.LogFlumeSwitchSegment
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.transferone.LogFlumeTransfer
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.transferone.LogFlumeTransferSegment
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOPPage
import net.bandithemepark.bandicore.park.attractions.rideop.camera.RideOPCameraButton
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.GatesButton
import net.bandithemepark.bandicore.park.attractions.rideop.util.pages.RideOPCameraPage
import net.bandithemepark.bandicore.park.attractions.rideop.util.pages.RideOPHomePage
import net.bandithemepark.bandicore.park.attractions.rideop.util.pages.RideOPStoragePage
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import net.bandithemepark.bandicore.park.attractions.tracks.vehicles.attachments.types.SeatAttachment
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class LogFlumeRideOP: RideOP(
    "logflume",
    "logflumestation",
    Location(Bukkit.getWorld("world"), 63.0, -0.2, -164.5, -90.0F, -90.0F)
) {
    val layout = BandiCore.instance.trackManager.loadedTracks.find { it.id == "logflume" }!!
    val transferSegment = layout.segmentSeparators.find { it.type is LogFlumeTransferSegment }!!
    val transfer = LogFlumeTransfer(
        layout,
        layout.getNode("transferconnector1")!!,
        layout.getNode("transferconnector2")!!,
        layout.getNode("transfer1")!!,
        layout.getNode("transfer2")!!,
        Vector(-39.5, 2.0, 78.0),
        Vector(-39.5, 2.0, 82.0),
        Vector(-44.5, 2.0, 78.0),
        Vector(-44.5, 2.0, 82.0)
    )

    val switchSegment = layout.segmentSeparators.find { it.type is LogFlumeSwitchSegment }!!
    val switch = LogFlumeSwitch(
        layout,
        switchSegment,
        layout.getNode("storageconnector")!!,
        layout.getNode("switchpart1")!!,
        layout.getNode("switchpart2")!!,
        layout.getNode("switchconnector1")!!,
        layout.getNode("switchconnector2")!!,
        Vector(-37.5, 9.0, 89.0),
        Vector(-35.0, 9.0, 91.5),
        Vector(-32.5, 9.0, 89.0),
        Vector(-35.0, 9.0, 86.5)
    )

    val stationSegment = layout.segmentSeparators.find { it.type is LogflumeStationSegment }!!
    val station = stationSegment.type as LogflumeStationSegment
    var dispatchDelay = 0
    val automaticRideOP = LogFlumeAutomaticRideOP(this)
    var lastDispatch = System.currentTimeMillis()

    val gatesButton = LogFlumeGatesButton()
    val harnessButton = LogFlumeHarnessButton()
    val dispatchButton = LogFlumeDispatchButton()

    var transferModeActive = false
    var storageSegments = listOf<LogFlumeStorageSegment>()
    var boatsInStorage = 0
    var MAX_BOATS_IN_STORAGE = 3
    var storageState = StorageState.NONE

    enum class StorageState {
        STORING, RETRIEVING, NONE
    }

    private fun loadStorageSegments() {
        storageSegments = layout.segmentSeparators
            .filter { it.type is LogFlumeStorageSegment }
            .sortedBy { (it.type as LogFlumeStorageSegment).metadata[0].toInt() }
            .map { it.type as LogFlumeStorageSegment }
        MAX_BOATS_IN_STORAGE = storageSegments.size
    }

    fun canSendIntoStorage(): Boolean {
        if(!transferModeActive) return false
        if((switchSegment.type as LogFlumeSwitchSegment).state != LogFlumeSwitchSegment.SwitchState.WAITING_TO_START) return false
        if(boatsInStorage >= MAX_BOATS_IN_STORAGE) return false
        if(storageState != StorageState.NONE) return false
        return true
    }

    fun sendIntoStorage() {
        val targetSegment = MAX_BOATS_IN_STORAGE - boatsInStorage
        storageSegments.forEach { it.mode = LogFlumeStorageSegment.Mode.PASSTHROUGH }
        storageSegments[targetSegment-1].mode = LogFlumeStorageSegment.Mode.STORING
        storageState = StorageState.STORING

        (switchSegment.type as LogFlumeSwitchSegment).sendIntoStorage()
        updateMenu()
    }

    fun canRetrieveFromStorage(): Boolean {
        if(!transferModeActive) return false
        if((switchSegment.type as LogFlumeSwitchSegment).state != LogFlumeSwitchSegment.SwitchState.RESETTING) return false
        if(boatsInStorage <= 0) return false
        if(storageState != StorageState.NONE) return false

        return true
    }

    fun retrieveFromStorage() {
        val targetSegment = MAX_BOATS_IN_STORAGE - boatsInStorage + 1
        storageSegments.forEach { it.mode = LogFlumeStorageSegment.Mode.PASSTHROUGH }
        storageState = StorageState.RETRIEVING

        storageSegments[targetSegment-1].retrieve()
        boatsInStorage--
        updateMenu()
    }

    override fun getPages(): List<RideOPPage> {
        return mutableListOf(
            RideOPHomePage(listOf(
                gatesButton,
                harnessButton,
                LogFlumeIndicator(),
                dispatchButton,
                LogFlumeEStop()
            )),
            RideOPCameraPage(listOf(
                RideOPCameraButton(0, "Transfer", Location(Bukkit.getWorld("world"), 24.0, 12.0, -90.0, -135.0F, 40.0F), this),
                RideOPCameraButton(1, "Station Queue", Location(Bukkit.getWorld("world"), 56.5, 4.0, -163.0, 180.0F, 20.0F), this),
                RideOPCameraButton(2, "Lifthill 1", Location(Bukkit.getWorld("world"), 62.0, 13.7, -134.5, 170.0F, 40.0F), this),
                RideOPCameraButton(3, "Drop 1", Location(Bukkit.getWorld("world"), 38.0, 10.5, -105.0, -60.0F, 40.0F), this),
                RideOPCameraButton(4, "Drop 1 Splash", Location(Bukkit.getWorld("world"), 23.5, 5.55, -103.5, -70.0F, 40.0F), this),
                RideOPCameraButton(5, "Switchtrack", Location(Bukkit.getWorld("world"), 20.0, 5.5, -97.0, 150.0F, 33.0F), this),
                RideOPCameraButton(6, "Lifthill 2", Location(Bukkit.getWorld("world"), 55.0, 11.0, -87.0, 50.0F, 27.0F), this),
                RideOPCameraButton(7, "Drop 2 Splash", Location(Bukkit.getWorld("world"), 33.5, 14.0, -89.5, -105.0F, 27.0F), this),
                RideOPCameraButton(8, "Storage", Location(Bukkit.getWorld("world"), 16.5, 11.5, -92.5, 60.0F, 17.0F), this),
                RideOPCameraButton(9, "Disco Entrance", Location(Bukkit.getWorld("world"), 13.5, 16.5, -114.5, -20.0F, 12.0F), this),
                RideOPCameraButton(10, "Disco", Location(Bukkit.getWorld("world"), 14.5, 17.0, -95.5, -130.0F, 22.0F), this),
                RideOPCameraButton(11, "Drop 3 Start", Location(Bukkit.getWorld("world"), 42.5, 3.5, -124.5, 45.0F, 18.0F), this),
                RideOPCameraButton(12, "Drop 3 Airtime Hill", Location(Bukkit.getWorld("world"), 32.5, 3.0, -139.5, -45.0F, 27.0F), this),
                RideOPCameraButton(13, "Station Boat Entrance", Location(Bukkit.getWorld("world"), 60.5, 2.0, -189.5, 60.0F, 10.0F), this),
            )),
            RideOPStoragePage(listOf(
                LogFlumeTransferModeButton(),
                LogFlumeTransferSendButton(),
                LogFlumeTransferRetrieveButton(),
            ))
        )
    }

    fun isTransferClear(): Boolean {
        if(transferSegment.vehicles.isNotEmpty()) return false
        if(transferTimeLeft != 0) return false
        if(transferMovingForward) return false
        if(layout.eStop) return false

        return true
    }

    fun isSwitchClear(): Boolean {
        if(switchSegment.vehicles.isNotEmpty()) return false
        if(switchTimeLeft != 0) return false
        if(switchMovingForward) return false
        if(layout.eStop) return false

        return true
    }

    var transferMovingForward = false
    var transferTimeLeft = 0

    var switchMovingForward = false
    var switchTimeLeft = 0

    override fun onTick() {
        if(transferTimeLeft > 0) {
            transferTimeLeft--

            if(transferMovingForward) {
                transfer.moveTo((100-transferTimeLeft)/100.0)
                if(transferTimeLeft == 0) transfer.setToEnd()
            } else {
                transfer.moveTo(transferTimeLeft/100.0)
                if(transferTimeLeft == 0) transfer.setToStart()
            }
        }

        if(switchTimeLeft > 0) {
            switchTimeLeft--

            if(switchMovingForward) {
                switch.moveTo((100-switchTimeLeft)/100.0)
                if(switchTimeLeft == 0) switch.setToEnd()
            } else {
                switch.moveTo(switchTimeLeft/100.0)
                if(switchTimeLeft == 0) switch.setToStart()
            }
        }
    }

    override fun onSecond() {
        if(dispatchDelay > 0) {
            dispatchDelay--
            if(dispatchDelay == 0) updateMenu()
        }

        automaticRideOP.second()
    }

    override fun onServerStart() {
        transfer.spawnModel()
        transfer.setToStart()
        switch.spawnModel()
        switch.setToStart()
        loadStorageSegments()

        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
            BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "117" }!!, 0), 0.0)
            BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "118" }!!, 0), 0.0)
            BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "119" }!!, 0), 0.0)
            BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "120" }!!, 0), 0.0)
            BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "121" }!!, 0), 0.0)
        }, 10)
    }

    override fun onServerStop() {

    }

    fun startTransfer() {
        transfer.prepareForward()
        transferMovingForward = true
        transferTimeLeft = 100
    }

    fun revertTransfer() {
        transfer.prepareBackward()
        transferMovingForward = false
        transferTimeLeft = 100
    }

    fun startSwitch() {
        switch.prepareForwards()
        switchMovingForward = true
        switchTimeLeft = 100
    }

    fun revertSwitch() {
        switch.prepareBackwards()
        switchMovingForward = false
        switchTimeLeft = 100
    }

    fun dispatch() {
        automaticRideOP.dispatchesLeft--
        if(station.currentStopped != null) {
            if (station.currentStopped?.getPlayerPassengers()!!.isNotEmpty()) {
                automaticRideOP.dispatchesLeft = 4
            }
        }

        automaticRideOP.countdown = false

        station.dispatch()
        dispatchDelay = 20
        lastDispatch = System.currentTimeMillis()
    }

    fun getPlayerPassengers(): List<Player> {
        val passengers = mutableListOf<Player>()

        for(vehicle in layout.getVehicles()) {
            for(attachment in vehicle.getAllAttachments().filter { it.type is SeatAttachment }) {
                passengers.addAll((attachment.type as SeatAttachment).seat!!.getPassengers().filterIsInstance<Player>())
            }
        }

        return passengers
    }
}