package de.fllip.inventory.api.section.type

import de.fllip.inventory.api.creator.AbstractInventoryConfiguration
import de.fllip.inventory.api.section.AbstractInventorySection
import de.fllip.inventory.api.section.InventorySectionExtra
import de.fllip.inventory.api.section.InventorySectionType
import de.fllip.inventory.api.section.bukkit.InventoryItemStack
import de.fllip.inventory.api.replacement.PlaceholderReplacer
import org.bukkit.Material
import org.bukkit.entity.Player
import de.fllip.inventory.api.inventory.Inventory

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 17:57
 */
class StaticInventorySection(
    identifier: String = "",
    type: InventorySectionType = InventorySectionType.STATIC,
    val material: Material = Material.AIR,
    val displayName: String = "",
    val amount: Int = 1,
    val loreLines: List<String> = emptyList(),
    slots: List<Int> = emptyList(),
    val extras: Map<InventorySectionExtra, String> = emptyMap()
) : AbstractInventorySection(identifier, slots, type) {

    override fun setItem(
        inventory: Inventory,
        player: Player,
        sectionConfigurator: AbstractInventoryConfiguration.SectionConfigurator?
    ) {
        val replacements = sectionConfigurator?.placeholders?: emptyList()

        val item = InventoryItemStack(material, amount)
            .withIdentifier(identifier)
            .withDisplayName(PlaceholderReplacer.replace(displayName, player, inventory, replacements))
            .withLore(loreLines.map { PlaceholderReplacer.replace(it, player, inventory, replacements) })
            .withExtras(extras)

        slots.forEach {
            inventory.setItem(it, item)
        }
    }

}