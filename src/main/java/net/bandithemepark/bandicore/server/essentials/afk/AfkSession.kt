package net.bandithemepark.bandicore.server.essentials.afk

import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import java.time.Duration

class AfkSession(val player: Player) {
    val title = AfkTitle.values().random()

    fun sendFadeInTitle() {
        player.showTitle(Title.title(
            Util.color("<${BandiColors.RED}>Welcome back!"),
            Util.color("<${BandiColors.LIGHT_GRAY}>We've missed you!"),
            Title.Times.times(
                Duration.ofMillis(500),
                Duration.ofSeconds(1),
                Duration.ofSeconds(0)
            )
        ))
    }

    fun sendNormalTitle() {
        player.showTitle(Title.title(
            title.title,
            title.subtitle,
            Title.Times.times(
                Duration.ofSeconds(0),
                Duration.ofSeconds(2),
                Duration.ofSeconds(0)
            )
        ))
    }

    fun sendFadeOutTitle() {
        player.showTitle(Title.title(
            Util.color("<${BandiColors.RED}>Welcome back!"),
            Util.color("<${BandiColors.LIGHT_GRAY}>We've missed you!"),
            Title.Times.times(
                Duration.ofSeconds(0),
                Duration.ofSeconds(1),
                Duration.ofMillis(500)
            )
        ))
    }

    enum class AfkTitle(val title: Component, val subtitle: Component) {
        COINS(Util.color("<${BandiColors.YELLOW}>Coins..."), Util.color("<${BandiColors.LIGHT_GRAY}>You aren't getting any")),
        BANDIBOY(Util.color("<${BandiColors.LIGHT_BLUE}>Away from keyboard"), Util.color("<${BandiColors.BLUE}>Playing on your BandiBoy?")),
        PORTAL(Util.color("<${BandiColors.LIGHT_BLUE}>Are you still alive?"), Util.color("<${BandiColors.BLUE}>(The cake is a lie)")),
        W(Util.color("<${BandiColors.GREEN}>You are AFK"), Util.color("<${BandiColors.DARK_GREEN}>I recommend you press the W button")),
        TIKTOK(Util.color("<${BandiColors.LIGHT_BLUE}>Watching TikTok?"), Util.color("<${BandiColors.PINK}>Follow us @bandithemepark")),
        TWITTER(Util.color("<${BandiColors.LIGHT_BLUE}>Scrolling through Twitter?"), Util.color("<${BandiColors.BLUE}>Follow us @BandiThemepark")),
        DISCORD(Util.color("<${BandiColors.BLUE}>Looks like you're doing something else"), Util.color("<${BandiColors.LIGHT_GRAY}>Meanwhile, why not join our Discord?"))
    }
}