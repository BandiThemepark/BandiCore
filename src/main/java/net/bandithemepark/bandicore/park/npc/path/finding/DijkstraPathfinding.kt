package net.bandithemepark.bandicore.park.npc.path.finding

import net.bandithemepark.bandicore.park.npc.path.PathManager
import net.bandithemepark.bandicore.park.npc.path.PathPoint
import net.bandithemepark.bandicore.util.math.MathUtil

class DijkstraPathfinding(val manager: PathManager) {
    fun run(from: PathPoint, to: PathPoint): List<PathPoint> {
        val result = dijkstraMijnMakker(from)

        val path = mutableListOf<PathPoint>()
        var current = to
        while (current != from) {
            path.add(current)
            current = result.results.find { it.vertex == current }!!.previous!!
        }

        path.reverse()
        if(!path.contains(from)) path.add(0, from)
        if(!path.contains(to)) path.add(to)
        return path
    }

    private fun dijkstraMijnMakker(from: PathPoint): Result {
        val visited = mutableListOf<PathPoint>()
        val unvisited = mutableListOf<PathPoint>()
        unvisited.addAll(manager.pathPoints)

        val distances = mutableListOf<Distance>()
        distances.add(Distance(from, from, 0.0))
        distances.addAll(manager.pathPoints.filter { it != from }.map { Distance(it, null, Double.MAX_VALUE) })

        while(unvisited.isNotEmpty()) {
            val visiting = findSmallestUnvisited(distances.filter { unvisited.contains(it.from) })
            unvisited.remove(visiting.from)
            visited.add(visiting.from)

            for(neighbour in visiting.from.connectedTo.filter { unvisited.contains(it) }) {
                val distanceFromStart = visiting.distance + MathUtil.getDistanceBetween(visiting.from.location.toVector(), neighbour.location.toVector())
                val distanceInstanceNeighbour = distances.find { it.from == neighbour }!!

                if(distanceFromStart < distanceInstanceNeighbour.distance) {
                    distanceInstanceNeighbour.distance = distanceFromStart
                    distanceInstanceNeighbour.to = visiting.from
                }
            }
        }

        val results = mutableListOf<VertexResult>()
        for(distance in distances) {
            results.add(VertexResult(distance.from, distance.distance, distance.to))
        }
        return Result(results)
    }

    private fun findSmallestUnvisited(distances: List<Distance>): Distance {
        return distances.minByOrNull { it.distance }!!
    }

    data class Distance(var from: PathPoint, var to: PathPoint?, var distance: Double)
    data class Result(val results: List<VertexResult>)
    data class VertexResult(val vertex: PathPoint, val distance: Double, val previous: PathPoint?)
}