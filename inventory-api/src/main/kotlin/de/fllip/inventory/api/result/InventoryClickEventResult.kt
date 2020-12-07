package de.fllip.inventory.api.result

import de.fllip.inventory.api.inventory.Inventory
import de.fllip.inventory.api.section.bukkit.InventoryItemStack
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 04.12.2020
 * Time: 20:03
 */
data class InventoryClickEventResult(
    val player: Player,
    val item: InventoryItemStack,
    val inventory: Inventory,
    val clickType: ClickType
)
