package dev.themackabu.requests.utils

import org.bukkit.entity.Player

fun getExpFromLevel(level: Int): Int {
    if (level > 30) { return (4.5 * level * level - 162.5 * level + 2220).toInt() }
    if (level > 15) { return (2.5 * level * level - 40.5 * level + 360).toInt() }
    return level * level + 6 * level
}

fun getExpToNext(level: Int): Int {
    if (level >= 30) { return level * 9 - 158 }
    if (level >= 15) { return level * 5 - 38 }
    return level * 2 + 7
}

fun getExpTotal(player: Player): Int {
    return getExpFromLevel(player.getLevel()) + Math.round(getExpToNext(player.getLevel()) * player.getExp())
}