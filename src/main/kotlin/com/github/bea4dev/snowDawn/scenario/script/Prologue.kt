package com.github.bea4dev.snowDawn.scenario.script

import com.github.bea4dev.snowDawn.camera.createCamera
import com.github.bea4dev.snowDawn.coroutine.CoroutineFlagRegistry
import com.github.bea4dev.snowDawn.coroutine.MainThread
import com.github.bea4dev.snowDawn.coroutine.async
import com.github.bea4dev.snowDawn.coroutine.play
import com.github.bea4dev.snowDawn.craft.CraftGUIManager
import com.github.bea4dev.snowDawn.generator.GeneratorRegistry
import com.github.bea4dev.snowDawn.item.ItemRegistry
import com.github.bea4dev.snowDawn.listeners.InventoryListener
import com.github.bea4dev.snowDawn.save.PlayerDataRegistry
import com.github.bea4dev.snowDawn.scenario.DEFAULT_TEXT_BOX
import com.github.bea4dev.snowDawn.scenario.SCENARIO_TICK_THREAD
import com.github.bea4dev.snowDawn.scenario.Scenario
import com.github.bea4dev.snowDawn.scenario.getPlayerSkin
import com.github.bea4dev.snowDawn.scenario.lowSound
import com.github.bea4dev.snowDawn.text.Text
import com.github.bea4dev.snowDawn.world.WorldRegistry
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.camera.CameraPositionAt
import com.github.bea4dev.vanilla_source.api.camera.CameraPositionsManager
import com.github.bea4dev.vanilla_source.api.entity.EngineEntity
import com.github.bea4dev.vanilla_source.api.entity.TickBase
import com.github.bea4dev.vanilla_source.api.nms.entity.NMSEntityController
import com.github.bea4dev.vanilla_source.api.player.EnginePlayer
import com.github.bea4dev.vanilla_source.api.text.TextBox
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import kotlinx.coroutines.delay
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket
import net.minecraft.network.protocol.game.ClientboundTickingStatePacket
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.Vec3
import org.bukkit.*
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.block.data.CraftBlockData
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.time.Duration
import java.util.*
import kotlin.math.pow
import kotlin.random.Random


private val CAMERA1_POSITION = Vector(1.5, 298.0, -0.5)
private val PLAYER_POSITION = Vector(-0.5, 295.5, 0.5)
private val BEAM_START = Vector(32, 267, 28)
private val BEAM_END = Vector(-25, 338, -23)
private val CAMERA3_EXPLOSION_POSITION = Vector(0, 304, 0)

