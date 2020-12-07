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