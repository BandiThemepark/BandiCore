package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.segments.LogflumeStationSegment
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.switch.LogFlumeSwitch
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.switch.LogFlumeSwitchSegment
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.transferone.LogFlumeTransfer
import net.bandithemepark.bandicore.bandithemepark.adventure.logflume.transferone.LogFlumeTransferSegment
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOPPage
import net.bandithemepark.bandicore.park.attractions.rideop.util.buttons.GatesButton
import net.bandithemepark.bandicore.park.attractions.rideop.util.pages.RideOPCameraPage
import net.bandithemepark.bandicore.park.attractions.rideop.util.pages.RideOPHomePage
import net.bandithemepark.bandicore.park.attractions.rideop.util.pages.RideOPStoragePage
import net.bandithemepark.bandicore.park.attractions.tracks.TrackPosition
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.Vector

class LogFlumeRideOP: RideOP(
    "logflume",
    "logflumestation",
    Location(Bukkit.getWorld("world"), 63.0, -0.4, -164.5, -90.0F, -90.0F)
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

    override fun getPages(): List<RideOPPage> {
        return mutableListOf(
            RideOPHomePage(listOf(
                LogFlumeGatesButton(),
                LogFlumeHarnessButton(),
                LogFlumeIndicator(),
                LogFlumeDispatchButton(),
                LogFlumeEStop()
            )),
            RideOPCameraPage(listOf(

            )),
            RideOPStoragePage(listOf(

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
    }

    override fun onServerStart() {
        transfer.setToStart()
        switch.setToStart()

        BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "117" }!!, 0), 0.0)
        BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "118" }!!, 0), 0.0)
        BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "119" }!!, 0), 0.0)
        BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "120" }!!, 0), 0.0)
        BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "121" }!!, 0), 0.0)
//        BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "122" }!!, 0), 0.0)
//        BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "123" }!!, 0), 0.0)
//        BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "124" }!!, 0), 0.0)
//        BandiCore.instance.trackManager.vehicleManager.loadTrain("logflume", layout, TrackPosition(layout.nodes.find { it.id == "125" }!!, 0), 0.0)
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
}