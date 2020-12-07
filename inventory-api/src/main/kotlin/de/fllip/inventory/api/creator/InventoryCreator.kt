package de.fllip.inventory.api.creator

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.inject.Inject
import com.google.inject.Singleton
import de.fllip.inventory.api.result.InventoryClickResult
import de.fllip.inventory.api.storage.IStorageLoader
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 18:53
 */
@Singleton
class InventoryCreator @Inject constructor(
    private val storageLoader: IStorageLoader
) {

    val inventories = Lists.newArrayList<InventoryInformation>()!!

    fun createInventory(inventoryName: String, configuration: AbstractInventoryConfiguration) {
        val inventoryFile = storageLoader.loadOrGetFile(inventoryName)

        configuration.configureSection("placeholder", AbstractInventoryConfiguration.SectionConfigurator()
            .withEventHandler { InventoryClickResult.DENY_GRABBING }
        )
        configuration.configure()

        inventories.add(
            InventoryInformation(
            inventoryName,
            inventoryFile,
            configuration
        )
        )
    }

}