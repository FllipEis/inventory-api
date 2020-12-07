/*
 * MIT License
 *
 * Copyright (c) 2020 Philipp Eistrach
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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