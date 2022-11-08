package net.bandithemepark.bandicore.server.achievements

enum class AchievementType(val showWhenNotUnlocked: Boolean, val showDescriptionWhenNotUnlocked: Boolean, val typeTranslationId: String?, val unlockedTranslationId: String) {
    NORMAL(true, true, null, "achievement-type-unlocked-amount-normal"),
    SECRET(false, false, "achievement-type-secret", "achievement-type-unlocked-amount-secret"),
    HISTORICAL(false, true, "achievement-type-historical", "achievement-type-unlocked-amount-historical"),
    EVENT(false, true, "achievement-type-event", "achievement-type-unlocked-amount-event"),
}