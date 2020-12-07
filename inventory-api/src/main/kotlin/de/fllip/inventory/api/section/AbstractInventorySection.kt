package de.fllip.inventory.api.section

import de.fllip.inventory.api.creator.AbstractInventoryConfiguration
import org.bukkit.entity.Player
import de.fllip.inventory.api.inventory.Inventory

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 17:52
 */
abstract class AbstractInventorySection(
    val identifier: String = "none",
    var slots: List<Int> = emptyList(),
    val type: InventorySectionType = InventorySectionType.NONE
) {

    abstract fun setItem(inventory: Inventory, player: Player, sectionConfigurator: AbstractInventoryConfiguration.SectionConfigurator?)

}