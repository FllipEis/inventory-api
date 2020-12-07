package de.fllip.inventory.api.storage

import de.fllip.inventory.api.file.InventoryFile

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 17:42
 */
interface IStorageLoader {

    fun loadFile(inventoryName: String): InventoryFile

    fun loadOrGetFile(inventoryName: String): InventoryFile

    fun getFile(inventoryName: String): InventoryFile?

    fun getLoadedFiles(): List<InventoryFile>

}