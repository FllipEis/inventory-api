package de.fllip.inventory.api.inventory

import com.google.common.collect.Lists
import com.google.inject.Inject
import com.google.inject.Singleton
import de.fllip.inventory.api.creator.InventoryCreator
import de.fllip.inventory.api.creator.InventoryInformation
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


    private val inventories = Lists.newArrayList<InventoryCache>()

    fun openInventory(player: Player, inventoryName: String) {
        openInventory(player, inventoryName, null)
    }

    fun openInventory(player: Player, inventoryName: String, page: Int? ) {
        val inventoryInformation =
            creator.inventories.firstOrNull { it.inventoryName.equals(inventoryName, true) } ?: return
        val inventory = getInventory(player, inventoryName) ?: createBukkitInventory(player, inventoryInformation)


        player.setMetadata("current-inventory", FixedMetadataValue(javaPlugin, inventoryName))

        if (page == null) {
            inventory.open()
            return
        }
        inventory.open(page)
    }

    private fun createBukkitInventory(player: Player, inventoryInformation: InventoryInformation): Inventory {
        val inventory = Inventory(player, inventoryInformation)
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
    }

}