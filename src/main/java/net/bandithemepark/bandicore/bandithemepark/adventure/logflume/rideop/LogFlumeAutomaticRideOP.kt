package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop

import net.bandithemepark.bandicore.BandiCore
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class LogFlumeAutomaticRideOP(val rideOP: LogFlumeRideOP) {
    var dispatchesLeft = 0
    var countdown = false
    var countdownLeft = 0
    var automaticDispatchTimeSeconds = 90

    fun second() {
        if(rideOP.operator == null) {
            if(false) { // TODO Transfer mode on?
                if(!true) { // TODO Moving on transfer?
                    // TODO Disable transfer mode
                }
            } else {
                if(rideOP.station.currentStopped != null) {
                    if(!rideOP.gatesButton.open) {
                        rideOP.gatesButton.open = true
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { rideOP.gatesButton.updateGates() })
                        rideOP.updateMenu()
                    }

                    if(!rideOP.harnessButton.open) {
                        rideOP.harnessButton.setOpen()
                        rideOP.harnessButton.open = true
                        rideOP.updateMenu()
                    }

                    if(dispatchesLeft > 0) {
                        if(countdown) {
                            countdownLeft--

                            if(countdownLeft <= 0) {
                                if(rideOP.dispatchDelay == 0 && !rideOP.layout.eStop && rideOP.station.currentStopped != null) {
                                    rideOP.gatesButton.open = false
                                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { rideOP.gatesButton.updateGates() })
                                    rideOP.harnessButton.setClosed()
                                    rideOP.harnessButton.open = false
                                    rideOP.dispatch()
                                    rideOP.updateMenu()
                                } else {
                                    // TODO Actionbar starting soon
                                    getPlayersInStation()?.forEach { it.sendActionBar(Component.text("Log flume is expected to dispatch soon")) }
                                }
                            } else {
                                // TODO Actionbar dispatch in
                                getPlayersInStation()?.forEach { it.sendActionBar(Component.text("Log flume will dispatch in $countdownLeft seconds")) }
                            }
                        } else {
                            startCountdown()
                        }
                    } else {
                        if(countdown) {
                            countdownLeft--

                            if(arePlayersInStation()) {
                                if(countdownLeft <= 0) {
                                    if(rideOP.gatesButton.open) {
                                        rideOP.gatesButton.open = false
                                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { rideOP.gatesButton.updateGates() })
                                        rideOP.updateMenu()
                                    }

                                    if(rideOP.harnessButton.open) {
                                        rideOP.harnessButton.setClosed()
                                        rideOP.harnessButton.open = false
                                        rideOP.updateMenu()
                                    }

                                    if(rideOP.dispatchButton.isAvailable()) {
                                        rideOP.gatesButton.open = false
                                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { rideOP.gatesButton.updateGates() })
                                        rideOP.harnessButton.setClosed()
                                        rideOP.harnessButton.open = false
                                        rideOP.dispatch()
                                        rideOP.updateMenu()
                                    } else {
                                        // TODO Actionbar dispatch soon
                                        getPlayersInStation()?.forEach { it.sendActionBar(Component.text("Log flume is expected to dispatch soon")) }
                                    }
                                } else {
                                    // TODO Actionbar dispatch in
                                    getPlayersInStation()?.forEach { it.sendActionBar(Component.text("Log flume will dispatch in $countdownLeft seconds")) }
                                }
                            } else {
                                countdown = false
                            }
                        } else {
                            if(arePlayersInStation()) {
                                startCountdown()
                            } else {
                                if(rideOP.lastDispatch + 1000L * automaticDispatchTimeSeconds < System.currentTimeMillis()) {
                                    rideOP.gatesButton.open = false
                                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { rideOP.gatesButton.updateGates() })
                                    rideOP.harnessButton.setClosed()
                                    rideOP.harnessButton.open = false
                                    rideOP.dispatch()
                                    rideOP.updateMenu()
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if(rideOP.station.currentStopped != null) {
                // TODO Actionbar with waiting to dispatch
                getPlayersInStation()?.forEach { it.sendActionBar(Component.text("Waiting for operator ${rideOP.operator!!.name} to dispatch")) }
            }
        }
    }

    private fun arePlayersInStation(): Boolean {
        return rideOP.station.currentStopped != null && rideOP.station.currentStopped?.getPlayerPassengers()!!.isNotEmpty()
    }

    private fun getPlayersInStation(): List<Player>? {
        return if(rideOP.station.currentStopped != null) {
            rideOP.station.currentStopped?.getPlayerPassengers()
        } else {
            null
        }
    }

    private fun startCountdown() {
        countdown = true

        countdownLeft = if(dispatchesLeft > 0) {
            rideOP.dispatchDelay + 10
        } else {
            10
        }
    }
}