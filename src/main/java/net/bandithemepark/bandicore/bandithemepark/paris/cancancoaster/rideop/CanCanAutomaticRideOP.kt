package net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.rideop

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.paris.cancancoaster.CanCanRideOP
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class CanCanAutomaticRideOP(val rideOP: CanCanRideOP) {
    val COUNTDOWN_TIME = 10
    val AUTOMATIC_DISPATCH_TIME_SECONDS_FOUR_TRAINS = 60
    val AUTOMATIC_DISPATCH_TIME_SECONDS_FIVE_TRAINS = 45
    val AUTOMATIC_DISPATCH_TIME_SECONDS_SIX_TRAINS = 35

    var currentState = State.IDLE
    var countdownLeft = COUNTDOWN_TIME

    fun update() {
        if(rideOP.operator != null) {
            currentState = State.OPERATOR
            rideOP.getPlayersInStation().forEach { it.sendTranslatedActionBar("rideop-waiting-for-operator", BandiColors.YELLOW.toString(), MessageReplacement("ride", rideOP.getParentAttraction()!!.appearance.displayName), MessageReplacement("operator", rideOP.operator!!.name)) }
            return
        }

        if(rideOP.transferMode) {
            currentState = State.TRANSFER_RESET
        }

        currentState.update(this)
    }

    fun getAutomaticDispatchTimeSeconds(): Int {
        when(rideOP.getAmountOfTrainsOnTrack()) {
            4 -> return AUTOMATIC_DISPATCH_TIME_SECONDS_FOUR_TRAINS
            5 -> return AUTOMATIC_DISPATCH_TIME_SECONDS_FIVE_TRAINS
            6 -> return AUTOMATIC_DISPATCH_TIME_SECONDS_SIX_TRAINS
        }
        return AUTOMATIC_DISPATCH_TIME_SECONDS_SIX_TRAINS
    }

    enum class State {
        TRANSFER_RESET {
            override fun update(automaticRideOP: CanCanAutomaticRideOP) {
                if(automaticRideOP.rideOP.canDisableTransferMode()) {
                    automaticRideOP.rideOP.disableTransferMode()
                    automaticRideOP.rideOP.updateMenu()
                    automaticRideOP.currentState = IDLE
                    return
                }
            }
        },
        OPERATOR {
            override fun update(automaticRideOP: CanCanAutomaticRideOP) {
                if(automaticRideOP.rideOP.operator == null) {
                    if(automaticRideOP.rideOP.track.eStop) {
                        automaticRideOP.rideOP.track.eStop = false
                        automaticRideOP.rideOP.updateMenu()
                    }

                    automaticRideOP.currentState = IDLE
                }
            }
        },
        IDLE {
            override fun update(automaticRideOP: CanCanAutomaticRideOP) {
                if(automaticRideOP.rideOP.isTrainInStation()) {
                    if(!automaticRideOP.rideOP.harnessButton.open) {
                        automaticRideOP.rideOP.harnessButton.setOpen()
                        automaticRideOP.rideOP.harnessButton.open = true
                        automaticRideOP.rideOP.updateMenu()
                    }

                    if(!automaticRideOP.rideOP.gatesButton.open) {
                        automaticRideOP.rideOP.gatesButton.open = true
                        Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { automaticRideOP.rideOP.gatesButton.updateGates() })
                        automaticRideOP.rideOP.updateMenu()
                    }
                }

                if(automaticRideOP.rideOP.isTrainInStation() && automaticRideOP.rideOP.getPlayersInStation().isNotEmpty()) {
                    automaticRideOP.countdownLeft = automaticRideOP.COUNTDOWN_TIME
                    automaticRideOP.currentState = COUNTDOWN
                    return
                }

                if(automaticRideOP.rideOP.lastDispatch + automaticRideOP.getAutomaticDispatchTimeSeconds() * 1000 <= System.currentTimeMillis()) {
                    automaticRideOP.currentState = DISPATCHING
                    return
                }
            }
        },
        COUNTDOWN {
            override fun update(automaticRideOP: CanCanAutomaticRideOP) {
                if(automaticRideOP.rideOP.getPlayersInStation().isEmpty()) {
                    automaticRideOP.currentState = IDLE
                    return
                }

                if(!automaticRideOP.rideOP.harnessesLocked) {
                    automaticRideOP.rideOP.harnessesLocked = true
                    automaticRideOP.rideOP.updateLockedState()
                }

                automaticRideOP.countdownLeft--
                automaticRideOP.rideOP.getPlayersInStation().forEach { it.sendTranslatedActionBar("automatic-rideop-dispatch-in", BandiColors.YELLOW.toString(), MessageReplacement("ride", automaticRideOP.rideOP.getParentAttraction()!!.appearance.displayName), MessageReplacement("seconds", automaticRideOP.countdownLeft.toString())) }

                if(automaticRideOP.countdownLeft == 2) {
                    if(automaticRideOP.rideOP.pushDownAllButton.isAvailable()) {
                        for(harness in automaticRideOP.rideOP.getAllHarnesses()) {
                            if(harness.harnessPosition != 0.0 && harness.currentProgress >= 30) {
                                harness.startDownwardsInterpolation()
                            }
                        }
                    }
                }

                if(automaticRideOP.countdownLeft <= 0) {
                    automaticRideOP.currentState = DISPATCHING
                }
            }
        },
        DISPATCHING {
            override fun update(automaticRideOP: CanCanAutomaticRideOP) {
                automaticRideOP.rideOP.getPlayersInStation().forEach { it.sendTranslatedActionBar("automatic-rideop-dispatch-soon", BandiColors.YELLOW.toString(), MessageReplacement("ride", automaticRideOP.rideOP.getParentAttraction()!!.appearance.displayName)) }

                if(automaticRideOP.rideOP.harnessButton.open) {
                    automaticRideOP.rideOP.harnessButton.setClosed()
                    automaticRideOP.rideOP.harnessButton.open = false
                    automaticRideOP.rideOP.updateMenu()
                }

                if(automaticRideOP.rideOP.gatesButton.open) {
                    automaticRideOP.rideOP.gatesButton.open = false
                    Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { automaticRideOP.rideOP.gatesButton.updateGates() })
                    automaticRideOP.rideOP.updateMenu()
                }

                if(!automaticRideOP.rideOP.canDispatch()) {
                    if(automaticRideOP.rideOP.pushDownAllButton.isAvailable()) {
                        for(harness in automaticRideOP.rideOP.getAllHarnesses()) {
                            if(harness.harnessPosition != 0.0 && harness.currentProgress >= 30) {
                                harness.startDownwardsInterpolation()
                            }
                        }
                    }

                    return
                }

                automaticRideOP.rideOP.dispatch()
                automaticRideOP.currentState = IDLE
                automaticRideOP.rideOP.updateMenu()
            }
        };

        abstract fun update(automaticRideOP: CanCanAutomaticRideOP)
    }
}