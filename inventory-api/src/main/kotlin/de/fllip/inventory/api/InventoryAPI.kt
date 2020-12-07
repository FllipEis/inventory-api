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