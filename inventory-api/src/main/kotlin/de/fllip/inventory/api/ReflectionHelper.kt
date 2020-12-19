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

import org.bukkit.Bukkit
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 15.12.2020
 * Time: 12:31
 */
object ReflectionHelper {

    private val OBC_PACKAGE: String
    private val NMS_PACKAGE: String
    private val VERSION: String

    private val SEND_PACKET: Method
    private val PLAYER_GET_HANDLE: Method

    private val PLAYER_CONNECTION: Field

    init {
        OBC_PACKAGE = "org.bukkit.craftbukkit"
        NMS_PACKAGE = "net.minecraft.server"
        VERSION = Bukkit.getServer()::class.java.getPackage().name.substring(OBC_PACKAGE.length + 1)

        try {
            val craftPlayerClass: Class<*> = obcClass("entity.CraftPlayer")
            val entityPlayerClass: Class<*> = nmsClass("EntityPlayer")
            val playerConnectionClass: Class<*> = nmsClass("PlayerConnection")
            PLAYER_GET_HANDLE = craftPlayerClass.getDeclaredMethod("getHandle")
            PLAYER_CONNECTION = entityPlayerClass.getDeclaredField("playerConnection")
            SEND_PACKET = playerConnectionClass.getDeclaredMethod(
                "sendPacket",
                nmsClass("Packet")
            )
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }

    fun nmsClassName(className: String): String? {
        return "$NMS_PACKAGE.$VERSION.$className"
    }

    fun nmsClass(className: String): Class<*> {
        return Class.forName(nmsClassName(className))
    }

    fun obcClassName(className: String): String? {
        return "$OBC_PACKAGE.$VERSION.$className"
    }

    fun obcClass(className: String): Class<*> {
        return Class.forName(obcClassName(className))
    }

}