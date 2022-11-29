package net.bandithemepark.bandicore.server.achievements

enum class AchievementCategoryType(val showWhenNoneUnlocked: Boolean, val textTranslationId: String?) {
    NORMAL(true, null),
    HISTORICAL(false, "achievement-category-historical"),
    EVENT(false, "achievement-category-event"),
    HIDDEN(false, "achievement-category-hidden")
}