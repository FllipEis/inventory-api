package de.fllip.inventory.api.inventory

import de.fllip.inventory.api.section.bukkit.InventoryItemStack

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.12.2020
 * Time: 15:57
 */
data class InventoryGroupItemsCache(
    val identifier: String,
    val slots: List<Int>,
    val items: List<InventoryItemStack>
)
