package io.github.yuazer.zobbletrainerlevel.events

import io.github.yuazer.zobbletrainerlevel.ZobbleTrainerLevel
import io.github.yuazer.zobbletrainerlevel.api.events.TrainerLevelUpEvent
import io.github.yuazer.zobbletrainerlevel.utils.PlayerUtils.runKether
import taboolib.common.platform.event.SubscribeEvent

object PlayerEvent {
    @SubscribeEvent
    fun onLevelUp(event: TrainerLevelUpEvent){
        val player = event.player
        val level = event.level
        val levelRewardList = ZobbleTrainerLevel.rewardConfig.getConfigurationSection("LevelReward")?.getKeys(false)
        if (!levelRewardList!!.contains(level.toString())){
            return
        }
        val rewardList = ZobbleTrainerLevel.rewardConfig.getStringList("LevelReward.$level")
        rewardList.runKether(player)
    }
}