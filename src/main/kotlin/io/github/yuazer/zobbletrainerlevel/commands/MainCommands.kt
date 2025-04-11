package io.github.yuazer.zobbletrainerlevel.commands

import io.github.yuazer.zobbletrainerlevel.ZobbleTrainerLevel
import io.github.yuazer.zobbletrainerlevel.api.LevelApi
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.platform.util.asLangText
import taboolib.platform.util.onlinePlayers
import taboolib.platform.util.sendLang

@CommandHeader("zobbletrainerlevel", ["ztl"])
object MainCommands {
    @CommandBody(permission = "zobbletrainerlevel.reload")
    val reload = subCommand {
        execute<CommandSender> { sender, context, argument ->
            ZobbleTrainerLevel.config.reload()
            ZobbleTrainerLevel.options.reload()
            sender.sendLang("reload-message")
        }
    }

    @CommandBody
    val help = subCommand {
        createHelper(true)
    }
    @CommandBody(permission = "zobbletrainerlevel.debug")
    val debug = subCommand {
        execute<CommandSender> { sender, context, argument ->
            val name = sender.name
            val level = LevelApi.getPlayerLevelContainer(name).level
            val exp = LevelApi.getPlayerLevelContainer(name).experience
            sender.sendMessage("Level:$level")
            sender.sendMessage("Exp:$exp")
        }
    }
    @CommandBody(permission = "zobbletrainerlevel.add")
    val add = subCommand {
        player("player") {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                onlinePlayers.map { it.name }
            }
            dynamic("level/exp") {
                suggestion<CommandSender>(uncheck = true) { _, _ ->
                    listOf("level", "exp")
                }
                dynamic("value") {
                    execute<CommandSender> { sender, context, argument ->
                        val player = context.player("player")
                        val bukkitPlayer = Bukkit.getPlayer(player.uniqueId)?.let {
                            val type = context["level/exp"]
                            val value = context.int("value")
                            if (type.equals("level", true)) {
                                LevelApi.getPlayerLevelContainer(it.name).addLevel(value)
                                it.sendMessage(it.asLangText("add-level-message").replace("{value}",value.toString()))
                            } else if (type.equals("exp", true)) {
                                LevelApi.getPlayerLevelContainer(it.name).addExperience(value)
                                it.sendMessage(it.asLangText("add-exp-message").replace("{value}",value.toString()))
                            } else {
                                it.sendLang("error-args")
                            }
                        }
                    }
                }
            }
        }
        dynamic("level/exp") {
            dynamic("value") {
                execute<CommandSender> { sender, context, _ ->
                    if (sender !is Player) {
                        return@execute
                    }
                    val bukkitPlayer = Bukkit.getPlayer(sender.uniqueId)?.let {
                        val type = context["level/exp"]
                        val value = context.int("value")
                        when (type.lowercase()) {
                            "level" -> {
                                LevelApi.getPlayerLevelContainer(it.name).addLevel(value)
                                it.sendMessage(it.asLangText("add-level-message").replace("{value}",value.toString()))
                            }

                            "exp" -> {
                                LevelApi.getPlayerLevelContainer(it.name).addExperience(value)
                                it.sendMessage(it.asLangText("add-exp-message").replace("{value}",value.toString()))
                            }
                            else -> it.sendLang("error-args")
                        }
                    }
                }
            }
        }
    }

    @CommandBody(permission = "zobbletrainerlevel.set")
    val set = subCommand {
        player("player") {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                onlinePlayers.map { it.name }
            }
            dynamic("level/exp") {
                suggestion<CommandSender>(uncheck = true) { _, _ ->
                    listOf("level", "exp")
                }
                dynamic("value") {
                    execute<CommandSender> { _, context, _ ->
                        val player = context.player("player")
                        Bukkit.getPlayer(player.uniqueId)?.let {
                            val type = context["level/exp"]
                            val value = context.int("value")
                            when (type.lowercase()) {
                                "level" -> {
                                    LevelApi.getPlayerLevelContainer(it.name).level = value
                                    it.sendMessage(it.asLangText("set-level-message").replace("{value}",value.toString()))
                                }

                                "exp" -> {
                                    LevelApi.getPlayerLevelContainer(it.name).updateExperience(value)
                                    it.sendMessage(it.asLangText("set-exp-message").replace("{value}",value.toString()))

                                }

                                else -> it.sendLang("error-args")
                            }
                        }
                    }
                }
            }
        }
        dynamic("level/exp") {
            dynamic("value") {
                execute<CommandSender> { sender, context, _ ->
                    if (sender !is Player) return@execute
                    val type = context["level/exp"]
                    val value = context.int("value")
                    Bukkit.getPlayer(sender.uniqueId)?.let {
                        when (type.lowercase()) {
                            "level" -> {
                                LevelApi.getPlayerLevelContainer(sender.name).level = value
                                it.sendMessage(it.asLangText("set-level-message").replace("{value}",value.toString()))
                            }

                            "exp" -> {
                                LevelApi.getPlayerLevelContainer(sender.name).updateExperience(value)
                                it.sendMessage(it.asLangText("set-exp-message").replace("{value}",value.toString()))
                            }

                            else -> it.sendLang("error-args")
                        }
                    }

                }
            }
        }
    }

    @CommandBody(permission = "zobbletrainerlevel.reduce")
    val reduce = subCommand {
        player("player") {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                onlinePlayers.map { it.name }
            }
            dynamic("level/exp") {
                suggestion<CommandSender>(uncheck = true) { _, _ ->
                    listOf("level", "exp")
                }
                dynamic("value") {
                    execute<CommandSender> { _, context, _ ->
                        val player = context.player("player")
                        Bukkit.getPlayer(player.uniqueId)?.let {
                            val type = context["level/exp"]
                            val value = context.int("value")
                            when (type.lowercase()) {
                                "level" -> {
                                    LevelApi.getPlayerLevelContainer(it.name).addLevel(-value)
                                    it.sendMessage(it.asLangText("reduce-level-message").replace("{value}",value.toString()))
                                }

                                "exp" -> {
                                    LevelApi.getPlayerLevelContainer(it.name).addExperience(-value)
                                    it.sendMessage(it.asLangText("reduce-exp-message").replace("{value}",value.toString()))
                                }

                                else -> it.sendLang("error-args")
                            }
                        }
                    }
                }
            }
        }
        dynamic("level/exp") {
            dynamic("value") {
                execute<CommandSender> { sender, context, _ ->
                    if (sender !is Player) return@execute
                    Bukkit.getPlayer(sender.uniqueId)?.let {
                        val type = context["level/exp"]
                        val value = context.int("value")
                        when (type.lowercase()) {
                            "level" -> {
                                LevelApi.getPlayerLevelContainer(it.name).addLevel(-value)
                                it.sendMessage(it.asLangText("reduce-level-message").replace("{value}",value.toString()))
                            }

                            "exp" -> {
                                LevelApi.getPlayerLevelContainer(it.name).addExperience(-value)
                                it.sendMessage(it.asLangText("reduce-exp-message").replace("{value}",value.toString()))
                            }

                            else -> it.sendLang("error-args")
                        }
                    }
                }
            }
        }
    }


}