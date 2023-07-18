package net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.rideop

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.Rupsbaan
import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.RupsbaanRideSchedule
import net.bandithemepark.bandicore.bandithemepark.adventure.rupsbaan.effects.RupsbaanEffectManager
import net.bandithemepark.bandicore.park.attractions.rideop.RideOP
import net.bandithemepark.bandicore.park.attractions.rideop.RideOPPage
import net.bandithemepark.bandicore.park.attractions.rideop.util.pages.RideOPCameraPage
import net.bandithemepark.bandicore.park.attractions.rideop.util.pages.RideOPHomePage
import net.bandithemepark.bandicore.server.translations.LanguageUtil.sendTranslatedActionBar
import net.bandithemepark.bandicore.server.translations.MessageReplacement
import net.bandithemepark.bandicore.util.chat.BandiColors
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

class RupsbaanRideOP: RideOP("rupsbaan", "rupsbaanstation",
    Location(Bukkit.getWorld("world"), -43.0, 1.7, -195.5, 90.0F, 90.0F)
) {
    override fun getPages(): List<RideOPPage> {
        return mutableListOf(
            RideOPHomePage(listOf(
                RupsbaanGatesButton(),
                RupsbaanHarnessLockButton(),
                RupsbaanPushDownAllButton(),
                RupsbaanDispatchButton(),
                RupsbaanStatusButton(),
                RupsbaanSuperModeButton(),
                RupsbaanEStopButton()
            )),
            RideOPCameraPage(listOf(

            ))
        )
    }

    val ride = Rupsbaan(Location(Bukkit.getWorld("world"), -30.5, 3.0, -195.5))

    companion object {
        var superMode = false
        var topSpeed = 7.5
    }

    fun getSuperMode(): Boolean {
        return superMode
    }
    fun setSuperMode(newSuperMode: Boolean) {
        superMode = newSuperMode

        topSpeed = if(superMode) {
            10.0
        } else {
            7.5
        }
    }

    val rideSchedule = RupsbaanRideSchedule(ride, hashMapOf(
        0 to 0.0,
        20 to 1.0,
        240 to 1.0,
        280 to 3.0,
        560 to 3.0,
        600 to 5.0,
        740 to 5.0,
        760 to 6.0,
        900 to 6.0,
        920 to 6.0,
        940 to 4.0,
        1080 to 4.0,
        1100 to 5.0,
        1240 to 5.0,
        1280 to 6.0,
        1380 to 6.0,
        1460 to 7.0,
        1620 to 7.0,
        1700 to 7.0,
        1800 to 6.0,
        1880 to 10.0,
        2000 to 10.0,
        2020 to 9.0,
        2200 to 9.0,
        2360 to 9.0,
        2440 to 4.0,
        2500 to 2.0,
        2680 to 2.0,
        2720 to 0.0,
    ))
    override fun onTick() {
        ride.update()
        rideSchedule.update()
    }

    var countdown = false
    var countdownLeft = 0
    override fun onSecond() {
        if(operator == null && !rideSchedule.active) {
            // open harnesses if harnesses closed
            if(!ride.harnessesLocked) {
                ride.setHarnesses(false)
                updateMenu()
            }

            // open gates if gates closed
            val gatesButton = loadedPages[0].loadedButtons.first { it is RupsbaanGatesButton } as RupsbaanGatesButton
            if(!gatesButton.open) {
                gatesButton.open = true
                Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { gatesButton.updateGates() })
                updateMenu()
            }

            if(countdown) {
                countdownLeft--
                // dispatch in
                getPlayersOnRide().forEach {
                    it.sendTranslatedActionBar("automatic-rideop-dispatch-in", BandiColors.YELLOW.toString(), MessageReplacement("ride", getParentAttraction()!!.appearance.displayName), MessageReplacement("seconds", countdownLeft.toString()))
                }

                if(getPlayersOnRide().isNotEmpty()) {
                    if(countdownLeft == 2) {
                        ride.carts.forEach {
                            if(it.harnessPosition != 0.0) it.startDownwardsInterpolation()
                        }
                    }
                    if(countdownLeft <= 0) {
                        if(!ride.harnessesLocked) {
                            ride.setHarnesses(true)
                            ride.carts.forEach { it.startDownwardsInterpolation() }
                            updateMenu()
                        }

                        if(gatesButton.open) {
                            gatesButton.open = false
                            Bukkit.getScheduler().runTask(BandiCore.instance, Runnable { gatesButton.updateGates() })
                            updateMenu()
                        }

                        val dispatchButton = loadedPages[0].loadedButtons.first { it is RupsbaanDispatchButton } as RupsbaanDispatchButton
                        if(dispatchButton.isAvailable()) {
                            rideSchedule.start()
                        } else {
                            // expected to dispatch soon
                            getPlayersOnRide().forEach {
                                it.sendTranslatedActionBar("automatic-rideop-dispatch-soon", BandiColors.YELLOW.toString(), MessageReplacement("ride", getParentAttraction()!!.appearance.displayName))
                            }
                        }
                    }
                } else {
                    countdown = false
                }
            } else {
                if(getPlayersOnRide().isNotEmpty()) {
                    countdown = true
                    countdownLeft = 15
                }
            }
        }

        if(ride.harnessesLocked) {
            for (cart in ride.carts) {
                if(cart.harnessPosition != 0.0) {
                    cart.getPlayers().forEach {
                        it.sendTranslatedActionBar("rupsbaan-hold-to-close", BandiColors.YELLOW.toString(), MessageReplacement("key", "<key:key.back>"))
                    }
                }
            }
        }
    }

    fun getPlayersOnRide(): List<Player> {
        val players = mutableListOf<Player>()
        ride.carts.forEach { players.addAll(it.getPlayers()) }
        return players.toList()
    }

    private lateinit var effectManager: RupsbaanEffectManager
    override fun onServerStart() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BandiCore.instance, {
            ride.spawn()
            ride.currentSpeed = 0.0
            ride.setHarnesses(true)

            effectManager = RupsbaanEffectManager()
        }, 10)
    }

    override fun onServerStop() {
        ride.deSpawn()
    }
}