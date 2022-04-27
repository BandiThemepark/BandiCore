package net.bandithemepark.bandicore.util.chat

enum class BandiColors(val hex: String) {
    YELLOW("#E0D268"),
    RED("#b82727"),
    GREEN("#7FB375"),
    LIGHT_GRAY("#aaa9a8");

    override fun toString(): String {
        return hex
    }
}