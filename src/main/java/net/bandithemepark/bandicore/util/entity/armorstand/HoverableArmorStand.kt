package net.bandithemepark.bandicore.util.entity.armorstand

import net.bandithemepark.bandicore.util.entity.HoverableEntity

abstract class HoverableArmorStand(override val translationId: String, override val permission: String?): PacketEntityArmorStand(), HoverableEntity