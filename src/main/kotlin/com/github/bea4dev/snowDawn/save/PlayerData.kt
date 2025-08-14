package com.github.bea4dev.snowDawn.save

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object PlayerDataRegistry {
    private val map = ConcurrentHashMap<UUID, PlayerData>()

    operator fun get(player: Player): PlayerData {
        return map.computeIfAbsent(player.uniqueId) { PlayerData(player) }
    }
}

class PlayerData(player: Player) {
    var finishedTutorial = false
    var respawnLocation = player.location.clone()
    var lastLocation = player.location.clone()
    var finishedSisetuMovie = false
    var prevSnowLandEntrance: Location? = null
    var secondMegaStructureEnterFlag = false
}