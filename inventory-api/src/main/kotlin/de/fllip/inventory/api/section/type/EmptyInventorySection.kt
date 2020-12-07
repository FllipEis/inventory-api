package de.fllip.inventory.api.section.type

import de.fllip.inventory.api.creator.AbstractInventoryConfiguration
import de.fllip.inventory.api.section.AbstractInventorySection
import org.bukkit.entity.Player
import de.fllip.inventory.api.inventory.Inventory

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 23:04
 */
class EmptyInventorySection : AbstractInventorySection() {

    override fun setItem(
        inventory: Inventory,
        player: Player,
        sectionConfigurator: AbstractInventoryConfiguration.SectionConfigurator?
    ) {}

}