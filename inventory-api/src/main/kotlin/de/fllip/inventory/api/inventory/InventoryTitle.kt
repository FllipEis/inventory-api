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

package de.fllip.inventory.api.inventory

import de.fllip.inventory.api.ReflectionHelper
import de.fllip.inventory.api.type.InventoryType
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryView
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*


/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.12.2020
 * Time: 12:28
 */
class InventoryTitle(
    val title: String
) {

    companion object {

        private lateinit var getHandle: Method
        private lateinit var sendPacket: Method
        private lateinit var activeContainerField: Field
        private lateinit var windowIdField: Field
        private lateinit var playerConnectionField: Field
        private lateinit var containersClass: Class<*>
        private lateinit var chatMessageConstructor: Constructor<*>
        private lateinit var packetPlayOutOpenWindowConstructor: Constructor<*>

        init {
            try {
                getHandle = ReflectionHelper.obcClass("entity.CraftPlayer").getMethod("getHandle")
                chatMessageConstructor = ReflectionHelper.nmsClass("ChatMessage").getConstructor(
                    String::class.java,
                    Array<Any>::class.java
                )
                val nmsPlayer: Class<*> = ReflectionHelper.nmsClass("EntityPlayer")
                activeContainerField = nmsPlayer.getField("activeContainer")
                windowIdField = ReflectionHelper.nmsClass("Container").getField("windowId")
                playerConnectionField = nmsPlayer.getField("playerConnection")
                containersClass = ReflectionHelper.nmsClass("Containers")
                packetPlayOutOpenWindowConstructor =
                    ReflectionHelper.nmsClass("PacketPlayOutOpenWindow").getConstructor(
                        Integer.TYPE,
                        containersClass, ReflectionHelper.nmsClass("IChatBaseComponent")
                    )
                sendPacket = ReflectionHelper.nmsClass("PlayerConnection")
                    .getMethod("sendPacket", ReflectionHelper.nmsClass("Packet"))
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    fun execute(player: Player, type: InventoryType): InventoryTitle {
        this.openInventory(player).ifPresent { inventoryView ->
            if (inventoryView.title.equals("container.crafting", true)) return@ifPresent
            try {
                val handle = getHandle.invoke(player)
                val message = chatMessageConstructor.newInstance(this.title, arrayOfNulls<Any>(0))
                val container = activeContainerField[handle]
                val containerType = containersClass.getField(type.name.toUpperCase()).get(null)
                val windowId = windowIdField[container]
                val packet = packetPlayOutOpenWindowConstructor.newInstance(
                    windowId,
                    containerType,
                    message
                )
                val playerConnection = playerConnectionField[handle]
                sendPacket.invoke(playerConnection, packet)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            player.updateInventory()
        }
        return this
    }

    private fun openInventory(player: Player): Optional<InventoryView> {
        return Optional.of(player.openInventory)
    }

}