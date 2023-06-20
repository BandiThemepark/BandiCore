package net.bandithemepark.bandicore.server.animation.rig

import com.google.gson.JsonObject
import net.bandithemepark.bandicore.server.animation.Channel
import net.bandithemepark.bandicore.server.animation.Interpolation
import net.bandithemepark.bandicore.server.animation.KeyFrame
import net.bandithemepark.bandicore.server.animation.KeyFrameData
import net.bandithemepark.bandicore.util.ItemFactory
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.math.MathUtil
import net.bandithemepark.bandicore.util.math.Matrix
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import net.minecraft.world.entity.decoration.ArmorStand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector

class RigPart(val name: String, val children: MutableList<RigPart>, var itemStack: ItemStack?, val baseOffset: Vector, val baseRotation: Vector, var hasModel: Boolean = true) {
    var spawnOrder = Integer.MAX_VALUE
    var offset = Vector()
    var rotation = Vector()
    var type = Type.HEAD
    var parent = null as String?

    fun getAllParts(): List<RigPart> {
        val parts = mutableListOf<RigPart>()
        parts.add(this)
        children.forEach { parts.addAll(it.getAllParts()) }
        return parts
    }

    val armorStand = PacketEntityArmorStand()
    fun spawn(location: Location) {
        if(!hasModel) return
        armorStand.spawn(location.clone().add(type.staticOffset))
        armorStand.handle!!.isInvisible = true
        (armorStand.handle!! as ArmorStand).isMarker = true
        type.applyModel(itemStack!!, armorStand)
    }

    fun deSpawn() {
        if(!hasModel) return
        armorStand.deSpawn()
    }

    fun update(position: Vector, rotation: EulerAngle, rotationQuaternion: Quaternion) {
        val newPosition = position.clone().add(getPosition(baseOffset.clone().add(offset), rotationQuaternion))

        var newRotation = EulerAngle(rotation.x, rotation.y, rotation.z)
        newRotation = newRotation.add(Math.toRadians(baseRotation.x + this.rotation.x), Math.toRadians(baseRotation.y + this.rotation.y), Math.toRadians(baseRotation.z + this.rotation.z))

        if(hasModel) updateArmorStand(newPosition.clone(), newRotation.clone(), rotationQuaternion.clone())

        for(child in children) {
            child.update(newPosition.clone(), newRotation.clone(), rotationQuaternion.clone())
        }
    }

    private fun updateArmorStand(position: Vector, rotation: EulerAngle, rotationQuaternion: Quaternion) {
        val newPosition = position.clone().add(type.staticOffset)
        armorStand.moveEntity(newPosition.x, newPosition.y, newPosition.z)
        type.applyPose(armorStand, Math.toDegrees(rotation.x), Math.toDegrees(rotation.y), Math.toDegrees(rotation.z))
    }


//    fun update(position: Vector, rotation: Vector, rotationQuaternion: Quaternion) {
//        val newPosition = position.clone().add(getPosition(baseOffset.clone().add(offset), rotationQuaternion))
//        val newRotation = rotationQuaternion.clone()
//        newRotation.multiply(Quaternion.fromYawPitchRoll(baseRotation.x, baseRotation.y, baseRotation.z))
//        newRotation.multiply(Quaternion.fromYawPitchRoll(this.rotation.x, this.rotation.y, this.rotation.z))
//
//        if(hasModel) updateArmorStand(newPosition.clone(), rotation.clone(), newRotation.clone())
//
//        for(child in children) {
//            child.update(newPosition.clone(), rotation.clone(), newRotation.clone())
//        }
//    }
//
//    private fun updateArmorStand(position: Vector, rotation: Vector, rotationQuaternion: Quaternion) {
//        val newPosition = position.clone().add(type.staticOffset)
//        armorStand.moveEntity(newPosition.x, newPosition.y, newPosition.z)
//
//        val armorStandPose = MathUtil.getArmorStandPose(rotationQuaternion)
//        type.applyPose(armorStand, Math.toDegrees(armorStandPose.x), Math.toDegrees(armorStandPose.y), Math.toDegrees(armorStandPose.z))
//    }

