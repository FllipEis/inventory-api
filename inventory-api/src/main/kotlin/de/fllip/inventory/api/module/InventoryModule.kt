package de.fllip.inventory.api.module

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import de.fllip.inventory.api.section.AbstractInventorySection
import de.fllip.inventory.api.section.deserializer.InventorySectionDeserializer
import de.fllip.inventory.api.storage.IStorageLoader
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 17:41
 */
class InventoryModule(
    private val javaPlugin: JavaPlugin,
    private val storageLoaderClass: Class<out IStorageLoader>
) : AbstractModule() {

    override fun configure() {
        val objectMapper = ObjectMapper()
        val module = SimpleModule()
        module.addDeserializer(AbstractInventorySection::class.java, InventorySectionDeserializer())
        objectMapper.registerModule(module)

        bind(IStorageLoader::class.java).to(storageLoaderClass)
        bind(ObjectMapper::class.java).annotatedWith(Names.named("inventoryapi")).toInstance(objectMapper)
        bind(JavaPlugin::class.java).toInstance(javaPlugin)
    }

}