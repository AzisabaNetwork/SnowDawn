package com.github.bea4dev.snowDawn.camera

import com.github.bea4dev.vanilla_source.api.VanillaSourceAPI
import com.github.bea4dev.vanilla_source.api.camera.CameraHandler
import com.github.bea4dev.vanilla_source.api.contan.ContanUtil
import com.github.bea4dev.vanilla_source.api.player.EnginePlayer
import org.bukkit.entity.Player

fun createCamera(player: Player): CameraHandler {
    return CameraHandler(
        EnginePlayer.getEnginePlayer(player)!!,
        VanillaSourceAPI.getInstance().tickThreadPool.nextTickThread,
        ContanUtil.getEmptyClassInstance(),
    )
}