object Prologue : Scenario() {
    override suspend fun run(player: Player) {
        val enginePlayer = EnginePlayer.getEnginePlayer(player)

        black(player)
        blackSpace(player)

        val playerSkin = async {
            getPlayerSkin(player.uniqueId)
        }.await()

        delay(Duration.ofSeconds(3).toMillis())

        MainThread.sync {
            player.gameMode = GameMode.SPECTATOR
            player.inventory.helmet = ItemStack(Material.CARVED_PUMPKIN)
        }.await()

        delay(Duration.ofSeconds(3).toMillis())

        clearBlack(player)

        val airParticle = AirParticle(player)
        airParticle.spawn()

        val camera0 = createCamera(player)
        val camera0Positions = CameraPositionsManager.getCameraPositionsByName("prologue_0")
        camera0.setCameraPositions(camera0Positions)
        camera0.setLookAtPositions(CameraPositionAt(PLAYER_POSITION))
        camera0.prepare()
        camera0.shake(true)

        val camera1 = createCamera(player)
        val camera1Positions = CameraPositionsManager.getCameraPositionsByName("prologue_1")
        camera1.setCameraPositions(camera1Positions)
        camera1.setLookAtPositions(CameraPositionAt(PLAYER_POSITION))
        camera1.prepare()
        camera1.shake(true)

        val camera2 = createCamera(player)
        val camera2Positions = CameraPositionsManager.getCameraPositionsByName("prologue_2")
        camera2.setCameraPositions(camera2Positions)
        camera2.setLookAtPositions(CameraPositionAt(PLAYER_POSITION))
        camera2.prepare()
        camera2.shake(true)

        val camera3 = createCamera(player)
        val camera3Positions = CameraPositionsManager.getCameraPositionsByName("prologue_3")
        camera3.setCameraPositions(camera3Positions)
        camera3.setLookAtPositions(CameraPositionAt(PLAYER_POSITION))
        camera3.prepare()
        camera3.shake(true)

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
        val stand = nmsHandler.createNMSEntityController(
            player.world,
            PLAYER_POSITION.x,
            PLAYER_POSITION.y,
            PLAYER_POSITION.z,
            EntityType.ITEM_DISPLAY,
            null
        )
        npc.bukkitEntity.isSneaking = true
        npc.setRotation(-90.0F, 0.0F)
        npc.show(null, enginePlayer)
        stand.show(null, enginePlayer)

        val mountPacket = nmsHandler.createSetPassengersPacket(stand, intArrayOf(npc.bukkitEntity.entityId))
        nmsHandler.sendPacket(player, mountPacket)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.TUTORIAL_1[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "",
            1,
            Text.TUTORIAL_2[player]
        ).play().await()

        player.playSound(
            camera0Positions!!.getTickPosition(0)!!.toLocation(player.world),
            Sound.ITEM_ELYTRA_FLYING,
            Float.MAX_VALUE,
            0.8F
        )

        camera0.play().await()

        blackSpace(player)
        player.stopSound(Sound.ITEM_ELYTRA_FLYING)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "？？？",
            1,
            Text.TUTORIAL_3[player, player.name]
        ).lowSound().play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.TUTORIAL_4[player]
        ).lowSound().play().await()

        player.playSound(
            CAMERA1_POSITION.toLocation(player.world),
            Sound.ITEM_ELYTRA_FLYING,
            Float.MAX_VALUE,
            0.4F
        )

        camera1.play().await()

        blackSpace(player)
        player.stopSound(Sound.ITEM_ELYTRA_FLYING)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.TUTORIAL_5[player]
        ).lowSound().play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.TUTORIAL_6[player]
        ).lowSound().play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.TUTORIAL_7[player]
        ).lowSound().play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.TUTORIAL_8[player]
        ).lowSound().play().await()

        player.playSound(
            camera2Positions!!.getTickPosition(0)!!.toLocation(player.world),
            Sound.ITEM_ELYTRA_FLYING,
            Float.MAX_VALUE,
            0.8F
        )

        camera2.play().await()

        blackSpace(player)
        player.stopSound(Sound.ITEM_ELYTRA_FLYING)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.TUTORIAL_9[player]
        ).lowSound().play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.TUTORIAL_10[player]
        ).lowSound().play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.TUTORIAL_11[player]
        ).lowSound().play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.TUTORIAL_12[player]
        ).lowSound().play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.LUCAS[player],
            1,
            Text.TUTORIAL_13[player]
        ).lowSound().play().await()

        val camera3Future = camera3.play()

        delay(3500)

        val camera3FirstPosition = camera3Positions!!.getTickPosition(0)!!.toLocation(player.world)
        player.playSound(
            camera3FirstPosition,
            Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,
            Float.MAX_VALUE,
            1.0F
        )

        delay(500)

        val beamVector = BEAM_END.clone().subtract(BEAM_START)
        val beamLength = beamVector.length()

        for (l in 0..beamLength.toInt() step 1) {
            val beamPosition = BEAM_START.clone().add(beamVector.clone().normalize().multiply(l))

            player.spawnParticle(Particle.SONIC_BOOM, beamPosition.toLocation(player.world), 1)
        }

        delay(250)

        player.spawnParticle(Particle.FLASH, CAMERA3_EXPLOSION_POSITION.toLocation(player.world), 1)
        player.spawnParticle(Particle.EXPLOSION, CAMERA3_EXPLOSION_POSITION.toLocation(player.world), 20, 5.0, 5.0, 5.0)

        val radius = 7
        val velocity = 0.3
        for (x in -radius until radius) {
            for (y in -radius until (radius + 10)) {
                for (z in -radius until radius) {
                    if (Random.nextInt(2) != 0) {
                        continue
                    }

                    val blockX = CAMERA3_EXPLOSION_POSITION.blockX + x
                    val blockY = CAMERA3_EXPLOSION_POSITION.blockY + y
                    val blockZ = CAMERA3_EXPLOSION_POSITION.blockZ + z

                    val block = player.world.getBlockAt(blockX, blockY, blockZ)

                    val fallingBlock = FallingBlockEntity(
                        net.minecraft.world.entity.EntityType.FALLING_BLOCK,
                        (player.world as CraftWorld).handle
                    )
                    fallingBlock.setPosRaw(blockX.toDouble(), blockY.toDouble(), blockZ.toDouble())

                    val baseVelocity = beamVector.normalize().multiply(2)
                    fallingBlock.deltaMovement = Vec3(
                        Random.nextDouble(-velocity, velocity),
                        Random.nextDouble(-velocity, velocity),
                        Random.nextDouble(-velocity, velocity)
                    ).add(
                        baseVelocity.x, baseVelocity.y, baseVelocity.z,
                    )

                    val blockSpawnPacket = ClientboundAddEntityPacket(
                        fallingBlock,
                        Block.getId((block.blockData as CraftBlockData).state),
                        BlockPos(blockX, blockY, blockZ)
                    )
                    val velocityPacket = ClientboundSetEntityMotionPacket(fallingBlock)
                    nmsHandler.sendPacket(player, blockSpawnPacket)
                    nmsHandler.sendPacket(player, velocityPacket)

                    player.sendBlockChange(block.location, Material.AIR.createBlockData())
                }
            }
        }

        delay(250)

        airParticle.amount = 1

        val tickPacket = ClientboundTickingStatePacket(2.0F, false)
        nmsHandler.sendPacket(player, tickPacket)

        delay(Duration.ofSeconds(1).toMillis())

        player.playSound(
            camera3FirstPosition,
            Sound.ENTITY_LIGHTNING_BOLT_IMPACT,
            Float.MAX_VALUE,
            0.1F
        )

        delay(Duration.ofSeconds(3).toMillis())

        blackFeedOut(player, 200)

        delay(Duration.ofSeconds(5).toMillis())

        airParticle.remove()

        val cameraPacket = nmsHandler.createCameraPacket(nmsHandler.getNMSPlayer(player))
        nmsHandler.sendPacket(player, cameraPacket)

        val tickResetPacket = ClientboundTickingStatePacket(Bukkit.getServer().serverTickManager.tickRate, false)
        nmsHandler.sendPacket(player, tickResetPacket)

        camera3Future.await()

        delay(Duration.ofSeconds(1).toMillis())

        val spawnRangeStart = GeneratorRegistry.SNOW_LAND.spawnAssetRange.first
        val spawnPosition = spawnRangeStart.clone().add(Vector(3.5, 0.0, 2.5))
        val spawnLocation = spawnPosition.toLocation(WorldRegistry.SNOW_LAND)
        spawnLocation.pitch = 50.0F
        PlayerDataRegistry[player].respawnLocation = spawnLocation.clone()

        MainThread.sync {
            for (y in 0..WorldRegistry.SNOW_LAND.maxHeight) {
                spawnLocation.y = y.toDouble()
                if (spawnLocation.block.type == Material.WAXED_EXPOSED_COPPER) {
                    break
                }
            }
            spawnLocation.y += 2.2

            player.health = 5.0
            player.teleport(spawnLocation)
            player.inventory.clear()
            player.gameMode = GameMode.ADVENTURE
        }.await()

        val sitEntity = nmsHandler.createNMSEntityController(
            WorldRegistry.SNOW_LAND,
            spawnLocation.x,
            spawnLocation.y,
            spawnLocation.z,
            EntityType.ITEM_DISPLAY,
            null
        )
        sitEntity.show(null, enginePlayer)

        val sitPacket = nmsHandler.createSetPassengersPacket(sitEntity, intArrayOf(player.entityId))
        nmsHandler.sendPacket(player, sitPacket)

        val modelLocation = spawnLocation.clone().add(Vector(-2.0, 1.3, 2.0))

        val modelEntity = EngineEntity(
            SCENARIO_TICK_THREAD.threadLocalCache.getGlobalWorld(WorldRegistry.SNOW_LAND.name),
            nmsHandler.createNMSEntityController(
                WorldRegistry.SNOW_LAND,
                modelLocation.x,
                modelLocation.y,
                modelLocation.z,
                EntityType.ITEM_DISPLAY,
                null
            ),
            SCENARIO_TICK_THREAD,
            null
        )
        modelEntity.setGravity(false)
        modelEntity.setModel("benevo_1")
        modelEntity.setRotationLookAt(spawnLocation.x, spawnLocation.y + 1.0, spawnLocation.z)

        val animationHandler = modelEntity.animationHandler!!
        animationHandler.playAnimation("idle", 0.3, 0.3, 1.0, true)

        modelEntity.spawn()

        delay(Duration.ofSeconds(5).toMillis())

        blackFeedIn(player, Duration.ofSeconds(4).toMillis())

        delay(Duration.ofSeconds(5).toMillis())

        animationHandler.playAnimation("talk", 0.3, 0.3, 1.0, true)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "？？？",
            1,
            Text.TUTORIAL_14[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "？？？",
            1,
            Text.TUTORIAL_15[player]
        ).play().await()

        animationHandler.playAnimation("point", 0.3, 0.3, 1.0, false)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            "？？？",
            1,
            Text.TUTORIAL_16[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.BENE[player],
            1,
            Text.TUTORIAL_17[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.BENE[player],
            1,
            Text.TUTORIAL_18[player]
        ).play().await()

        delay(100)

        MainThread.sync {
            player.inventory.addItem(
                ItemStack(Material.CAMPFIRE),
                ItemStack(Material.FLINT_AND_STEEL)
            )
            player.playSound(
                net.kyori.adventure.sound.Sound.sound(
                    Sound.ENTITY_ITEM_PICKUP,
                    net.kyori.adventure.sound.Sound.Source.NEUTRAL,
                    1.0F,
                    Float.MAX_VALUE
                )
            )
        }.await()

        delay(100)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.BENE[player],
            1,
            Text.TUTORIAL_19[player]
        ).play().await()

        animationHandler.stopAnimation("talk")

        delay(100)

        blackFeedOut(player, Duration.ofSeconds(1).toMillis())

        delay(1500)

        modelEntity.kill()

        delay(500)

        blackFeedIn(player, Duration.ofSeconds(1).toMillis())

        delay(Duration.ofSeconds(1).toMillis())

        MainThread.sync {
            player.gameMode = GameMode.SURVIVAL
        }.await()

        val sitDeletePacket = nmsHandler.createEntityDestroyPacket(sitEntity)
        nmsHandler.sendPacket(player, sitDeletePacket)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.BENE[player],
            1,
            Text.TUTORIAL_20[player]
        ).play().await()

        CoroutineFlagRegistry.CAMPFIRE_PLACE[player].future().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.BENE[player],
            1,
            Text.TUTORIAL_21[player]
        ).play().await()

        CoroutineFlagRegistry.CAMPFIRE_CLICK[player].future().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.BENE[player],
            1,
            Text.TUTORIAL_22[player]
        ).play().await()

        player.inventory.addItem(ItemRegistry.SCRAP.createItemStack().also { item -> item.amount = 3 })
        player.inventory.addItem(ItemRegistry.COAL.createItemStack().also { item -> item.amount = 4 })
        player.inventory.addItem(ItemStack(Material.BREAD, 10))

        CraftGUIManager.update(player)
        InventoryListener.closeCheck.add(player)

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.BENE[player],
            1,
            Text.TUTORIAL_23[player]
        ).play().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.BENE[player],
            1,
            Text.TUTORIAL_24[player]
        ).play().await()

        CoroutineFlagRegistry.CLOSE_CRAFT_UI[player].future().await()

        TextBox(
            player,
            DEFAULT_TEXT_BOX,
            Text.BENE[player],
            1,
            Text.TUTORIAL_25[player]
        ).play().await()
    }


    private var blackSpaceEntity: NMSEntityController? = null

    private suspend fun blackSpace(player: Player) {
        MainThread.sync {
            player.teleport(Location(player.world, 0.5, 0.5, 0.5))
        }.await()

        val nmsHandler = VanillaSourceAPI.getInstance().nmsHandler

        if (blackSpaceEntity != null) {
            val destroyPacket = nmsHandler.createEntityDestroyPacket(blackSpaceEntity)
            nmsHandler.sendPacket(player, destroyPacket)
        }
        blackSpaceEntity = nmsHandler.createNMSEntityController(player.world, 0.0, 1.0, 0.0, EntityType.BOAT, null)
        val spawnPacket = nmsHandler.createSpawnEntityPacket(blackSpaceEntity)
        nmsHandler.sendPacket(player, spawnPacket)

        val cameraPacket = nmsHandler.createCameraPacket(blackSpaceEntity)
        nmsHandler.sendPacket(player, cameraPacket)
    }
}

private class AirParticle(private val player: Player) : TickBase {
    private val radius = 30.0
    private val excludeRadius = 2.5
    private val speed = 10.0

    var amount = 5

    override fun tick() {
        for (y in 260 until 300) {
            for (i in 0 until amount) {
                val x = Random.nextDouble(-radius, radius)
                val z = Random.nextDouble(-radius, radius)

                val lengthSquared = x * x + z * z
                if (lengthSquared !in excludeRadius.pow(2)..radius.pow(2)) {
                    continue
                }

                player.spawnParticle(
                    Particle.CLOUD,
                    x + 0.5,
                    y + Random.nextDouble(2.0) - 1.0,
                    z + 0.5,
                    0,
                    0.0,
                    speed,
                    0.0
                )
            }
        }
    }

    fun spawn() {
        SCENARIO_TICK_THREAD.addEntity(this)
    }

    private var remove: Boolean = false
    override fun shouldRemove(): Boolean {
        return remove
    }

    fun remove() {
        remove = true
    }
}
