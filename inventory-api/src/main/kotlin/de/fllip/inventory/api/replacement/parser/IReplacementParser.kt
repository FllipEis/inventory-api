package de.fllip.inventory.api.replacement.parser

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 05.12.2020
 * Time: 12:32
 */
interface IReplacementParser {

    fun parse(text: String): String

}