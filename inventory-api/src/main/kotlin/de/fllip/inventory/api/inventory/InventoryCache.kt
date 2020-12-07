package de.fllip.inventory.api.inventory

import org.bukkit.entity.Player

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.12.2020
 * Time: 13:02
 */
data class InventoryCache(
    val player: Player,
    val inventoryName: String,
    val inventory: Inventory
)