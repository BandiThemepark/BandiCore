package net.bandithemepark.bandicore.park.npc

class ThemeParkNPCNamer {
    val firstNames = mutableListOf(
        "Gert",
        "John",
        "Youri",
        "Elias",
        "Hecki",
        "Dennis",
        "Yarael",
        "Colin",
        "Bernard",
        "Lorem"
    )

    val lastNames = mutableListOf(
        "Deere",
        "Grasmaaier",
        "The Depressed",
        "Bierkraag",
        "The Unhappy",
        "The Unlucky",
        "The Unloved",
        "Ipsum"
    )

    fun getName(): String {
        return "${firstNames.random()} ${lastNames.random()}"
    }
}