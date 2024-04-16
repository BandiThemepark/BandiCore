package net.bandithemepark.bandicore.server.minigames.cooking.item

import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.Util
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

enum class CookingItem(val itemStack: ItemStack) {
    PLATE(ItemFactory(Material.HEAVY_WEIGHTED_PRESSURE_PLATE).setDisplayName(Util.color("<!i><#FFFFFF>Plate")).build()),

    // ham
    HAM(ItemFactory(Material.PORKCHOP).setDisplayName(Util.color("<!i><#FFFFFF>Ham")).build()),
    // burger
    BURGER(ItemFactory(Material.BEEF).setDisplayName(Util.color("<!i><#FFFFFF>Burger")).build()),
    // lettuce
    LETTUCE(ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(0).setDisplayName(Util.color("<!i><#FFFFFF>Lettuce")).build()),
    // tomato
    TOMATO(ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(32).setDisplayName(Util.color("<!i><#FFFFFF>Tomato")).build()),
    // cheese slice
    CHEESE_SLICE(ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(29).setDisplayName(Util.color("<!i><#FFFFFF>Cheese slice")).build()),
    // burger bun
    BURGER_BUN(ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(28).setDisplayName(Util.color("<!i><#FFFFFF>Burger bun")).build()),
    // slice of bread
    BREAD_SLICE(ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(0).setDisplayName(Util.color("<!i><#FFFFFF>Slice of bread")).build()),

    // HAMBURGER
    // cooked burger
    COOKED_BURGER(ItemFactory(Material.COOKED_BEEF).setDisplayName(Util.color("<!i><#FFFFFF>Cooked burger")).build()),
    // burnt burger
    BURNT_BURGER(ItemFactory(Material.GUNPOWDER).setDisplayName(Util.color("<!i><#FFFFFF>Burnt burger")).build()),
    // lettuce slice
    LETTUCE_SLICE(ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(30).setDisplayName(Util.color("<!i><#FFFFFF>Lettuce slices")).build()),
    // tomate slice
    TOMATO_SLICE(ItemFactory(Material.DIAMOND_SHOVEL).setCustomModelData(31).setDisplayName(Util.color("<!i><#FFFFFF>Tomato slices")).build()),
    // burger bun + lettuce slice
    BURGER_BUN_LETTUCE_SLICE(ItemFactory(Material.BROWN_CARPET).setDisplayName(Util.color("<!i><#FFFFFF>Burger bun with lettuce slices")).build()),
    // burger bun + lettuce slide + tomato slice
    BURGER_BUN_LETTUCE_SLICE_TOMATO_SLICE(ItemFactory(Material.BROWN_CARPET).setDisplayName(Util.color("<!i><#FFFFFF>Burger bun with lettuce and tomato slices")).build()),
    // burger bun + lettuce slice + tomato slice + cooked burger
    HAMBURGER(ItemFactory(Material.BROWN_CARPET).setDisplayName(Util.color("<!i><#FFFFFF>Hamburger")).build()),
    // hamburger (everything but on a plate)
    HAMBURGER_ON_PLATE(ItemFactory(Material.RED_CARPET).setDisplayName(Util.color("<!i><#FFFFFF>Hamburger on a plate")).build()),

    // CHEESEBURGER
    // burger with cheese
    BURGER_WITH_CHEESE(ItemFactory(Material.BEEF).setDisplayName(Util.color("<!i><#FFFFFF>Burger with cheese")).build()),
    // cooked burger with cheese
    COOKED_BURGER_WITH_CHEESE(ItemFactory(Material.COOKED_BEEF).setDisplayName(Util.color("<!i><#FFFFFF>Cooked burger with cheese")).build()),
    // burnt burger with cheese
    BURNT_BURGER_WITH_CHEESE(ItemFactory(Material.GUNPOWDER).setDisplayName(Util.color("<!i><#FFFFFF>Burnt burger with cheese")).build()),
    // burger bun + cooked burger with cheese
    CHEESEBURGER(ItemFactory(Material.BROWN_CARPET).setDisplayName(Util.color("<!i><#FFFFFF>Cheeseburger")).build()),
    // cheeseburger (everything but on a plate)
    CHEESEBURGER_ON_PLATE(ItemFactory(Material.RED_CARPET).setDisplayName(Util.color("<!i><#FFFFFF>Cheeseburger on a plate")).build()),

    // CLUB SANDWICH
    // bread slices + ham
    BREAD_SLICES_HAM(ItemFactory(Material.BROWN_CARPET).setDisplayName(Util.color("<!i><#FFFFFF>Bread slices with ham")).build()),
    // bread slices + ham + lettuce slice
    CLUB_SANDWICH(ItemFactory(Material.BROWN_CARPET).setDisplayName(Util.color("<!i><#FFFFFF>Club sandwich")).build()),
    // club sandwich (everything but on a plate)
    CLUB_SANDWICH_ON_PLATE(ItemFactory(Material.RED_CARPET).setDisplayName(Util.color("<!i><#FFFFFF>Club sandwich on a plate")).build()),

    // TOSTI
    // bread slices + ham + cheese slice
    UNCOOKED_TOSTI(ItemFactory(Material.BROWN_CARPET).setDisplayName(Util.color("<!i><#FFFFFF>Uncooked tosti")).build()),
    // cooked tosti
    TOSTI(ItemFactory(Material.BROWN_CARPET).setDisplayName(Util.color("<!i><#FFFFFF>Tosti")).build()),
    // burnt tosti
    BURNT_TOSTI(ItemFactory(Material.GUNPOWDER).setDisplayName(Util.color("<!i><#FFFFFF>Burnt tosti")).build()),
    // tosti (everything but on a plate)
    TOSTI_ON_PLATE(ItemFactory(Material.RED_CARPET).setDisplayName(Util.color("<!i><#FFFFFF>Tosti on a plate")).build());

    fun getName(): String {
        return name.replace("_", " ").lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}