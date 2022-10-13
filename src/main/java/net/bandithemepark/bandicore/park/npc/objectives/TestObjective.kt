package net.bandithemepark.bandicore.park.npc.objectives

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.npc.ThemeParkNPCObjective
import net.bandithemepark.bandicore.park.npc.path.PathPointType
import java.util.*

class TestObjective: ThemeParkNPCObjective() {
    override fun onCreate() {
        // Select a random path point that is of the type DEFAULT, and calculate a route
        //val targetPathPoint = BandiCore.instance.server.themePark.pathManager.pathPoints.find { it.uuid == UUID.fromString("e00a314e-98cd-4319-88e8-0c3359eed8fa") }!!
        val targetPathPoint = BandiCore.instance.server.themePark.pathManager.pathPoints.filter { it.type == PathPointType.DEFAULT }.filter { it != npc.currentPoint }.random()
        val route = BandiCore.instance.server.themePark.pathManager.getFastestRoute(npc.currentPoint, targetPathPoint)
        npc.currentRoute = route
    }

    override fun onUpdate() {
        if(npc.currentRoute == null) finish()
    }
}