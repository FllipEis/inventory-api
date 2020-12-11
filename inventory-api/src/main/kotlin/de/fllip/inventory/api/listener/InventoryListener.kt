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

package de.fllip.inventory.api.listener

import com.google.inject.Inject
import com.google.inject.Singleton
import de.fllip.inventory.api.creator.InventoryCreator
import de.fllip.inventory.api.inventory.InventoryService
import de.fllip.inventory.api.result.InventoryClickEventResult
import de.fllip.inventory.api.result.InventoryClickResult
import de.fllip.inventory.api.result.InventoryStateSwitchResult
import de.fllip.inventory.api.section.bukkit.isStateItem
import de.fllip.inventory.api.section.bukkit.toInventoryItemStack
import de.fllip.inventory.api.section.state.InventoryStateHelper
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.ClickType
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
        val bukkitItem = event.currentItem ?: return
        val item = bukkitItem.toInventoryItemStack() ?: return
        val inventory = inventoryService.getOpenedInventory(player) ?: return

        val inventoryInformation =
            inventoryCreator.inventories.firstOrNull { it.inventoryName == currentInventoryName } ?: return

        val inventoryItem =
            inventoryInformation.inventoryFile.sections.firstOrNull { it.identifier == item.getIdentifier() } ?: return
        val itemIdentifier = inventoryItem.identifier
        val itemConfigurator = inventoryInformation.inventoryConfiguration.getSectionConfigurators()[itemIdentifier]

        val clickType = event.click
        val clickResult = itemConfigurator?.eventHandler?.invoke(
            InventoryClickEventResult(
                player,
                item,
                inventory,
                clickType
            )
        )


        if (clickResult != null) {
            event.isCancelled = clickResult == InventoryClickResult.DENY_GRABBING
        }

        val isStateItem = item.isStateItem()

        if (isStateItem) {
            var newState = ""

            if (clickType == ClickType.LEFT) {
                newState = InventoryStateHelper.nextState(player, itemIdentifier)
            } else {
                if (clickType == ClickType.RIGHT) {
                    newState = InventoryStateHelper.previousState(player, itemIdentifier)
                }
            }

            if (newState.isNotBlank()) {
                itemConfigurator?.stateHandler?.accept(
                    InventoryStateSwitchResult(
                        player,
                        newState
                    )
                )

                event.isCancelled = true
                inventory.update()
            }
        }
    }

    @EventHandler
    fun handleQuit(event: PlayerQuitEvent) {
        val player = event.player

        inventoryService.destroyInventoriesOfPlayer(player)
    }

}