    enum class Type(val staticOffset: Vector) {
        HEAD(Vector(0.0, -1.4375, 0.0)) {
            override fun applyModel(model: ItemStack, armorStand: PacketEntityArmorStand) {
                armorStand.helmet = model
                armorStand.setHeadPose(0.0, 0.0, 0.0)
            }

            override fun applyPose(armorStand: PacketEntityArmorStand, x: Double, y: Double, z: Double) {
                armorStand.setHeadPose(x, y, z)
            }
        },
        RIGHT_ARM(Vector(0.3125, -1.375, 0.0)) {
            override fun applyModel(model: ItemStack, armorStand: PacketEntityArmorStand) {
                armorStand.itemInMainHand = model
                armorStand.setArmsVisible()
                armorStand.setRightArmPose(0.0, 0.0, 0.0)
            }

            override fun applyPose(armorStand: PacketEntityArmorStand, x: Double, y: Double, z: Double) {
                armorStand.setRightArmPose(x, y, z)
            }
        },
        LEFT_ARM(Vector(-0.3125, -1.375, 0.0)) {
            override fun applyModel(model: ItemStack, armorStand: PacketEntityArmorStand) {
                armorStand.itemInOffHand = model
                armorStand.setArmsVisible()
                armorStand.setLeftArmPose(0.0, 0.0, 0.0)
            }

            override fun applyPose(armorStand: PacketEntityArmorStand, x: Double, y: Double, z: Double) {
                armorStand.setLeftArmPose(x, y, z)
            }
        };

        abstract fun applyModel(model: ItemStack, armorStand: PacketEntityArmorStand)
        abstract fun applyPose(armorStand: PacketEntityArmorStand, x: Double, y: Double, z: Double)
    }

    fun getPosition(position: Vector, rotation: Quaternion): Vector {
        val rotationMatrix = rotation.toMatrix()

        val coordinateMatrix = Matrix(4, 1)
        coordinateMatrix.m[0][0] = position.x
        coordinateMatrix.m[1][0] = position.y
        coordinateMatrix.m[2][0] = position.z
        coordinateMatrix.m[3][0] = 1.0

        val newMatrix = Matrix.multiply(rotationMatrix, coordinateMatrix)
        return Vector(newMatrix.m[0][0], newMatrix.m[1][0], newMatrix.m[2][0])
    }

    fun toJson(): JsonObject {
        val json = JsonObject()

        json.addProperty("name", name)
        if(parent != null) json.addProperty("parent", parent)

        if(itemStack != null) {
            json.addProperty("material", itemStack!!.type.toString())
            json.addProperty("customModelData", itemStack!!.itemMeta.customModelData)
        }

        json.add("baseOffset", vectorToJson(baseOffset))
        json.add("baseRotation", vectorToJson(baseRotation))
        json.addProperty("spawnOrder", spawnOrder)
        json.addProperty("type", type.toString())

        return json
    }

    companion object {
        fun EulerAngle.clone(): EulerAngle {
            return EulerAngle(this.x, this.y, this.z)
        }

        fun getFromJson(json: JsonObject): RigPart {
            val name = json.get("name").asString

            var parent = null as String?
            if(json.has("parent")) parent = json.get("parent").asString

            var itemStack = null as ItemStack?

            if(json.has("material")) {
                val material = json.get("material").asString

                var customModelData = 0
                if (json.has("customModelData")) customModelData = json.get("customModelData").asInt

                itemStack = ItemFactory(Material.matchMaterial(material.uppercase())).setCustomModelData(customModelData).build()
            }

            val baseOffset = getVectorFromJson(json.getAsJsonObject("baseOffset"))
            val baseRotation = getVectorFromJson(json.getAsJsonObject("baseRotation"))

            val part = RigPart(name, mutableListOf(), null, baseOffset, baseRotation, itemStack != null)
            part.parent = parent
            part.spawnOrder = json.get("spawnOrder").asInt
            part.type = Type.valueOf(json.get("type").asString.uppercase())

            return part
        }

        fun getVectorFromJson(json: JsonObject): Vector {
            return Vector(json.get("x").asDouble, json.get("y").asDouble, json.get("z").asDouble)
        }

        fun vectorToJson(vector: Vector): JsonObject {
            val json = JsonObject()
            json.addProperty("x", vector.x)
            json.addProperty("y", vector.y)
            json.addProperty("z", vector.z)
            return json
        }
    }
}