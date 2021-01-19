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

package de.fllip.inventory.api.section.type

import de.fllip.inventory.api.creator.AbstractInventoryConfiguration
import de.fllip.inventory.api.inventory.Inventory
import de.fllip.inventory.api.section.AbstractInventorySection
import de.fllip.inventory.api.section.InventorySectionType
import de.fllip.inventory.api.section.bukkit.InventoryItemStack
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 04.12.2020
 * Time: 12:13
 */
class GroupInventorySection(
    identifier: String = "",
    type: InventorySectionType = InventorySectionType.DYNAMIC,
    val slotRange: Boolean = false,
    slots: List<Int> = emptyList()
) : AbstractInventorySection(identifier, slots, type) {

    override fun setItem(
        inventory: Inventory,
        player: Player,
        sectionConfigurator: AbstractInventoryConfiguration.SectionConfigurator?
    ) {
        val cache = inventory.getCachedGroupItems(identifier)

        if (cache != null) {
            addItems(player, inventory, cache, sectionConfigurator)
            return
        }

        val dataSupplier = sectionConfigurator?.dataSupplier ?: return
        val asyncHandler = dataSupplier.asyncHandler ?: return
        val itemStackMapper = dataSupplier.itemStackMapper ?: return
        val future = CompletableFuture.supplyAsync {
            asyncHandler.invoke(player)
        }

        inventory.addFutureCache(future)

        future.thenAccept { list ->
            val items = list.map {
                itemStackMapper.invoke(it)
                    .withIdentifier(identifier)
            }

            inventory.addCachedGroupItems(identifier, items)
            inventory.update()
        }
    }

    private fun addItems(
        player: Player,
        inventory: Inventory,
        items: List<InventoryItemStack>,
        sectionConfigurator: AbstractInventoryConfiguration.SectionConfigurator?
    ) {
        val slots = getCustomSlots(player, sectionConfigurator)

        val inventorySlots = if (slotRange) {
            slots.chunked(2)
                .flatMap { IntRange(it.first(), it.last()).toList() }
        } else slots

        inventory.addGroupItems(identifier, items, inventorySlots)
    }

}