package net.bandithemepark.bandicore.util.entity.display

import net.bandithemepark.bandicore.util.entity.HoverableEntity

abstract class HoverableItemDisplay(override val translationId: String, override val permission: String?): PacketItemDisplay(), HoverableEntity {
    override val detectionOffset: Double
        get() = 0.0
}
