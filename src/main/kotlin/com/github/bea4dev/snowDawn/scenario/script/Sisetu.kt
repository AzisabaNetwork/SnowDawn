package com.github.bea4dev.snowDawn.scenario.script

import com.github.bea4dev.snowDawn.camera.createCamera
import com.github.bea4dev.snowDawn.coroutine.MainThread
import com.github.bea4dev.snowDawn.coroutine.async
import com.github.bea4dev.snowDawn.coroutine.play
import com.github.bea4dev.snowDawn.scenario.DEFAULT_TEXT_BOX
import com.github.bea4dev.snowDawn.scenario.SCENARIO_TICK_THREAD
import com.github.bea4dev.snowDawn.scenario.Scenario
import com.github.bea4dev.snowDawn.scenario.getPlayerSkin
import com.github.bea4dev.snowDawn.text.Text
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.camera.CameraPositionAt
import com.github.bea4dev.vanilla_source.api.camera.CameraPositionsManager
import com.github.bea4dev.vanilla_source.api.entity.EngineEntity
import com.github.bea4dev.vanilla_source.api.player.EnginePlayer
import com.github.bea4dev.vanilla_source.api.text.TextBox
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import java.time.Duration
import java.util.UUID
import kotlinx.coroutines.time.delay
import net.kyori.adventure.sound.Sound
import net.minecraft.network.protocol.game.ClientboundAnimatePacket
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.data.type.Switch
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

private val PLAYER_POSITION = Vector(1206.0, 257.0, 5.5)
private val CAMERA_POSITION = Vector(1206.5, 257.0, 5.5)
private val MODEL_POSITION = Vector(1207.7, 258.3, 7.6)
private val BUTTON_POSITION = Vector(1206, 258, 5)
private val ANT_POSITION = Vector(1207, 281, 5)

