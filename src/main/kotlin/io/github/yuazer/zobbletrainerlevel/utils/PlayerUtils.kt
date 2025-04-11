package io.github.yuazer.zobbletrainerlevel.utils

import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.module.kether.runKether
import taboolib.platform.compat.replacePlaceholder

object PlayerUtils {

    fun List<String>.runKether(player: Player) {
        val script = this
        runKether(script, detailError = true) {
            KetherShell.eval(
                script.replacePlaceholder(player),
                options = ScriptOptions(sender = adaptCommandSender(player))
            )
        }
    }


    fun replaceInList(strings: List<String>, oldChar: String, newChar: String): List<String> {
        return strings.map { it.replace(oldChar, newChar) }
    }
}