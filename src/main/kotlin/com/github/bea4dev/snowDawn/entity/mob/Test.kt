package com.github.bea4dev.snowDawn.entity.mob

import com.github.bea4dev.vanilla_source.api.entity.tick.TickThread
import org.bukkit.Location

class Test(
    location: Location,
    health: Float,
    attackDamage: Float
) : Mob(location, health, attackDamage) {

    override val attackRange = 2.0
    override val attackJumpTick = 20
    override val parryBufferTick = 4
    override val maxStuck = TickThread.TPS * 3
    override val maxDamageTick = 10
}