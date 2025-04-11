package io.github.yuazer.zobbletrainerlevel.api.events

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

data class TrainerLevelUpEvent(
    val player: Player,
    val level: Int,
):BukkitProxyEvent()
