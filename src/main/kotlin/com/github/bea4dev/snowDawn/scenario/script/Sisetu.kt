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
import kotlinx.coroutines.time.delay
import net.kyori.adventure.sound.Sound
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.time.Duration
import java.util.UUID

private val PLAYER_POSITION = Vector(1206.0, 257.0, 5.5)
private val CAMERA_POSITION = Vector(1206.5, 257.0, 5.5)
private val MODEL_POSITION = Vector(1207.7, 258.0, 7.6)

object Sisetu : Scenario() {
    override suspend fun run(player: Player) {
        val enginePlayer = EnginePlayer.getEnginePlayer(player)

        blackFeedOut(player, 500)

        val playerSkin = async {
            getPlayerSkin(player.uniqueId)
        }.await()

        val camera0 = createCamera(player)
        val camera0Positions = CameraPositionsManager.getCameraPositionsByName("sisetu")
        camera0.setCameraPositions(camera0Positions)
        camera0.setLookAtPositions(CameraPositionAt(CAMERA_POSITION))
        camera0.prepare()
        camera0.shake(false)

        MainThread.sync {
            player.gameMode = GameMode.SPECTATOR
            player.inventory.helmet = ItemStack(Material.CARVED_PUMPKIN)
        }.await()

        val nmsHandler = VanillaSourceAPI.getInstance().nmsHandler

        val profile = GameProfile(UUID.randomUUID(), player.name)
        profile.properties.put("textures", Property("textures", playerSkin.first, playerSkin.second))
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
            SCENARIO_TICK_THREAD.threadLocalCache.getGlobalWorld(WorldRegistry.SNOW_LAND.name),
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
        modelEntity.setRotationLookAt(PLAYER_POSITION.x, PLAYER_POSITION.y + 1.5, PLAYER_POSITION.z)

        val animationHandler = modelEntity.animationHandler!!
        animationHandler.playAnimation("idle", 0.3, 0.3, 1.0, true)

        modelEntity.spawn()

        delay(Duration.ofSeconds(1))

        player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_IRON_DOOR_OPEN, Sound.Source.MASTER, Float.MAX_VALUE, 1.0F))

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

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.BENE[player],
            1,
            Text.SISETU_0[player]
        ).play().await()

        camera0.end()
    }
}