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
import de.fllip.inventory.api.section.type.GroupInventorySection
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
    private val groupItemSlots = Lists.newArrayList<Int>()
    private val groupItems = Lists.newArrayList<InventoryGroupItemsCache>()

    fun create() {
        val inventoryFile = inventoryInformation.inventoryFile

        val loadingTitle = inventoryFile.loadingTitle
        val title = loadingTitle ?: PlaceholderReplacer.replace(
            inventoryFile.title,
            player,
            this,
            inventoryInformation.inventoryConfiguration.titlePlaceholders
        )

        bukkitInventory = Bukkit.createInventory(
            player,
            inventoryFile.type.slots,
            title
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
            player.openInventory(bukkitInventory)

            Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, Runnable {
                cachedGroupItems.clear()
                update()
            })
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

        val typesToSet = arrayOf(InventorySectionType.GROUP, InventorySectionType.DYNAMIC, InventorySectionType.STATE)

        setItems(*typesToSet)
        clearOldItems()

        player.updateInventory()
        updateTitle()
    }

    fun updateTitle() {
        if (player.openInventory.topInventory != bukkitInventory) {
            return
        }

        val inventoryFile = inventoryInformation.inventoryFile

        if (inventoryFile.loadingTitle == null) {
            return
        }

        val type = inventoryFile.type
        if (!areFuturesComplete()) {
            InventoryTitle(inventoryFile.loadingTitle)
                .execute(player, type)
        } else {
            val titlePlaceholders = inventoryInformation.inventoryConfiguration.titlePlaceholders
            val title = inventoryFile.title

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

    private fun loadItems(vararg types: InventorySectionType, sort: Boolean = true) {
        groupItems.clear()

        val typeList = types.toList()

        var sections = inventoryInformation.inventoryFile.sections

        if (sort) {
            sections = sections.sortedBy { !typeList.contains(it.type) }
        } else {
            sections = sections.filter { types.contains(it.type) }
        }

        sections.forEach {
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

    private fun clearOldItems() {
        if (!areFuturesComplete()) {
            return
        }

        inventoryInformation.inventoryFile.sections
            .filter { it.type == InventorySectionType.GROUP }
            .flatMap { section ->
                if ((section as GroupInventorySection).slotRange) {
                    section.slots.chunked(2)
                        .flatMap { IntRange(it.first(), it.last()).toList() }
                } else section.slots
            }
            .forEach {
                if (!groupItemSlots.contains(it) && bukkitInventory.getItem(it) != null) {
                    bukkitInventory.clear(it)
                }
            }
    }

    private fun setItems(vararg filterTypes: InventorySectionType = emptyArray()) {
        groupItemSlots.clear()
        if (filterTypes.isEmpty()) {
            loadItems(InventorySectionType.GROUP)
        } else {
            loadItems(*filterTypes)
        }

        items.forEach {
            bukkitInventory.setItem(it.key, it.value)
        }

        groupItems
            .forEach { cache ->
                val paginationCache = getPaginationCache(cache.identifier)

                if (paginationCache != null) {
                    if (paginationCache.second.isNotEmpty()) {
                        paginationCache.second[currentPage - 1].forEachIndexed { index, item ->
                            val slot = paginationCache.first.slots[index]
                            groupItemSlots.add(slot)
                            bukkitInventory.setItem(slot, item)
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