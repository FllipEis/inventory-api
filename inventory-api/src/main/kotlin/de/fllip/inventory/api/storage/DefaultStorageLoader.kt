package de.fllip.inventory.api.storage

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.inject.Inject
import com.google.inject.name.Named
import de.fllip.inventory.api.file.InventoryFile
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 18:18
 */
class DefaultStorageLoader @Inject constructor(
    @Named("inventoryapi")
    private val objectMapper: ObjectMapper
) : IStorageLoader {

    private val files = Maps.newHashMap<String, InventoryFile>()

    override fun loadFile(inventoryName: String): InventoryFile {
        val inputStream = this.javaClass.classLoader.getResourceAsStream("$inventoryName.json")

        return objectMapper.readValue(inputStream, InventoryFile::class.java)
    }

    override fun loadOrGetFile(inventoryName: String): InventoryFile {
        return getFile(inventoryName)?: loadFile(inventoryName)
    }

    override fun getFile(inventoryName: String): InventoryFile? {
        return files[inventoryName]
    }

    override fun getLoadedFiles(): List<InventoryFile> {
        return files.values.toMutableList()
    }

}