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

package de.fllip.inventory.example.configuration;

import de.fllip.inventory.api.creator.AbstractInventoryConfiguration;
import de.fllip.inventory.api.replacement.PlaceholderReplacement;
import de.fllip.inventory.api.result.InventoryClickResult;

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 19:08
 */
public class JavaInventoryConfiguration extends AbstractInventoryConfiguration {

    @Override
    public void configure() {
        configureSection("test", new SectionConfigurator()
                .withEventHandler(event -> {
                    event.getPlayer().sendMessage("Hallo");

                    return InventoryClickResult.DENY_GRABBING;
                })
                .withPlaceholder(new PlaceholderReplacement("name", (player, inventory) -> String.valueOf(inventory.getCurrentPage())))
        );
    }

}
