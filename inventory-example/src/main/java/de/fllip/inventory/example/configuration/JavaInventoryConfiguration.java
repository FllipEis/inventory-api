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

import com.google.common.collect.Lists;
import de.fllip.inventory.api.creator.AbstractInventoryConfiguration;
import de.fllip.inventory.api.replacement.PlaceholderReplacement;
import de.fllip.inventory.api.result.InventoryClickResult;
import de.fllip.inventory.api.section.bukkit.InventoryItemStack;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 19:08
 */
public class JavaInventoryConfiguration extends AbstractInventoryConfiguration {

    @Override
    public void configure() {
        configureTitlePlaceholder(new PlaceholderReplacement("name", (player, inventory) -> player.getName()));

        configureSection("bottle", new SectionConfigurator()
                .withEventHandler(result -> {
                    result.getPlayer().sendMessage("§7Clicked");

                    return InventoryClickResult.DENY_GRABBING;
                })
                .withPlaceholder(new PlaceholderReplacement("currentPage", (player, inventory) -> String.valueOf(inventory.getCurrentPage())))
        );

        configureSection("stateExample", new SectionConfigurator()
                .withEventHandler(result -> {
                    result.getPlayer().playSound(result.getPlayer().getLocation(), Sound.BLOCK_LAVA_POP, 1F, 5F);

                    return InventoryClickResult.DENY_GRABBING;
                })
                .withFirstState(player -> {
                    return "on";
                })
                .withStateHandler(result -> {
                    result.getPlayer().sendMessage("§7State changed: §b§l${result.changedState}");
                })
        );

        configureSection("groupExample", new SectionConfigurator()
                .withGroupItems(player -> {
                    List<InventoryItemStack> items = Lists.newArrayList();

                    for (int i = 1; i <= 100; i++) {
                        items.add(new InventoryItemStack(Material.GLASS_BOTTLE)
                                .withDisplayName("§b§lLobby-" + i)
                        );
                    }

                    return items;
                })
        );

        configureSection("paginationPrevious", new SectionConfigurator()
                .withDynamicItem((inventory, player) -> {
                    if (!inventory.hasPreviousPage()) {
                        return new InventoryItemStack(Material.RED_STAINED_GLASS_PANE)
                                .withDisplayName("§cPrevious page");
                    } else {
                        return new InventoryItemStack(Material.LIME_STAINED_GLASS_PANE)
                                .withDisplayName("§aPrevious page");
                    }
                })
                .withEventHandler(result -> {
                    if (result.getInventory().hasPreviousPage()) {
                        result.getInventory().openPreviousPage();
                        result.getPlayer().playSound(result.getPlayer().getLocation(), Sound.BLOCK_BEEHIVE_ENTER, 1F, 1F);
                    } else {
                        result.getPlayer().playSound(result.getPlayer().getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 10F);
                    }

                    return InventoryClickResult.DENY_GRABBING;
                })
        );

        configureSection("paginationNext", new SectionConfigurator()
                .withDynamicItem((inventory, player) -> {
                    if (!inventory.hasPreviousPage()) {
                        return new InventoryItemStack(Material.RED_STAINED_GLASS_PANE)
                                .withDisplayName("§cNext page");
                    } else {
                        return new InventoryItemStack(Material.LIME_STAINED_GLASS_PANE)
                                .withDisplayName("§aNext page");
                    }
                })
                .withEventHandler(result -> {
                    if (result.getInventory().hasNextPage()) {
                        result.getInventory().openNextPage();
                        result.getPlayer().playSound(result.getPlayer().getLocation(), Sound.BLOCK_BEEHIVE_ENTER, 1F, 1F);
                    } else {
                        result.getPlayer().playSound(result.getPlayer().getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 10F);
                    }

                    return InventoryClickResult.DENY_GRABBING;
                })
        );
    }

}
