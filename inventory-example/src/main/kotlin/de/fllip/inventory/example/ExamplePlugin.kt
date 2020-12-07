package de.fllip.inventory.example

import de.fllip.inventory.api.InventoryAPI
import de.fllip.inventory.api.replacement.PlaceholderReplacer
import de.fllip.inventory.api.section.bukkit.InventoryItemStack
import de.fllip.inventory.api.storage.DefaultStorageLoader
import de.fllip.inventory.example.configuration.InventoryConfiguration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 19:03
 */
class ExamplePlugin : JavaPlugin() {

    override fun onEnable() {
        InventoryAPI.init(this, DefaultStorageLoader::class.java)

        InventoryAPI.instance.inventoryCreator.createInventory(
            "test",
            InventoryConfiguration()
        )
    }

}