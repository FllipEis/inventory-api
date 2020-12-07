package de.fllip.inventory.api.creator

import de.fllip.inventory.api.file.InventoryFile

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 04.12.2020
 * Time: 11:24
 */
data class InventoryInformation(
    val inventoryName: String,
    val inventoryFile: InventoryFile,
    val inventoryConfiguration: AbstractInventoryConfiguration
)
