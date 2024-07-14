@file:Suppress("unused", "MemberVisibilityCanBePrivate")
package org.serverct.parrot.parrotx.util

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandContext

/**
 * 将 CommandContext 直接转换为 Player
 * @param player context中玩家参数名称
 */
fun CommandContext<*>.toPlayer(player: String = "player"): Player = Bukkit.getPlayerExact(this[player])!!