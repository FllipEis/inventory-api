package de.fllip.inventory.api.type

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 17:50
 */
enum class InventoryType(
    val slots: Int
) {

    GENERIC_9X1(9 * 1),
    GENERIC_9X2(9 * 2),
    GENERIC_9X3(9 * 3),
    GENERIC_9X4(9 * 4),
    GENERIC_9X5(9 * 5),
    GENERIC_9X6(9 * 6),
    GENERIC_3X3(9 * 7),
    NONE(0)

}