package net.bandithemepark.bandicore.server.leaderboards

class LeaderboardSettings(
    val name: String,
    val subtext: String,
    entries: List<LeaderboardEntry>
) {
    var entries: List<LeaderboardEntry> = entries
        set(value) {
            field = value
            update()
        }

    private val updateHooks = mutableListOf<() -> Unit>()

    fun addUpdateHook(hook: () -> Unit) {
        updateHooks.add(hook)
    }

    fun update() {
        updateHooks.forEach { it() }
    }
}