package net.bandithemepark.bandicore.park.cosmetics

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.park.cosmetics.CosmeticManager.Companion.getOwnedCosmetics
import net.bandithemepark.bandicore.park.cosmetics.requirements.CosmeticRequirement
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class Cosmetic(
    val id: UUID,
    val name: String,
    val displayName: String,
    val description: List<String>,
    val consumable: Boolean,
    val type: CosmeticType,
    val price: Int,
    val requirements: List<CosmeticRequirement>,
    val tags: List<CosmeticTag>
) {

    fun getItemName(): Component {
        return Util.color("<!i><${BandiColors.YELLOW}>$displayName")
    }

    fun getItemStackDescription(): MutableList<Component> {
        val components = mutableListOf<Component>()

        if(tags.isNotEmpty()) {
            for(tag in tags) {
                components.add(Util.color("<!i><${BandiColors.GREEN}>${tag.displayName}"))
            }

            components.add(Util.color(" "))
        }

        for(line in description) {
            components.add(Util.color("<!i><${BandiColors.LIGHT_GRAY}>$line"))
        }

        return components
    }

    fun getShopDescription(player: Player): MutableList<Component> {
        val components = mutableListOf<Component>()

        // Tags
        if(tags.isNotEmpty()) {
            for(tag in tags) {
                components.add(Util.color("<!i><${BandiColors.GREEN}>${tag.displayName}"))
            }
            components.add(Util.color(" "))
        }

        // Description
        for(line in description) {
            components.add(Util.color("<!i><${BandiColors.LIGHT_GRAY}>$line"))
        }
        components.add(Util.color(" "))

        // Requirements
        if(requirements.isNotEmpty()) {
            components.add(Util.color("<!i><${BandiColors.YELLOW}>Requirements"))
            for (requirement in requirements) {
                if (requirement.check(player)) {
                    components.add(Util.color("<!i><${BandiColors.GREEN}>✔ ${requirement.type.getText(requirement.settings)}"))
                } else {
                    components.add(Util.color("<!i><${BandiColors.RED}>✘ ${requirement.type.getText(requirement.settings)}"))
                }
            }
            components.add(Util.color(" "))
        }

        // Buy text and such
        if(player.getOwnedCosmetics()!!.ownedCosmetics.any { it.cosmetic.id === id }) {
            // Already owned, show text to equip
            components.add(Util.color("<!i><${BandiColors.YELLOW}>Click to equip this cosmetic"))
        } else {
            // Not owned, show buy and preview text
            components.add(Util.color("<!i><${BandiColors.YELLOW}>Left click to buy for $price coins"))
            components.add(Util.color("<!i><${BandiColors.YELLOW}>Right click to preview in dressing room"))
        }

        return components
    }

    companion object {
        fun fromJson(json: JsonObject): Cosmetic {
            val id = UUID.fromString(json.get("id").asString)
            val name = json.get("name").asString
            val displayName = json.get("displayName").asString
            val description = json.get("description").asString.split("&&")
            val consumable = json.get("consumable").asBoolean
            val type = CosmeticType.getType(json.get("type").asString)!!
            val price = json.get("price").asInt

            val requirements = mutableListOf<CosmeticRequirement>()
            for(element in JsonParser().parse(json.get("requirements").asString).asJsonArray) {
                val requirement = CosmeticRequirement.fromJson(element.asJsonObject)
                requirements.add(requirement)
            }

            val metadata = JsonParser().parse(json.get("metaData").asString).asJsonObject
            type.onMetadataLoad(metadata)

            val tags = mutableListOf<CosmeticTag>()
            for (element in json.get("itemTags").asString.replace("\"", "").replace("[", "").replace("]", "")
                .replace(", ", ",").split(",")) {
                if(element == "") continue
                try {
                    val tag = CosmeticTag.valueOf(element)
                    tags.add(tag)
                } catch (e: IllegalArgumentException) {
                    Bukkit.getConsoleSender().sendMessage("No tag named $element found for cosmetic $name")
                }
            }

            return Cosmetic(id, name, displayName, description, consumable, type, price, requirements, tags)
        }
    }
}