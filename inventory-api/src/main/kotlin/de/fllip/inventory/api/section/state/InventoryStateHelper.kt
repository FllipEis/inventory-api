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

package de.fllip.inventory.api.section.state

import com.google.common.collect.Lists
import org.bukkit.entity.Player

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 08.12.2020
 * Time: 11:50
 */
object InventoryStateHelper {

    private val states = Lists.newArrayList<InventoryStateCache>()

    fun getCurrentState(player: Player, identifier: String, firstState: String?, allStates: List<InventoryStateInformation>): InventoryStateInformation {
        if (allStates.isEmpty()) {
            throw UnsupportedOperationException("allStates cannot be empty")
        }

        var cache = states.firstOrNull { it.player == player && it.identifier == identifier }

        if (cache == null) {
            cache = InventoryStateCache(player, identifier, firstState?: allStates.first().stateName, allStates)
            states.add(cache)
        }

        val currentState = cache.states.first { it.stateName == cache.currentstate }

        return currentState
    }

    fun nextState(player: Player, identifier: String): String {
        val cache = states.firstOrNull { it.player == player && it.identifier == identifier }?: return ""
        val currentState = cache.states.first { it.stateName == cache.currentstate }

        var nextStateIndex =  cache.states.indexOf(currentState) + 1

        if (nextStateIndex == cache.states.size) {
            nextStateIndex = 0
        }

        val newStateName = cache.states[nextStateIndex].stateName
        cache.currentstate = newStateName

        return newStateName
    }

    fun previousState(player: Player, identifier: String): String {
        val cache = states.firstOrNull { it.player == player && it.identifier == identifier }?: return ""
        val currentState = cache.states.first { it.stateName == cache.currentstate }

        var previousStateIndex = cache.states.indexOf(currentState) - 1

        if (previousStateIndex < 0) {
           previousStateIndex = cache.states.size - 1
        }

        val newStateName = cache.states[previousStateIndex].stateName
        cache.currentstate = newStateName

        return newStateName
    }

    fun destroyStatesOfPlayer(player: Player) {
        states.removeIf {it.player == player }
    }

}