package net.bandithemepark.bandicore.park.cosmetics.dressingroom

import net.bandithemepark.bandicore.BandiCore
import net.bandithemepark.bandicore.park.cosmetics.Cosmetic
import net.bandithemepark.bandicore.park.cosmetics.CosmeticManager.Companion.getEquipped
import net.bandithemepark.bandicore.park.cosmetics.OwnedCosmetic
import net.bandithemepark.bandicore.park.cosmetics.types.TitleCosmetic
import net.bandithemepark.bandicore.park.shops.Shop
import net.bandithemepark.bandicore.park.shops.ShopMenu
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerRig
import net.bandithemepark.bandicore.server.custom.player.CustomPlayerSkin.Companion.getAdaptedSkin
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag
import net.bandithemepark.bandicore.server.essentials.ranks.nametag.PlayerNameTag.Companion.getNameTag
import net.bandithemepark.bandicore.util.Util
import net.bandithemepark.bandicore.util.chat.BandiColors
import net.bandithemepark.bandicore.util.entity.HoverableEntity
import net.bandithemepark.bandicore.util.entity.PacketEntity
import net.bandithemepark.bandicore.util.entity.armorstand.PacketEntityArmorStand
import net.bandithemepark.bandicore.util.entity.display.PacketTextDisplay
import net.bandithemepark.bandicore.util.math.Quaternion
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.*
import org.bukkit.util.Vector
import java.time.Duration

