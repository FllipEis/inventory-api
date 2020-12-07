package de.fllip.inventory.api.section.bukkit

import org.bukkit.inventory.ItemStack

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 04.12.2020
 * Time: 18:09
 */

fun ItemStack.toInventoryItemStack(): InventoryItemStack? {
    return InventoryItemStack.fromItemStack(this)
}