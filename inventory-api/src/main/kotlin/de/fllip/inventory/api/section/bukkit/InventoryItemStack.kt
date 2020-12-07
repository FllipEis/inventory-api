package de.fllip.inventory.api.section.bukkit

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.fllip.inventory.api.section.InventorySectionExtra
import de.tr7zw.changeme.nbtapi.NBTItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.material.MaterialData
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 03.12.2020
 * Time: 21:22
 */
class InventoryItemStack(
    material: Material,
    amount: Int = 1
) : ItemStack(material, amount) {

    companion object {
        fun fromItemStack(itemStack: ItemStack?): InventoryItemStack? {
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