package net.bandithemepark.bandicore.server.minigames.cooking

import net.bandithemepark.bandicore.server.minigames.MinigamePlayer
import net.bandithemepark.bandicore.server.minigames.cooking.item.CookingItem
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingProduct
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingRecipeCombine
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingRecipeCook
import net.bandithemepark.bandicore.server.minigames.cooking.item.recipe.CookingRecipeCutting
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.types.CookingPlaceableDispenser
import net.bandithemepark.bandicore.server.minigames.cooking.placeable.types.*
import net.bandithemepark.bandicore.util.ItemFactory
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

class CookingMinigameSolo: CookingMinigameGame("cooking-solo", 1, 1, "Cooking (Solo)", listOf("Play solo"), ItemFactory(Material.PAPER).build()) {
    override val map = CookingMap(listOf(
        Location(Bukkit.getWorld("world"), -83.5, 14.0, -115.5, 120f, 0f)
    ), listOf(
        CookingPlaceableWorkspace(Location(Bukkit.getWorld("world"), -82.5, 14.0, -113.5, -180.0f, 0.0f)),
        CookingPlaceableWorkspace(Location(Bukkit.getWorld("world"), -84.5, 14.0, -113.5, -180.0f, 0.0f)),
        CookingPlaceableWorkspace(Location(Bukkit.getWorld("world"), -86.5, 14.0, -113.5, -180.0f, 0.0f)),
        CookingPlaceableWorkspace(Location(Bukkit.getWorld("world"), -87.5, 14.0, -113.5, -180.0f, 0.0f)),
        CookingPlaceableWorkspace(Location(Bukkit.getWorld("world"), -87.5, 14.0, -114.5, -90.0f, 0.0f)),
        CookingPlaceableWorkspace(Location(Bukkit.getWorld("world"), -87.5, 14.0, -115.5, -90.0f, 0.0f)),
        CookingPlaceableWorkspace(Location(Bukkit.getWorld("world"), -87.5, 14.0, -117.5, -90.0f, 0.0f)),
        CookingPlaceableWorkspace(Location(Bukkit.getWorld("world"), -84.5, 14.0, -117.5, 90.0f, 0.0f)),
        CookingPlaceableWorkspace(Location(Bukkit.getWorld("world"), -84.5, 14.0, -119.5, 90.0f, 0.0f)),
        CookingPlaceableWorkspace(Location(Bukkit.getWorld("world"), -83.5, 14.0, -117.5, -90.0f, 0.0f)),
        CookingPlaceablePlateDispenser(Location(Bukkit.getWorld("world"), -84.5, 14.0, -118.5, 90.0f, 0.0f)),
        CookingPlaceableDispenser(Location(Bukkit.getWorld("world"), -86.5, 14.0, -125.5, 0.0f, 0.0f), ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(25).build(), CookingItem.BURGER_BUN),
        CookingPlaceableDispenser(Location(Bukkit.getWorld("world"), -85.5, 14.0, -125.5, 0.0f, 0.0f), ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(25).build(), CookingItem.BREAD_SLICE),
        CookingPlaceableDispenser(Location(Bukkit.getWorld("world"), -86.5, 15.0, -125.5, 0.0f, 0.0f), ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(25).build(), CookingItem.LETTUCE),
        CookingPlaceableDispenser(Location(Bukkit.getWorld("world"), -85.5, 15.0, -125.5, 0.0f, 0.0f), ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(25).build(), CookingItem.TOMATO),
        CookingPlaceableDispenser(Location(Bukkit.getWorld("world"), -86.5, 16.0, -125.5, 0.0f, 0.0f), ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(25).build(), CookingItem.CHEESE_SLICE),
        CookingPlaceableDispenser(Location(Bukkit.getWorld("world"), -86.5, 16.0, -125.5, 0.0f, 0.0f), ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(25).build(), CookingItem.CHEESE_SLICE),
        CookingPlaceableDispenser(Location(Bukkit.getWorld("world"), -82.5, 15.0, -124.5, 90.0f, 0.0f), ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(26).build(), CookingItem.BURGER),
        CookingPlaceableDispenser(Location(Bukkit.getWorld("world"), -82.5, 15.0, -123.5, 90.0f, 0.0f), ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(27).build(), CookingItem.HAM),
        CookingPlaceableTrashcan(Location(Bukkit.getWorld("world"), -87.5, 14.0, -118.5, -90.0f, 0.0f)),
        CookingPlaceableElevator(Location(Bukkit.getWorld("world"), -88.5, 15.0, -120.5, -90.0f, 0.0f)),
        CookingPlaceableStove(Location(Bukkit.getWorld("world"), -83.5, 14.0, -113.5, -180.0f, 0.0f)),
        CookingPlaceableStove(Location(Bukkit.getWorld("world"), -87.5, 14.0, -116.5, -90.0f, 0.0f)),
        CookingPlaceableCuttingBoard(Location(Bukkit.getWorld("world"), -85.5, 14.0, -113.5, -180.0f, 0.0f)),
        CookingPlaceableSink(Location(Bukkit.getWorld("world"), -83.5, 14.0, -118.5, -90.0f, 0.0f)),
        CookingPlaceableWashedPlates(Location(Bukkit.getWorld("world"), -83.5, 14.0, -119.5, -90.0f, 0.0f)),
    ), listOf(
        CookingRecipeCook(CookingItem.BURGER, CookingItem.COOKED_BURGER, CookingItem.BURNT_BURGER, 10 * 20, 10 * 20),
        CookingRecipeCook(CookingItem.BURGER_WITH_CHEESE, CookingItem.COOKED_BURGER_WITH_CHEESE, CookingItem.BURNT_BURGER_WITH_CHEESE, 10 * 20, 8 * 20),
        CookingRecipeCook(CookingItem.UNCOOKED_TOSTI, CookingItem.TOSTI, CookingItem.BURNT_TOSTI, 10 * 20, 10 * 20),

        CookingRecipeCutting(CookingItem.TOMATO, CookingItem.TOMATO_SLICE, 10),
        CookingRecipeCutting(CookingItem.LETTUCE, CookingItem.LETTUCE_SLICE, 10),

        CookingRecipeCombine(CookingItem.BURGER_BUN, CookingItem.LETTUCE_SLICE, CookingItem.BURGER_BUN_LETTUCE_SLICE),
        CookingRecipeCombine(CookingItem.BURGER_BUN_LETTUCE_SLICE, CookingItem.TOMATO_SLICE, CookingItem.BURGER_BUN_LETTUCE_SLICE_TOMATO_SLICE),
        CookingRecipeCombine(CookingItem.BURGER_BUN_LETTUCE_SLICE_TOMATO_SLICE, CookingItem.COOKED_BURGER, CookingItem.HAMBURGER),
        CookingRecipeCombine(CookingItem.HAMBURGER, CookingItem.PLATE, CookingItem.HAMBURGER_ON_PLATE),

        CookingRecipeCombine(CookingItem.BURGER, CookingItem.CHEESE_SLICE, CookingItem.BURGER_WITH_CHEESE),
        CookingRecipeCombine(CookingItem.BURGER_BUN, CookingItem.COOKED_BURGER_WITH_CHEESE, CookingItem.CHEESEBURGER),
        CookingRecipeCombine(CookingItem.CHEESEBURGER, CookingItem.PLATE, CookingItem.CHEESEBURGER_ON_PLATE),

        CookingRecipeCombine(CookingItem.BREAD_SLICE, CookingItem.HAM, CookingItem.BREAD_SLICES_HAM),
        CookingRecipeCombine(CookingItem.BREAD_SLICES_HAM, CookingItem.LETTUCE_SLICE, CookingItem.CLUB_SANDWICH),
        CookingRecipeCombine(CookingItem.CLUB_SANDWICH, CookingItem.PLATE, CookingItem.CLUB_SANDWICH_ON_PLATE),

        CookingRecipeCombine(CookingItem.BREAD_SLICES_HAM, CookingItem.CHEESE_SLICE, CookingItem.UNCOOKED_TOSTI),
        CookingRecipeCombine(CookingItem.TOSTI, CookingItem.PLATE, CookingItem.TOSTI_ON_PLATE),
    ), listOf(
        CookingProduct(CookingItem.HAMBURGER_ON_PLATE, 10, 30 * 20, 5, "\uE020"),
        CookingProduct(CookingItem.CHEESEBURGER_ON_PLATE, 20, 40 * 20, 10, "\uE020"),
        CookingProduct(CookingItem.CLUB_SANDWICH_ON_PLATE, 20, 40 * 20, 10, "\uE020"),
        CookingProduct(CookingItem.TOSTI_ON_PLATE, 20, 40 * 20, 10, "\uE020"),
    ), 2)

    override val game = CookingGame(map)

    override fun isAvailable(): Boolean {
        return !game.busy
    }

    override fun onStart(players: List<MinigamePlayer>) {
        currentPlayers = players
        game.start(players.map { CookingPlayer(it as Player, game, this) })
    }
}