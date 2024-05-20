package net.bandithemepark.bandicore.park.cosmetics

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandicore.park.cosmetics.requirements.CosmeticRequirement
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.kyori.adventure.text.Component
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
            for(element in json.get("itemTags").asString.replace("\"", "").replace("[", "").replace("]", "").replace(", ", ",").split(",")) {
                val tag = CosmeticTag.valueOf(element)
                tags.add(tag)
            }

            return Cosmetic(id, name, displayName, description, consumable, type, price, requirements, tags)
        }
    }
}