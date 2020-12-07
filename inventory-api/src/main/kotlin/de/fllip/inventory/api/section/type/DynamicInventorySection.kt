package de.fllip.inventory.api.section.type

import de.fllip.inventory.api.creator.AbstractInventoryConfiguration
import de.fllip.inventory.api.section.AbstractInventorySection
import de.fllip.inventory.api.section.InventorySectionType
import org.bukkit.entity.Player
import de.fllip.inventory.api.inventory.Inventory

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 04.12.2020
 * Time: 12:13
 */
class DynamicInventorySection(
    identifier: String = "",
    type: InventorySectionType = InventorySectionType.DYNAMIC,
    slots: List<Int> = emptyList()
) : AbstractInventorySection(identifier, slots, type) {

    override fun setItem(
        inventory: Inventory,
        player: Player,
        sectionConfigurator: AbstractInventoryConfiguration.SectionConfigurator?
    ) {
        val item = sectionConfigurator?.dynamicItem?.invoke(inventory, player)
            ?.withIdentifier(identifier)?: return

            slots.forEach {
                inventory.setItem(it, item)
            }
    }

}