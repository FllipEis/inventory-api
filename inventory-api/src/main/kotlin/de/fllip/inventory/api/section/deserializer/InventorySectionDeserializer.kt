package de.fllip.inventory.api.section.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import de.fllip.inventory.api.section.InventorySectionType
import de.fllip.inventory.api.section.AbstractInventorySection
import de.fllip.inventory.api.section.type.EmptyInventorySection

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 22:39
 */
class InventorySectionDeserializer : StdDeserializer<AbstractInventorySection>(AbstractInventorySection::class.java) {

    private val objectMapper = ObjectMapper()

    override fun deserialize(parser: JsonParser, context: DeserializationContext): AbstractInventorySection {
        val node = parser.codec.readTree<JsonNode>(parser)

        val typeString = node.get("type")?.asText()?: InventorySectionType.NONE.toString()
        val type = InventorySectionType.valueOf(typeString)

        val inventoryItem = objectMapper.readValue(node.toString(), type.inventoryClass) ?: EmptyInventorySection()
        return inventoryItem
    }
}