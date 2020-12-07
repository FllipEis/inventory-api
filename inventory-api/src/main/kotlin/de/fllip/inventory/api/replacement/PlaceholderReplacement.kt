package de.fllip.inventory.api.replacement

import de.fllip.inventory.api.inventory.Inventory
import org.bukkit.entity.Player

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 03.12.2020
 * Time: 19:21
 */
data class PlaceholderReplacement(
    val placeholder: String,
    //TODO: Add inventory information
    val replacement: (Player, Inventory) -> String
)