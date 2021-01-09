/*
 * MIT License
 *
 * Copyright (c) 2020 Philipp Eistrach
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.fllip.inventory.api.inventory

import com.google.common.collect.Lists
import com.google.inject.Inject
import com.google.inject.Singleton
import de.fllip.inventory.api.creator.InventoryCreator
import de.fllip.inventory.api.creator.InventoryInformation
import de.fllip.inventory.api.result.InventoryResult
import de.fllip.inventory.api.section.state.InventoryStateHelper
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.12.2020
 * Time: 12:56
 */
@Singleton
class InventoryService @Inject constructor(
    private val creator: InventoryCreator,
    private val javaPlugin: JavaPlugin
) {


    val inventories = Lists.newArrayList<InventoryCache>()

    fun openInventory(player: Player, inventoryName: String) {
        openInventory(player, inventoryName, null)
    }

    fun openInventory(player: Player, inventoryName: String, page: Int?) {
        val inventoryInformation =
            creator.inventories.firstOrNull { it.inventoryName.equals(inventoryName, true) } ?: return
        val inventory = getInventory(player, inventoryName) ?: createBukkitInventory(player, inventoryInformation)

        inventoryInformation.inventoryConfiguration.openingHandler?.accept(InventoryResult(inventory, player))

        player.setMetadata("current-inventory", FixedMetadataValue(javaPlugin, inventoryName))

        if (page == null)
            inventory.open()
        else
            inventory.open(page)
    }

    private fun createBukkitInventory(player: Player, inventoryInformation: InventoryInformation): Inventory {
        val inventory = Inventory(javaPlugin, player, inventoryInformation, this)
        inventory.create()

        inventories.add(InventoryCache(player, inventoryInformation.inventoryName, inventory))

        return inventory
    }

    fun getOpenedInventory(player: Player): Inventory? {
        val inventoryName = player.getMetadata("current-inventory").firstOrNull()?.asString() ?: return null

        return getInventory(player, inventoryName)
    }

    fun getInventory(player: Player, inventoryName: String): Inventory? {
        return inventories.firstOrNull { it.player == player && it.inventoryName == inventoryName }?.inventory
    }

    fun destroyInventoriesOfPlayer(player: Player) {
        inventories.removeIf { it.player == player }
        InventoryStateHelper.destroyStatesOfPlayer(player)
    }

}