class DressingRoomSession(
    val player: Player,
    val dressingRoom: DressingRoom,
    val previewCosmetic: Cosmetic? = null,
    val previewShop: Shop? = null
) {
    val beforeGameMode = player.gameMode
    val beforeLocation = player.location.clone()

    lateinit var bukkitEntity: ArmorStand
    lateinit var vehicleEntity: PacketEntityArmorStand
    lateinit var customPlayer: CustomPlayerRig

    var exited = false

    init {
        setupCamera()
        setupCustomPlayer()
        startView()

        activeSessions.add(this)
    }

    private fun setupCustomPlayer() {
        customPlayer = CustomPlayerRig(player.getAdaptedSkin())
        customPlayer.visibilityType = PacketEntity.VisibilityType.WHITELIST
        customPlayer.visibilityList = mutableListOf(player)
        customPlayer.spawn(dressingRoom.playerPosition.toLocation(dressingRoom.world), null)
        customPlayer.moveTo(dressingRoom.playerPosition, Quaternion.fromYawPitchRoll(0.0, dressingRoom.playerYaw, 0.0))

        val hat = player.getEquipped("hat")
        if(hat != null) customPlayer.setHat(hat.cosmetic.type.getDressingRoomItem(player, hat.color, hat.cosmetic))

        val handheld = player.getEquipped("handheld")
        if(handheld != null) customPlayer.setHandheld(handheld.cosmetic.type.getDressingRoomItem(player, handheld.color, handheld.cosmetic))

        showPreviewCosmetic()

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            customPlayer.playAnimationOnce("dressing_room_enter") { playRandomIdleAnimation() }
        }, 10)

        spawnNameTag()
        if (player.getEquipped("title") != null) spawnTitle()
    }

    private fun showPreviewCosmetic() {
        if(previewCosmetic == null) return

        if(previewCosmetic.type.id == "hat") {
            customPlayer.setHat(previewCosmetic.type.getDressingRoomItem(player, null, previewCosmetic))
        } else if(previewCosmetic.type.id == "handheld") {
            customPlayer.setHandheld(previewCosmetic.type.getDressingRoomItem(player, null, previewCosmetic))
        }
    }

    val idleAnimations = listOf("dressing_room_idle_1", "dressing_room_idle_2")
    private fun playRandomIdleAnimation() {
        val animationId = idleAnimations.random()
        customPlayer.playAnimationOnce(animationId) { playRandomIdleAnimation() }
    }

    fun playAnimation(animationName: String) {
        customPlayer.playAnimationOnce(animationName) { playRandomIdleAnimation() }
    }

    private fun removeCustomPlayer() {
        customPlayer.deSpawn()
        deSpawnNameTag()
        deSpawnTitle()
    }

    fun exit() {
        stopView()

        activeSessions.remove(this)
    }

    private fun setupCamera() {
        bukkitEntity = dressingRoom.world.spawnEntity(dressingRoom.cameraPosition.toLocation(dressingRoom.world,
            dressingRoom.cameraYaw.toFloat(), dressingRoom.cameraPitch.toFloat()
        ), EntityType.ARMOR_STAND) as ArmorStand
        bukkitEntity.isPersistent = false
        bukkitEntity.isInvisible = true
        bukkitEntity.isMarker = true
        bukkitEntity.setGravity(false)

        vehicleEntity = PacketEntityArmorStand()
        vehicleEntity.spawn(dressingRoom.cameraPosition.toLocation(dressingRoom.world,
            dressingRoom.cameraYaw.toFloat(), dressingRoom.cameraPitch.toFloat()
        ).clone().add(Vector(0.0, -2.0, 0.0)))

        vehicleEntity.handle.isNoGravity = true
        vehicleEntity.handle.isInvisible = true
        (vehicleEntity.handle as net.minecraft.world.entity.decoration.ArmorStand).isMarker = true
        vehicleEntity.updateMetadata()
    }

    private fun removeCamera() {
        bukkitEntity.remove()
        vehicleEntity.deSpawn()
    }

    private fun startView() {
        player.showTitle(
            Title.title(
                Component.text("\uE000"),
                Component.text(""),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(100), Duration.ofMillis(500))
            )
        )

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            player.teleport(dressingRoom.cameraPosition.toLocation(dressingRoom.world))
        }, 10)

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            startCamera()
        }, 12)
    }

    private fun stopView() {
        player.showTitle(
            Title.title(
                Component.text("\uE000"),
                Component.text(""),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(0), Duration.ofMillis(500))
            )
        )

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            stopCamera()
            removeCustomPlayer()
            removeCamera()
        }, 10)

        if(previewShop != null) {
            Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
                ShopMenu(player, previewShop)
            }, 13)
        }
    }

    private fun startCamera() {
        player.getNameTag()!!.hidden = true
        exited = false

        HoverableEntity.timer.movements[player] = player.location.clone()

        vehicleEntity.addPassenger(player)
        vehicleEntity.updatePassengers()

        player.gameMode = GameMode.SPECTATOR
        (player as CraftPlayer).handle.connection.send(ClientboundSetCameraPacket((bukkitEntity as CraftArmorStand).handle))
        player.handle.connection.resetPosition()

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            (player).handle.connection.send(ClientboundSetCameraPacket((bukkitEntity as CraftArmorStand).handle))
            player.handle.connection.resetPosition()
        }, 2)
    }

    private fun stopCamera() {
        player.getNameTag()!!.hidden = false

        vehicleEntity.removePassenger(player)
        vehicleEntity.updatePassengers()

        player.gameMode = beforeGameMode
        (player as CraftPlayer).handle.connection.send(ClientboundSetCameraPacket(player.handle))

        Bukkit.getScheduler().runTaskLater(BandiCore.instance, Runnable {
            player.teleport(beforeLocation)
        }, 2)
    }

    fun onTick() {
        player.sendActionBar(Util.color("<${BandiColors.YELLOW}>JUMP to equip | CROUCH to exit"))
    }

    val equipAnimations = hashMapOf(
        "hat" to listOf("dressing_room_equip_hat_1", "dressing_room_equip_hat_2")
    )

    fun equipCosmetic(ownedCosmetic: OwnedCosmetic) {
        val cosmetic = ownedCosmetic.cosmetic

        // Equip cosmetic
        BandiCore.instance.cosmeticManager.equip(player, cosmetic, ownedCosmetic.color)

        // Update custom player
        if(cosmetic.type.id == "hat") {
            customPlayer.setHat(cosmetic.type.getDressingRoomItem(player, ownedCosmetic.color, cosmetic))
        } else if(cosmetic.type.id == "handheld") {
            customPlayer.setHandheld(cosmetic.type.getDressingRoomItem(player, ownedCosmetic.color, cosmetic))
        } else if(cosmetic.type.id == "title") {
            deSpawnTitle()
            spawnTitle()
        }

        // Play animation if present
        val animations = equipAnimations[cosmetic.type.id]
        if(!animations.isNullOrEmpty()) playAnimation(animations.random())
    }

    val unEquipAnimations = hashMapOf(
        "hat" to listOf("dressing_room_equip_hat_1")
    )

    fun unEquip(typeId: String) {
        BandiCore.instance.cosmeticManager.unEquip(player, typeId)

        // Update custom player
        if(typeId == "hat") {
            customPlayer.setHat(null)
        } else if(typeId == "handheld") {
            customPlayer.setHandheld(null)
        } else if(typeId == "title") {
            deSpawnTitle()
        }

        // Play animation if present
        val animations = unEquipAnimations[typeId]
        if(!animations.isNullOrEmpty()) playAnimation(animations.random())
    }

    private var textDisplay: PacketTextDisplay? = null
    private var titleDisplay: PacketTextDisplay? = null

    private fun spawnNameTag() {
        textDisplay = PacketTextDisplay()
        textDisplay!!.visibilityType = PacketEntity.VisibilityType.WHITELIST
        textDisplay!!.visibilityList = mutableListOf(player)

        textDisplay!!.spawn(customPlayer.animatronic.basePosition.toLocation(player.world).add(0.0, 2.0 + player.getNameTag()!!.heightOffset, 0.0))

        val rank = BandiCore.instance.server.rankManager.loadedPlayerRanks[player]!!
        val text = Util.color("<${rank.color}>${rank.name} ${player.name}")
        textDisplay!!.setText(text)
        textDisplay!!.setBillboard(Display.Billboard.CENTER)
        textDisplay!!.setDefaultBackground(true)
        textDisplay!!.setSeeThrough(false)
        textDisplay!!.setAlignment(TextDisplay.TextAlignment.CENTER)
        textDisplay!!.updateMetadata()
    }

    private fun deSpawnNameTag() {
        textDisplay!!.deSpawn()
        textDisplay = null
    }

    private fun spawnTitle() {
        if(titleDisplay != null) return

        titleDisplay = PacketTextDisplay()
        val title = Util.color((player.getEquipped("title")!!.cosmetic.type as TitleCosmetic).text!!)
        titleDisplay!!.visibilityType = PacketEntity.VisibilityType.WHITELIST
        titleDisplay!!.visibilityList = mutableListOf(player)

        titleDisplay!!.spawn(customPlayer.animatronic.basePosition.toLocation(player.world).add(0.0, 2.0 + player.getNameTag()!!.heightOffset + PlayerNameTag.TITLE_HEIGHT_OFFSET, 0.0))

        titleDisplay!!.setBillboard(Display.Billboard.CENTER)
        titleDisplay!!.setDefaultBackground(true)
        titleDisplay!!.setSeeThrough(false)
        titleDisplay!!.setAlignment(TextDisplay.TextAlignment.CENTER)
        titleDisplay!!.setText(title)
        titleDisplay!!.updateMetadata()
    }

    private fun deSpawnTitle() {
        if(titleDisplay == null) return
        titleDisplay!!.deSpawn()
        titleDisplay = null
    }

    companion object {
        val activeSessions = mutableListOf<DressingRoomSession>()
    }
}