object Sisetu : Scenario() {
    override suspend fun run(player: Player) {
        val enginePlayer = EnginePlayer.getEnginePlayer(player)

        blackFeedOut(player, 1000)

        val playerSkin = async { getPlayerSkin(player.uniqueId) }.await()

        val camera0 = createCamera(player)
        val camera0Positions = CameraPositionsManager.getCameraPositionsByName("sisetu")
        camera0.setCameraPositions(camera0Positions)
        camera0.setLookAtPositions(CameraPositionAt(CAMERA_POSITION))
        camera0.prepare()
        camera0.shake(false)
        camera0.autoEnd(false)

        val camera1 = createCamera(player)
        val camera1Positions = CameraPositionsManager.getCameraPositionsByName("sisetu_ant0")
        val camera1LookAtPositions = CameraPositionsManager.getCameraPositionsByName("sisetu_ant1")
        camera1.setCameraPositions(camera1Positions)
        camera1.setLookAtPositions(camera1LookAtPositions)
        camera1.prepare()
        camera1.shake(false)
        camera1.autoEnd(false)

        val camera2 = createCamera(player)
        val camera2Positions = CameraPositionsManager.getCameraPositionsByName("sisetu")
        camera2.setCameraPositions(camera2Positions)
        camera2.setLookAtPositions(CameraPositionAt(CAMERA_POSITION))
        camera2.prepare()
        camera2.shake(false)
        camera2.autoEnd(false)

        delay(Duration.ofSeconds(1))

        MainThread.sync {
            player.gameMode = GameMode.SPECTATOR
            player.inventory.helmet = ItemStack(Material.CARVED_PUMPKIN)
            player.teleport(CAMERA_POSITION.toLocation(player.world))
        }.await()

        val nmsHandler = VanillaSourceAPI.getInstance().nmsHandler

        val profile = GameProfile(UUID.randomUUID(), player.name)
        profile.properties.put(
            "textures",
            Property("textures", playerSkin.first, playerSkin.second)
        )
        val npc = nmsHandler.createNMSEntityController(
            player.world,
            PLAYER_POSITION.x,
            PLAYER_POSITION.y,
            PLAYER_POSITION.z,
            EntityType.PLAYER,
            profile
        )
        npc.bukkitEntity.isSneaking = true
        npc.setRotation(-39.5F, -7.0F)
        npc.show(null, enginePlayer)

        val modelEntity = EngineEntity(
            SCENARIO_TICK_THREAD.threadLocalCache.getGlobalWorld(
                WorldRegistry.SNOW_LAND.name
            ),
            nmsHandler.createNMSEntityController(
                WorldRegistry.SNOW_LAND,
                MODEL_POSITION.x,
                MODEL_POSITION.y,
                MODEL_POSITION.z,
                EntityType.ITEM_DISPLAY,
                null
            ),
            SCENARIO_TICK_THREAD,
            null
        )
        modelEntity.setGravity(false)
        modelEntity.setModel("benevo_1")
        modelEntity.setRotationLookAt(PLAYER_POSITION.x, PLAYER_POSITION.y + 1, PLAYER_POSITION.z)

        val animationHandler = modelEntity.animationHandler!!
        animationHandler.playAnimation("idle", 0.3, 0.3, 1.0, true)

        modelEntity.spawn()

        delay(Duration.ofSeconds(1))

        player.playSound(
            Sound.sound(
                org.bukkit.Sound.BLOCK_IRON_DOOR_OPEN,
                Sound.Source.MASTER,
                Float.MAX_VALUE,
                1.0F
            )
        )

        delay(Duration.ofSeconds(1))

        player.playSound(
            Sound.sound(
                org.bukkit.Sound.BLOCK_IRON_DOOR_CLOSE,
                Sound.Source.MASTER,
                Float.MAX_VALUE,
                1.0F
            )
        )

        delay(Duration.ofSeconds(1))

        camera0.start()

        blackFeedIn(player, 500)

        delay(Duration.ofSeconds(1))

        animationHandler.playAnimation("talk", 0.3, 0.3, 1.0, true)

        TextBox(player, DEFAULT_TEXT_BOX, Text.BENE[player], 1, Text.SISETU_0[player, player.name])
            .play()
            .await()

        TextBox(player, DEFAULT_TEXT_BOX, Text.BENE[player], 1, Text.SISETU_1[player, player.name])
            .play()
            .await()

        TextBox(player, DEFAULT_TEXT_BOX, Text.BENE[player], 1, Text.SISETU_2[player, player.name])
            .play()
            .await()

        TextBox(player, DEFAULT_TEXT_BOX, Text.BENE[player], 1, Text.SISETU_3[player, player.name])
            .play()
            .await()

        TextBox(player, DEFAULT_TEXT_BOX, Text.BENE[player], 1, Text.SISETU_4[player, player.name])
            .play()
            .await()

        animationHandler.playAnimation("point", 0.3, 0.3, 1.0, true)

        TextBox(player, DEFAULT_TEXT_BOX, Text.BENE[player], 1, Text.SISETU_5[player, player.name])
            .play()
            .await()

        animationHandler.stopAnimation("talk")

        // 頷く
        delay(Duration.ofMillis(50))
        npc.setRotation(-30.0F, -7.0F)
        npc.playTickResult(null, enginePlayer, true)
        delay(Duration.ofMillis(50))
        npc.setRotation(-30.0F, 10.0F)
        npc.playTickResult(null, enginePlayer, true)
        delay(Duration.ofMillis(50))
        npc.setRotation(-30.0F, 15.0F)
        npc.playTickResult(null, enginePlayer, true)
        delay(Duration.ofMillis(50))
        npc.setRotation(-30.0F, 20.0F)
        npc.playTickResult(null, enginePlayer, true)
        delay(Duration.ofMillis(50))
        npc.setRotation(-30.0F, 20.0F)
        npc.playTickResult(null, enginePlayer, true)
        delay(Duration.ofMillis(50))
        npc.setRotation(-30.0F, 15.0F)
        npc.playTickResult(null, enginePlayer, true)
        delay(Duration.ofMillis(50))
        npc.setRotation(-30.0F, 10.0F)
        npc.playTickResult(null, enginePlayer, true)
        delay(Duration.ofMillis(50))
        npc.setRotation(-30.0F, -7.0F)
        npc.playTickResult(null, enginePlayer, true)

        delay(Duration.ofMillis(500))

        // スイッチを見る
        delay(Duration.ofMillis(50))
        npc.setRotation(-53.0F, 6.0F)
        npc.playTickResult(null, enginePlayer, true)
        delay(Duration.ofMillis(50))
        npc.setRotation(-72.0F, 7.0F)
        npc.playTickResult(null, enginePlayer, true)
        delay(Duration.ofMillis(50))
        npc.setRotation(-83.0F, 7.0F)
        npc.playTickResult(null, enginePlayer, true)
        delay(Duration.ofMillis(50))
        npc.setRotation(-85.0F, 7.0F)
        npc.playTickResult(null, enginePlayer, true)

        delay(Duration.ofSeconds(1))

        // 腕を振る
        val animationPacket =
            ClientboundAnimatePacket(npc as net.minecraft.world.entity.Entity, ClientboundAnimatePacket.SWING_MAIN_HAND)
        nmsHandler.sendPacket(player, animationPacket)

        player.playSound(
            Sound.sound(
                org.bukkit.Sound.BLOCK_IRON_DOOR_OPEN,
                Sound.Source.MASTER,
                Float.MAX_VALUE,
                1.0F
            )
        )

        MainThread.sync {
            val buttonBlock = BUTTON_POSITION.toLocation(player.world).block
            val buttonData = buttonBlock.blockData
            if (buttonData is Switch) {
                buttonData.isPowered = true
                buttonBlock.blockData = buttonData
            }
        }.await()

        delay(Duration.ofMillis(500))

        player.playSound(
            Sound.sound(
                org.bukkit.Sound.BLOCK_BEACON_ACTIVATE,
                Sound.Source.MASTER,
                Float.MAX_VALUE,
                1.0F
            )
        )

        delay(Duration.ofMillis(500))

        blackFeedOut(player, 1000)

        delay(Duration.ofSeconds(1))

        camera0.end()
        camera1.start()

        blackFeedIn(player, 1000)

        delay(Duration.ofSeconds(2))

        player.playSound(
            Sound.sound(
                org.bukkit.Sound.BLOCK_BEACON_ACTIVATE,
                Sound.Source.MASTER,
                Float.MAX_VALUE,
                1.0F
            )
        )

        delay(Duration.ofSeconds(2))

        blackFeedOut(player, 1000)

        delay(Duration.ofSeconds(1))

        camera1.end()
        camera2.start()

        blackFeedIn(player, 1000)

        delay(Duration.ofSeconds(3))

        camera2.end()
    }
}
