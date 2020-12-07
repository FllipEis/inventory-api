package de.fllip.inventory.api.section.type

import de.fllip.inventory.api.creator.AbstractInventoryConfiguration
import de.fllip.inventory.api.section.AbstractInventorySection
import de.fllip.inventory.api.section.InventorySectionType
import de.fllip.inventory.api.section.bukkit.InventoryItemStack
import org.bukkit.Material
import org.bukkit.entity.Player
import de.fllip.inventory.api.inventory.Inventory

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 04.12.2020
 * Time: 12:13
 */
class PlaceholderInventorySection(
    type: InventorySectionType = InventorySectionType.PLACEHOLDER,
    val material: Material = Material.AIR,
    val slotRange: Boolean = false,
    slots: List<Int> = emptyList()
) : AbstractInventorySection("placeholder", slots, type) {

    override fun setItem(
        inventory: Inventory,
        player: Player,
        sectionConfigurator: AbstractInventoryConfiguration.SectionConfigurator?
    ) {
        val item = InventoryItemStack(material)
            .withIdentifier("placeholder")

        val inventorySlots = if (slotRange) {
            slots.chunked(2)
                .map { IntRange(it.first(), it.last()).toList() }
                .flatten()
        } else slots

        inventorySlots.forEach {
            inventory.setItem(it, item)
        }
    }

}