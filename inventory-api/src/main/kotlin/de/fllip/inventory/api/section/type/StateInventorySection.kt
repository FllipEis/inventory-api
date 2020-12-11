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
import de.fllip.inventory.api.replacement.PlaceholderReplacer
import de.fllip.inventory.api.section.AbstractInventorySection
import de.fllip.inventory.api.section.InventorySectionType
import de.fllip.inventory.api.section.bukkit.InventoryItemStack
import de.fllip.inventory.api.section.state.InventoryStateHelper
import de.fllip.inventory.api.section.state.InventoryStateInformation
import org.bukkit.entity.Player

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 08.12.2020
 * Time: 11:45
 */
class StateInventorySection(
    identifier: String = "",
    type: InventorySectionType = InventorySectionType.STATE,
    slots: List<Int> = emptyList(),
    val states: List<InventoryStateInformation> = emptyList()
) : AbstractInventorySection(identifier, slots) {

    override fun setItem(
        inventory: Inventory,
        player: Player,
        sectionConfigurator: AbstractInventoryConfiguration.SectionConfigurator?
    ) {
        val firstState = sectionConfigurator?.firstState?.invoke(player)

        val currentState = InventoryStateHelper.getCurrentState(player, identifier, firstState, states)

        val replacements = sectionConfigurator?.placeholders?: emptyList()

        val item = InventoryItemStack(currentState.material, currentState.amount)
            .withIdentifier(identifier)
            .withNBTTag("inventory-item-state", currentState.stateName)
            .withDisplayName(PlaceholderReplacer.replace(currentState.displayName, player, inventory, replacements))
            .withLore(currentState.loreLines.map { PlaceholderReplacer.replace(it, player, inventory, replacements) })

        slots.forEach {
            inventory.setItem(it, item)
        }
    }

}