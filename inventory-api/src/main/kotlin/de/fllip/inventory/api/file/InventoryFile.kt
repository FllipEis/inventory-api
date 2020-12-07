package de.fllip.inventory.api.file

import de.fllip.inventory.api.pagination.PaginationInformation
import de.fllip.inventory.api.section.AbstractInventorySection
import de.fllip.inventory.api.type.InventoryType

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 17:43
 */
data class InventoryFile(
    val title: String = "",
    val type: InventoryType = InventoryType.NONE,
    val pagination: PaginationInformation = PaginationInformation(),
    val sections: List<AbstractInventorySection> = emptyList()
)