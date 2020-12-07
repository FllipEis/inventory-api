package de.fllip.inventory.api.pagination

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.12.2020
 * Time: 12:44
 */
data class PaginationInformation(
    val enabled: Boolean = false,
    val groupIdentifier: String = "",
)
