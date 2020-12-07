package de.fllip.inventory.api.section

import com.fasterxml.jackson.annotation.JsonFormat
import de.fllip.inventory.api.section.type.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 17:54
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
enum class InventorySectionType(
    val inventoryClass: Class<out AbstractInventorySection>
) {

    STATIC(StaticInventorySection::class.java),
    DYNAMIC(DynamicInventorySection::class.java),
    GROUP(GroupInventorySection::class.java),
    PLACEHOLDER(PlaceholderInventorySection::class.java),
    NONE(EmptyInventorySection::class.java)

}