package de.fllip.inventory.api.replacement

import de.fllip.inventory.api.inventory.Inventory
import de.fllip.inventory.api.replacement.parser.DefaultReplacementParser
import de.fllip.inventory.api.replacement.parser.IReplacementParser
import org.bukkit.entity.Player

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 03.12.2020
 * Time: 21:29
 */
object PlaceholderReplacer {

    var parser: IReplacementParser = DefaultReplacementParser()

    fun replace(text: String, player: Player, inventory: Inventory, replacements: List<PlaceholderReplacement>): String {
        var parsedText = parser.parse(text)

        replacements.forEach {
            parsedText = parsedText.replace("{${it.placeholder}}", it.replacement.invoke(player, inventory))
        }

        return parsedText
    }

    fun withParser(parser: IReplacementParser): PlaceholderReplacer {
        this.parser = parser

        return this
    }

}