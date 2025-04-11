package io.github.yuazer.zobbletrainerlevel.api.events

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

data class TrainerExperienceGainEvent(
    val player:Player,
    val experienceGained:Int
):BukkitProxyEvent()

