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

package de.fllip.inventory.api.creator

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import de.fllip.inventory.api.inventory.Inventory
import de.fllip.inventory.api.section.bukkit.InventoryItemStack
import de.fllip.inventory.api.replacement.PlaceholderReplacement
import de.fllip.inventory.api.result.InventoryClickEventResult
import de.fllip.inventory.api.result.InventoryClickResult
import org.bukkit.entity.Player

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 19:00
 */
abstract class AbstractInventoryConfiguration {

    private val sectionConfigurators = Maps.newHashMap<String, SectionConfigurator>()
    val titlePlaceholders = Lists.newArrayList<PlaceholderReplacement>()

    abstract fun configure()

    fun configureTitlePlaceholder(placeholderReplacement: PlaceholderReplacement) {
        titlePlaceholders.add(placeholderReplacement)
    }

    fun configureSection(sectionIdentifier: String, configurator: SectionConfigurator) {
        sectionConfigurators[sectionIdentifier] = configurator
    }

    fun getSectionConfigurators(): Map<String, SectionConfigurator> {
        return sectionConfigurators
    }

    class SectionConfigurator() {

        val eventHandlers = Lists.newArrayList<(InventoryClickEventResult) -> InventoryClickResult>()
        var dynamicItem: ((Inventory, Player) -> InventoryItemStack)? = null
        var groupItems: ((Player) -> List<InventoryItemStack>)? = null
        val placeholders = Lists.newArrayList<PlaceholderReplacement>()

        fun withEventHandler(handleEvent: (InventoryClickEventResult) -> InventoryClickResult): SectionConfigurator {
            eventHandlers.add(handleEvent)

            return this
        }

        fun withPlaceholder(placeholderReplacement: PlaceholderReplacement): SectionConfigurator {
            placeholders.add(placeholderReplacement)

            return this
        }

        fun withDynamicItem(handleSet: (Inventory, Player) -> InventoryItemStack): SectionConfigurator {
            dynamicItem = handleSet

            return this
        }

        fun withGroupItems(handleSet: (Player) -> List<InventoryItemStack>): SectionConfigurator {
            groupItems = handleSet

            return this
        }

    }

}