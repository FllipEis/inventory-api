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