package de.fllip.inventory.api.section.type

import de.fllip.inventory.api.creator.AbstractInventoryConfiguration
import de.fllip.inventory.api.inventory.Inventory
import de.fllip.inventory.api.section.AbstractInventorySection
import de.fllip.inventory.api.section.InventorySectionType
import org.bukkit.entity.Player

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
        val items = sectionConfigurator?.groupItems?.invoke(player)
            ?.map { it.withIdentifier(identifier) } ?: emptyList()

        val inventorySlots = if (slotRange) {
            slots.chunked(2)
                .map { IntRange(it.first(), it.last()).toList() }
                .flatten()
        } else slots

        inventory.addGroupItems(identifier, items, inventorySlots)
    }

}