package net.bandithemepark.bandicore.util.chat

enum class BandiColors(val hex: String) {
    YELLOW("#E0D268"),
    RED("#b82727"),
    GREEN("#7FB375"),
    DARK_GREEN("#445c3f"),
    BLUE("#1d4291"),
    LIGHT_BLUE("#7ed6d5"),
    PINK("#ff0050"),
    LIGHT_GRAY("#aaa9a8");

    override fun toString(): String {
        return hex
    }
}