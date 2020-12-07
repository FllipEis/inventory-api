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

package de.fllip.inventory.example.configuration

import com.google.common.collect.Lists
import de.fllip.inventory.api.creator.AbstractInventoryConfiguration
import de.fllip.inventory.api.replacement.PlaceholderReplacement
import de.fllip.inventory.api.result.InventoryClickResult
import de.fllip.inventory.api.section.bukkit.InventoryItemStack
import org.bukkit.Material
import org.bukkit.Sound

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 19:05
 */
class InventoryConfiguration : AbstractInventoryConfiguration() {

    override fun configure() {
        configureTitlePlaceholder(PlaceholderReplacement("name") { player, _ ->
            player.name
        })

        configureSection("bottle", SectionConfigurator()
            .withEventHandler {
                it.player.sendMessage("§7Clicked")
                InventoryClickResult.DENY_GRABBING
            }
            .withPlaceholder(PlaceholderReplacement("currentPage") { _, inventory ->
                inventory.currentPage.toString()
            })
        )

        configureSection("groupExample", SectionConfigurator()
            .withGroupItems {
                val items = Lists.newArrayList<InventoryItemStack>()

                for (i: Int in 1..100) {
                    items.add(InventoryItemStack(Material.GLASS_BOTTLE)
                        .withDisplayName("§b§lLobby-$i")
                    )
                }

                items
            }
        )

        configureSection("paginationPrevious", SectionConfigurator()
            .withDynamicItem { inventory, player ->
                if (!inventory.hasPreviousPage()) {
                    return@withDynamicItem InventoryItemStack(Material.RED_STAINED_GLASS_PANE)
                        .withDisplayName("§cPrevious page")
                } else {
                    return@withDynamicItem InventoryItemStack(Material.LIME_STAINED_GLASS_PANE)
                        .withDisplayName("§aPrevious page")
                }
            }
            .withEventHandler {
                if (it.inventory.hasPreviousPage()) {
                    it.inventory.openPreviousPage()
                    it.player.playSound(it.player.location, Sound.BLOCK_BEEHIVE_ENTER, 1F, 1F)
                } else {
                    it.player.playSound(it.player.location, Sound.BLOCK_ANVIL_LAND, 1F, 10F)
                }

                InventoryClickResult.DENY_GRABBING
            }
        )

        configureSection("paginationNext", SectionConfigurator()
            .withDynamicItem { inventory, player ->
                if (!inventory.hasNextPage()) {
                    return@withDynamicItem InventoryItemStack(Material.RED_STAINED_GLASS_PANE)
                        .withDisplayName("§cNext page")
                } else {
                    return@withDynamicItem InventoryItemStack(Material.LIME_STAINED_GLASS_PANE)
                        .withDisplayName("§aNext page")
                }
            }
            .withEventHandler {
                if (it.inventory.hasNextPage()) {
                    it.inventory.openNextPage()
                    it.player.playSound(it.player.location, Sound.BLOCK_BEEHIVE_ENTER, 1F, 1F)
                } else {
                    it.player.playSound(it.player.location, Sound.BLOCK_ANVIL_LAND, 1F, 10F)
                }

                InventoryClickResult.DENY_GRABBING
            }
        )
    }

}