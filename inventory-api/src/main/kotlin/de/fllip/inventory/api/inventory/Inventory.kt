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
import com.google.common.collect.Maps
import de.fllip.inventory.api.creator.InventoryInformation
import de.fllip.inventory.api.replacement.PlaceholderReplacer
import de.fllip.inventory.api.section.InventorySectionType
import de.fllip.inventory.api.section.bukkit.InventoryItemStack
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.CompletableFuture

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.12.2020
 * Time: 12:48
 */
class Inventory(
    private val javaPlugin: JavaPlugin,
    val player: Player,
    val inventoryInformation: InventoryInformation,
    private val service: InventoryService
) {

    lateinit var bukkitInventory: Inventory
    var currentPage = 1
    private val paginationInformation = inventoryInformation.inventoryFile.pagination

    private val cachedGroupItems = Maps.newHashMap<String, List<InventoryItemStack>>()
    private val futureCache = Lists.newArrayList<CompletableFuture<*>>()
    private val items = Maps.newHashMap<Int, InventoryItemStack>()
    private val groupItems = Lists.newArrayList<InventoryGroupItemsCache>()

    fun create() {
        bukkitInventory = Bukkit.createInventory(
            player,
            inventoryInformation.inventoryFile.type.slots,
            "§cLoading..."
        )
    }

    fun open() {
        open(1)
    }

    fun open(page: Int) {
        val openedInventory = service.getOpenedInventory(player)
        if (openedInventory == null || openedInventory.inventoryInformation.inventoryName != inventoryInformation.inventoryName) {
            return
        }

        currentPage = page

        Bukkit.getScheduler().runTask(javaPlugin, Runnable {
            cachedGroupItems.clear()
            bukkitInventory.clear()
            setItems()

            player.openInventory(bukkitInventory)
            updateTitle()
        })
    }

    fun update() {
        update(currentPage)
    }

    fun update(page: Int) {
        val openedInventory = service.getOpenedInventory(player)
        if (openedInventory == null || openedInventory.inventoryInformation.inventoryName != inventoryInformation.inventoryName) {
            return
        }

        currentPage = page


        Bukkit.getScheduler().runTask(javaPlugin, Runnable {
            bukkitInventory.clear()
            setItems()

            player.updateInventory()
            updateTitle()
        })
    }

    fun updateTitle() {
        val type = inventoryInformation.inventoryFile.type
        if (!areFuturesComplete()) {
            InventoryTitle("§cLoading...")
                .execute(player, type)
        } else {
            val titlePlaceholders = inventoryInformation.inventoryConfiguration.titlePlaceholders
            val title = inventoryInformation.inventoryFile.title

            InventoryTitle(PlaceholderReplacer.replace(title, player, this, titlePlaceholders))
                .execute(player, type)
        }
    }

    fun openNextPage() {
        if (hasNextPage()) {
            update(currentPage + 1)
        }
    }

    fun openPreviousPage() {
        if (hasPreviousPage()) {
            update(currentPage - 1)
        }
    }

    fun clearCachedGroup(groupIdentifier: String) {
        cachedGroupItems.remove(groupIdentifier)
    }

    fun setItem(slot: Int, item: InventoryItemStack) {
        items[slot] = item
    }

    fun addGroupItems(groupIdentifier: String, items: List<InventoryItemStack>, slots: List<Int>) {
        groupItems.add(InventoryGroupItemsCache(groupIdentifier, slots, items))
    }

    fun isLastPage(): Boolean {
        if (!paginationInformation.enabled) {
            return true
        }

        return getPages() == currentPage
    }

    fun isFirstPage(): Boolean {
        if (!paginationInformation.enabled) {
            return true
        }

        return currentPage == 1
    }

    fun hasNextPage(): Boolean {
        return paginationInformation.enabled && getPages() > currentPage
    }

    fun hasPreviousPage(): Boolean {
        return paginationInformation.enabled && currentPage > 1
    }

    fun getPages(): Int {
        val size = getPaginationCache(paginationInformation.groupIdentifier)?.second?.size ?: 1
        return if (size == 0) 1 else size
    }

    fun isOpened(): Boolean {
        return player.openInventory.topInventory == bukkitInventory
    }

    fun addFutureCache(future: CompletableFuture<*>) {
        futureCache.add(future)
    }

    fun areFuturesComplete(): Boolean {
        return futureCache.isEmpty() || futureCache.all { it.isDone }
    }

    fun getCachedGroupItems(identifier: String): List<InventoryItemStack>? {
        return cachedGroupItems[identifier]
    }

    fun addCachedGroupItems(identifier: String, items: List<InventoryItemStack>) {
        cachedGroupItems[identifier] = items.map {
            it.withIdentifier(identifier)
        }
    }

    private fun loadItems(vararg sortByTypes: InventorySectionType) {
        items.clear()
        groupItems.clear()

        val typeList = sortByTypes.toList()

        inventoryInformation.inventoryFile.sections
            .sortedBy { !typeList.contains(it.type) }
            .forEach {
                val itemConfigurator = inventoryInformation
                    .inventoryConfiguration.getSectionConfigurators()[it.identifier]

                it.setItem(this, player, itemConfigurator)

                if (it.type == InventorySectionType.GROUP) {
                    val pages = getPages()
                    if (currentPage > pages) {
                        currentPage = pages
                    }
                }
            }
    }

    private fun setItems() {
        loadItems(InventorySectionType.GROUP)

        items.forEach {
            bukkitInventory.setItem(it.key, it.value)
        }

        if (paginationInformation.enabled) {
            val paginationCache = getPaginationCache(paginationInformation.groupIdentifier)

            if (paginationCache != null) {
                if (paginationCache.second.isNotEmpty()) {
                    paginationCache.second[currentPage - 1].forEachIndexed { index, item ->
                        bukkitInventory.setItem(paginationCache.first.slots[index], item)
                    }
                }
            }
        }

        groupItems
            .filter { if (paginationInformation.enabled) it.identifier != paginationInformation.groupIdentifier else true }
            .forEach { cache ->
                val paginationCache = getPaginationCache(cache.identifier)

                if (paginationCache != null) {
                    if (paginationCache.second.isNotEmpty()) {
                        paginationCache.second[currentPage - 1].forEachIndexed { index, item ->
                            bukkitInventory.setItem(paginationCache.first.slots[index], item)
                        }
                    }
                }
            }
    }

    private fun getPaginationCache(groupIdentifier: String): Pair<InventoryGroupItemsCache, List<List<InventoryItemStack>>>? {
        val cache = groupItems.firstOrNull { it.identifier == groupIdentifier } ?: return null
        val chunked = cache.items.chunked(cache.slots.size)

        return Pair(cache, chunked)
    }

}