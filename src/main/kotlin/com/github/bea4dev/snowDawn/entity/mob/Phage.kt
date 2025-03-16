package com.github.bea4dev.snowDawn.entity.mob

import com.github.bea4dev.snowDawn.SnowDawn
import com.github.bea4dev.snowDawn.player.PlayerManager
import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.entity.EngineEntity
import com.github.bea4dev.vanilla_source.api.entity.ai.navigation.GoalSelector
import com.github.bea4dev.vanilla_source.api.entity.ai.navigation.Navigator
import com.github.bea4dev.vanilla_source.api.entity.ai.navigation.goal.PathfindingGoal
import com.github.bea4dev.vanilla_source.api.entity.ai.pathfinding.BlockPosition
import com.github.bea4dev.vanilla_source.api.entity.tick.TickThread
import com.github.bea4dev.vanilla_source.api.util.collision.EngineBoundingBox
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.pow

class Phage(
    location: Location,
) : EngineEntity(
    SnowDawn.ENTITY_THREAD.threadLocalCache.getGlobalWorld(location.world.name),
    VanillaSourceAPI.getInstance().nmsHandler.createNMSEntityController(
        location.world,
        location.x,
        location.y,
        location.z,
        EntityType.ITEM_DISPLAY,
        null
    ),
    SnowDawn.ENTITY_THREAD,
    null
) {
    internal val bukkitWorld = location.world
    private var state = PhageState.IDLE
    private var stateVariables = PhageStateVariables()
    internal var target: Player? = null
    private var tick = 0

    private val attackRange = 4.0
    private val attackJumpTick = 20
    private val parryBufferTick = 5
    private val maxStuck = TickThread.TPS * 5

    private val height = 1.6
    private val width = 0.9

    init {
        super.aiController.goalSelector.registerGoal(0, PhageAIGoal(this))
        super.aiController.navigator.maxIterations = 100
        super.aiController.navigator.speed = 0.25F

        super.hasGravity = true

        super.setModel("phage_normal")
        super.getAnimationHandler().playAnimation(PhageAnimation.IDLE.key, 0.3, 0.3, 1.0, true)

        super.entityController.resetBoundingBoxForMovement(
            EngineBoundingBox(
                location.x - width / 2.0,
                location.y,
                location.z - width / 2.0,
                location.x + width / 2.0,
                location.y + height,
                location.z + width / 2.0
            )
        )
    }

    private fun setState(state: PhageState) {
        this.state = state
        this.stateVariables.reset()
    }

    override fun tick() {
        tick++
        super.tick()

        super.modeledEntityHolder.dummy.bodyRotationController.yBodyRot = super.yaw

        tickState()
    }

    private fun tickState() {
        when (state) {
            PhageState.IDLE -> {
                super.aiController.navigator.enableNavigation = true
                // TODO : walk randomly

                playIdleOrWalkAnimation()

                val target = this.target
                if (target != null) {
                    this.setState(PhageState.RUN)
                }
            }

            PhageState.RUN -> {
                super.aiController.navigator.enableNavigation = true

                val target = this.target
                if (target == null) {
                    this.setState(PhageState.IDLE)
                } else {
                    playIdleOrWalkAnimation()

                    if (target.location.toVector().distanceSquared(super.getPosition()) < attackRange.pow(2)) {
                        animationHandler.stopAnimation(PhageAnimation.WALK.key)
                        this.setState(PhageState.ATTACK)
                        this.stateVariables.attackTarget = target
                    }
                }
            }

            PhageState.ATTACK -> {
                super.aiController.navigator.enableNavigation = false

                val target = this.stateVariables.attackTarget!!

                if (this.stateVariables.attackTick == 0) {
                    animationHandler.playAnimation(PhageAnimation.ATTACK.key, 0.3, 0.3, 1.0, true)
                }

                this.stateVariables.attackTick++

                if (this.stateVariables.attackTick < attackJumpTick) {
                    super.setRotationLookAt(target.x, target.y, target.z)
                } else if (this.stateVariables.attackTick == attackJumpTick) {
                    val direction = target.location.toVector().subtract(super.getPosition())

                    if (!direction.isZero) {
                        direction.normalize().multiply(0.8).add(Vector(0.0, 0.4, 0.0))
                    }

                    super.velocity = direction
                } else {
                    super.velocity.add(Vector(0.0, -0.08, 0.0))

                    if (super.isOnGround()) {
                        animationHandler.stopAnimation(PhageAnimation.ATTACK.key)
                        this.setState(PhageState.RUN)
                    }
                }
            }

            PhageState.STUCK -> {
                stateVariables.stuckTick++

                if (stateVariables.stuckTick == 14) {
                    animationHandler.playAnimation(PhageAnimation.STUCK.key, 0.0, 0.3, 1.0, true)
                }

                if (stateVariables.stuckTick > maxStuck) {
                    animationHandler.stopAnimation(PhageAnimation.STUCK.key)
                    this.setState(PhageState.RUN)
                }
            }
        }
    }

    private fun playIdleOrWalkAnimation() {
        if (super.getMoveDelta().isZero) {
            animationHandler.stopAnimation(PhageAnimation.WALK.key)
            if (!animationHandler.animations.containsKey(PhageAnimation.IDLE.key)) {
                animationHandler.playAnimation(PhageAnimation.IDLE.key, 0.3, 0.3, 1.0, true)
            }
        } else {
            animationHandler.stopAnimation(PhageAnimation.IDLE.key)
            if (!animationHandler.animations.containsKey(PhageAnimation.WALK.key)) {
                animationHandler.playAnimation(PhageAnimation.WALK.key, 0.3, 0.3, 2.0, true)
            }
        }
    }

    fun tryParry(): Boolean {
        return state == PhageState.ATTACK && stateVariables.attackTick in attackJumpTick until (attackJumpTick + parryBufferTick)
    }

    fun parryBy(player: Player) {
        val location = player.location
        super.setRotationLookAt(location.x, location.y, location.z)

        val direction = super.getPosition().subtract(location.toVector())
        direction.setY(0.0)
        if (!direction.isZero) {
            direction.normalize().multiply(0.2).add(Vector(0.0, 0.1, 0.0))
        }
        super.velocity = direction

        animationHandler.playAnimation(PhageAnimation.PARRY.key, 0.0, 0.0, 1.0, true)
        setState(PhageState.STUCK)
    }
}

