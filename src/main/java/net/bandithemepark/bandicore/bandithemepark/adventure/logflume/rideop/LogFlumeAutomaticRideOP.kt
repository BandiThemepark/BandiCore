package net.bandithemepark.bandicore.bandithemepark.adventure.logflume.rideop

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
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
                                    getPlayersInStation()?.forEach { it.sendTranslatedActionBar("automatic-rideop-dispatch-soon", BandiColors.YELLOW.toString(), MessageReplacement("ride", rideOP.getParentAttraction()!!.appearance.displayName)) }
                                }
                            } else {
                                getPlayersInStation()?.forEach { it.sendTranslatedActionBar("automatic-rideop-dispatch-in", BandiColors.YELLOW.toString(), MessageReplacement("ride", rideOP.getParentAttraction()!!.appearance.displayName), MessageReplacement("seconds", countdownLeft.toString())) }
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
                                        getPlayersInStation()?.forEach { it.sendTranslatedActionBar("automatic-rideop-dispatch-soon", BandiColors.YELLOW.toString(), MessageReplacement("ride", rideOP.getParentAttraction()!!.appearance.displayName)) }
                                    }
                                } else {
                                    getPlayersInStation()?.forEach { it.sendTranslatedActionBar("automatic-rideop-dispatch-in", BandiColors.YELLOW.toString(), MessageReplacement("ride", rideOP.getParentAttraction()!!.appearance.displayName), MessageReplacement("seconds", countdownLeft.toString())) }
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
                getPlayersInStation()?.forEach { it.sendTranslatedActionBar("rideop-waiting-for-operator", BandiColors.YELLOW.toString(), MessageReplacement("ride", rideOP.getParentAttraction()!!.appearance.displayName), MessageReplacement("operator", rideOP.operator!!.name)) }
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