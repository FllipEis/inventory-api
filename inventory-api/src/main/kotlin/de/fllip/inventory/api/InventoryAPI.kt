package de.fllip.inventory.api

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import de.fllip.inventory.api.creator.InventoryCreator
import de.fllip.inventory.api.inventory.InventoryService
import de.fllip.inventory.api.listener.InventoryListener
import de.fllip.inventory.api.module.InventoryModule
import de.fllip.inventory.api.storage.DefaultStorageLoader
import de.fllip.inventory.api.storage.IStorageLoader
import de.tr7zw.changeme.nbtapi.data.NBTData
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 17:42
 */
@Singleton
class InventoryAPI @Inject constructor(
    val storageLoader: IStorageLoader,
    val inventoryCreator: InventoryCreator,
    val inventoryService: InventoryService,
    private val injector: Injector
) {

    companion object {
        @JvmStatic
        lateinit var instance: InventoryAPI
            private set

        @JvmStatic
        fun init(
            javaPlugin: JavaPlugin,
            storageLoaderClass: Class<out IStorageLoader> = DefaultStorageLoader::class.java
        ) {
            val injector = Guice.createInjector(InventoryModule(javaPlugin, storageLoaderClass))
            instance = injector.getInstance(InventoryAPI::class.java)
            register(injector, javaPlugin)
        }

        @JvmStatic
        fun register(injector: Injector, javaPlugin: JavaPlugin) {
            Bukkit.getPluginManager().registerEvents(injector.getInstance(InventoryListener::class.java), javaPlugin)
        }
    }

}