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
import de.fllip.inventory.api.async.DataSupplier
import de.fllip.inventory.api.inventory.Inventory
import de.fllip.inventory.api.replacement.PlaceholderReplacement
import de.fllip.inventory.api.result.InventoryClickEventResult
import de.fllip.inventory.api.result.InventoryClickResult
import de.fllip.inventory.api.result.InventoryResult
import de.fllip.inventory.api.result.InventoryStateSwitchResult
import de.fllip.inventory.api.section.bukkit.InventoryItemStack
import de.fllip.inventory.api.task.TaskInformation
import org.bukkit.entity.Player
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 19:00
 */
abstract class AbstractInventoryConfiguration {

    private val sectionConfigurators = Maps.newHashMap<String, SectionConfigurator>()
    val titlePlaceholders = Lists.newArrayList<PlaceholderReplacement>()
    var openingHandler: Consumer<InventoryResult>? = null
    var closingHandler: Consumer<InventoryResult>? = null
    var taskInformation: TaskInformation? = null

    abstract fun configure()

    fun configureRepeatingTask(
        callback: Consumer<Inventory>?,
        ticks: Long
    ) {
        configureRepeatingTask(TaskInformation(callback, ticks))
    }

    fun configureRepeatingTask(taskInformation: TaskInformation) {
        this.taskInformation = taskInformation
    }

    fun configureUpdater(ticks: Long) {
        configureRepeatingTask(null, ticks)
    }

    fun configureOpeningHandler(handleOpening: Consumer<InventoryResult>) {
        openingHandler = handleOpening
    }

    fun configureClosingHandler(handleClosing: Consumer<InventoryResult>) {
        closingHandler = handleClosing
    }

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

        var eventHandler: ((InventoryClickEventResult) -> InventoryClickResult)? = null
        var stateHandler: Consumer<InventoryStateSwitchResult>? = null
        var firstState: ((Player) -> String)? = null
        var dynamicItem: ((Inventory, Player) -> InventoryItemStack)? = null
        var dataSupplier: DataSupplier<*>? = null
        val placeholders = Lists.newArrayList<PlaceholderReplacement>()!!
        var customSlotsHandler: ((Player) -> List<Int>) = { emptyList() }

        fun withEventHandler(handleEvent: (InventoryClickEventResult) -> InventoryClickResult): SectionConfigurator {
            eventHandler = handleEvent

            return this
        }

        fun withStateHandler(handleStateChange: Consumer<InventoryStateSwitchResult>): SectionConfigurator {
            stateHandler = handleStateChange

            return this
        }

        fun withFirstState(handleFirstState: (Player) -> String): SectionConfigurator {
            firstState = handleFirstState

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
            return withGroupItems(
                DataSupplier<InventoryItemStack>()
                    .withData {
                        handleSet.invoke(it)
                    }
                    .withInventoryItemStackMapper {
                        it
                    }
            )
        }

        fun <T : Any> withGroupItems(supplier: DataSupplier<T>): SectionConfigurator {
            dataSupplier = supplier

            return this
        }

        fun withCustomSlots(customSlotsHandler: (Player) -> List<Int>): SectionConfigurator {
            this.customSlotsHandler = customSlotsHandler

            return this
        }

    }

}