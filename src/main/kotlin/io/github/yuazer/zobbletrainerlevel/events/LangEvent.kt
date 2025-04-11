package io.github.yuazer.zobbletrainerlevel.events

import io.github.yuazer.zobbletrainerlevel.ZobbleTrainerLevel
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.lang.event.PlayerSelectLocaleEvent
import taboolib.module.lang.event.SystemSelectLocaleEvent

object LangEvent {
    @SubscribeEvent
    fun lang(event: PlayerSelectLocaleEvent) {
        event.locale = ZobbleTrainerLevel.config.getString("Lang", "zh_CN")!!
    }

    @SubscribeEvent
    fun lang(event: SystemSelectLocaleEvent) {
        event.locale = ZobbleTrainerLevel.config.getString("Lang", "zh_CN")!!
    }
}