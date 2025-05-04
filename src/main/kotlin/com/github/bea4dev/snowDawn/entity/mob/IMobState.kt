package com.github.bea4dev.snowDawn.entity.mob

import com.github.bea4dev.snowDawn.entity.mob.MobState.*

interface IMobState {

    fun tickState(nowState: MobState) {
        when (nowState) {
            IDLE -> tickIdle()
            RUN -> tickRun()
            ATTACK -> tickAttack()
            STUCK -> tickStuck()
            DEATH -> tickDeath()
        }
    }
    fun tickIdle()

    fun tickRun()

    fun tickStuck()

    fun tickAttack()

    fun tickDeath()
}