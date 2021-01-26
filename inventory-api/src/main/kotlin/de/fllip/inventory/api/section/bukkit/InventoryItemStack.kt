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

package de.fllip.inventory.api.section.bukkit

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.fllip.inventory.api.section.InventorySectionExtra
import de.tr7zw.changeme.nbtapi.NBTItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.material.MaterialData
import org.bukkit.persistence.PersistentDataType
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 03.12.2020
 * Time: 21:22
 */
class InventoryItemStack(
    material: Material,
    amount: Int
) : ItemStack(material, amount) {

    constructor(material: Material) : this(material, 1)

    companion object {
        fun fromItemStack(itemStack: ItemStack?): InventoryItemStack? {
            if (itemStack?.type == Material.AIR) {
                return null
            }

            val serialize = itemStack?.serialize() ?: return null
            return deserialize(serialize)
        }

        fun deserialize(args: Map<String, Any>): InventoryItemStack {
            val version = if (args.containsKey("v")) (args["v"] as Number?)!!.toInt() else -1
            var damage: Short = 0
            var amount = 1
            if (args.containsKey("damage")) {
                damage = (args["damage"] as Number?)!!.toShort()
            }
            var type: Material?
            if (version < 0) {
                type = Material.getMaterial(Material.LEGACY_PREFIX + args["type"] as String?)
                val dataVal =
                    if (type != null && type.maxDurability.toInt() == 0) damage.toByte() else 0 // Actually durable items get a 0 passed into conversion
                type = Bukkit.getUnsafe().fromLegacy(MaterialData(type, dataVal), true)

                // We've converted now so the data val isn't a thing and can be reset
                if (dataVal.toInt() != 0) {
                    damage = 0
                }
            } else {
                type = Bukkit.getUnsafe().getMaterial(args["type"] as String?, version)
            }
            if (args.containsKey("amount")) {
                amount = (args["amount"] as Number?)!!.toInt()
            }
            val result = InventoryItemStack(type, amount)
            if (args.containsKey("enchantments")) { // Backward compatiblity, @deprecated
                val raw = args["enchantments"]
                if (raw is Map<*, *>) {
                    for ((key, value) in raw) {
                        val enchantment = Enchantment.getByName(key.toString())
                        if (enchantment != null && value is Int) {
                            result.addUnsafeEnchantment(enchantment, (value as Int?)!!)
                        }
                    }
                }
            } else if (args.containsKey("meta")) { // We cannot and will not have meta when enchantments (pre-ItemMeta) exist
                val raw = args["meta"]
                if (raw is ItemMeta) {
                    raw.setVersion(version)
                    result.itemMeta = raw
                }
            }
            if (version < 0) {
                // Set damage again incase meta overwrote it
                if (args.containsKey("damage")) {
                    result.durability = damage
                }
            }
            return result
        }

    }

    init {
        withNBTTag("uuid", UUID.randomUUID().toString())
    }

    fun getUniqueId(): UUID {
        return UUID.fromString(getNBTTag("uuid"))
    }

    fun withDisplayName(displayName: String): InventoryItemStack {
        val itemMeta = this.itemMeta!!
        itemMeta.setDisplayName(displayName)
        this.itemMeta = itemMeta

        return this
    }

    fun withLore(lines: List<String>): InventoryItemStack {
        val itemMeta = this.itemMeta!!
        itemMeta.lore = lines
        this.itemMeta = itemMeta

        return this
    }

    fun withSkullTexture(texture: String): InventoryItemStack {
        val itemMeta = itemMeta!!

        val field = itemMeta::class.java.getDeclaredField("profile")
        field.isAccessible = true
        field.set(itemMeta, this.buildProfile(texture))

        this.itemMeta = itemMeta

        return this
    }

    fun withExtra(extra: InventorySectionExtra, value: String): InventoryItemStack {
        if (extra == InventorySectionExtra.SKULL_TEXTURE) {
            this.withSkullTexture(value)
        }

        return this
    }

    fun withExtras(extras: Map<InventorySectionExtra, String>): InventoryItemStack {
        extras.forEach { (key, value) ->
            this.withExtra(key, value)
        }

        return this
    }

    fun withNBTTag(tag: String, value: String): InventoryItemStack {
        val item = NBTItem(this)
        item.setString(tag, value)

        item.applyNBT(this)

        return this
    }

    fun getNBTTag(tag: String): String {
        val item = NBTItem(this)
        return item.getString(tag) ?: ""
    }

    fun withIdentifier(identifier: String): InventoryItemStack {
        return this.withNBTTag("inventory-identifier", identifier)
    }

    fun <T, Z> withItemData(
        nameSpacedKey: NamespacedKey,
        persistenceDataType: PersistentDataType<T, Z>,
        value: Z
    ): InventoryItemStack {
        val itemMeta = itemMeta!!

        itemMeta.persistentDataContainer.set(nameSpacedKey, persistenceDataType, value)

        this.itemMeta = itemMeta

        return this
    }

    fun <T, Z> getItemData(
        nameSpacedKey: NamespacedKey,
        persistenceDataType: PersistentDataType<T, Z>
    ): Z? {
        val itemMeta = itemMeta!!

        return itemMeta.persistentDataContainer.get(nameSpacedKey, persistenceDataType)
    }

    fun getIdentifier(): String {
        return getNBTTag("inventory-identifier")
    }

    private fun buildProfile(texture: String): GameProfile {
        val uuid = UUID.randomUUID()
        val profile = GameProfile(uuid, null)

        profile.properties.put(
            "textures",
            Property("textures", texture)
        )

        return profile
    }

}