private enum class PhageAnimation(val key: String) {
    IDLE("idle"),
    WALK("walk"),
    ATTACK("attack"),
    PARRY("parry"),
    STUCK("stuck"),
}

private enum class PhageState {
    IDLE,
    RUN,
    ATTACK,
    STUCK,
}

private class PhageStateVariables {
    var attackTarget: Player? = null
    var attackTick = 0
    var stuckTick = 0

    fun reset() {
        attackTarget = null
        attackTick = 0
        stuckTick = 0
    }
}

private class PhageAIGoal(private val phage: Phage) : PathfindingGoal {
    private val maxTargetFollowTick = TickThread.TPS * 30
    private val targetSearchInterval = TickThread.TPS * 2
    private val maxPlayerDetectionRange = 50.0

    private var tick = 0
    private var targetFollowTick = maxTargetFollowTick

    override fun run(selector: GoalSelector, navigator: Navigator) {
        tick++

        if (phage.target != null) {
            targetFollowTick++
        }

        if (targetFollowTick >= maxTargetFollowTick && tick % targetSearchInterval == 0) {
            val target = PlayerManager.getNearestPlayer(phage.position.toLocation(phage.bukkitWorld))

            if (target != null && target.location.toVector()
                    .distanceSquared(phage.position) < maxPlayerDetectionRange.pow(2)
            ) {
                phage.target = target
            }
        }

        val targetLocation = phage.target?.location
        if (targetLocation != null) {
            navigator.navigationGoal =
                BlockPosition(targetLocation.blockX, targetLocation.blockY, targetLocation.blockZ)
        }

        selector.isFinished = true
    }
}