package de.fllip.inventory.api.listener

import com.google.inject.Inject
import com.google.inject.Singleton
import de.fllip.inventory.api.creator.InventoryCreator
import de.fllip.inventory.api.inventory.InventoryService
import de.fllip.inventory.api.section.bukkit.toInventoryItemStack
import de.fllip.inventory.api.result.InventoryClickEventResult
import de.fllip.inventory.api.result.InventoryClickResult
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 04.12.2020
 * Time: 10:54
 */
@Singleton
class InventoryListener @Inject constructor(
    private val inventoryCreator: InventoryCreator,
    private val inventoryService: InventoryService
) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun handleClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val currentInventoryName = player.getMetadata("current-inventory").firstOrNull()?.asString() ?: return
        val item = event.currentItem?.toInventoryItemStack() ?: return
        val inventory = inventoryService.getOpenedInventory(player)?: return

        val inventoryInformation =
            inventoryCreator.inventories.firstOrNull { it.inventoryName == currentInventoryName } ?: return

        val inventoryItem =
            inventoryInformation.inventoryFile.sections.firstOrNull { it.identifier == item.getIdentifier() } ?: return
        val itemIdentifier = inventoryItem.identifier
        val itemConfigurator = inventoryInformation.inventoryConfiguration.getSectionConfigurators()[itemIdentifier]

        itemConfigurator?.eventHandlers?.forEach {
            val clickResult = it.invoke(InventoryClickEventResult(
                player,
                item,
                inventory,
                event.click
            ))

            event.isCancelled = clickResult == InventoryClickResult.DENY_GRABBING
        }
    }

    @EventHandler
    fun handleQuit(event: PlayerQuitEvent) {
        val player = event.player

        inventoryService.destroyInventoriesOfPlayer(player)
    }


}