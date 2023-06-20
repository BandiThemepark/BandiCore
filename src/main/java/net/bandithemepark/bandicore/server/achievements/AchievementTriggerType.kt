package net.bandithemepark.bandicore.server.achievements

abstract class AchievementTriggerType(val id: String) {
    val listeners = hashMapOf<Achievement, String>()

    fun startListening(triggerValue: String, achievement: Achievement) {
        listeners[achievement] = triggerValue
    }

    init {
        register()
    }

    private fun register() {
        types.add(this)
    }

    companion object {
        val types = mutableListOf<AchievementTriggerType>()
    }
}