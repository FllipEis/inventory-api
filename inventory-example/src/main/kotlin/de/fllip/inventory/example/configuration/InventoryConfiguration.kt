package de.fllip.inventory.example.configuration

import com.google.common.collect.Lists
import de.fllip.inventory.api.creator.AbstractInventoryConfiguration
import de.fllip.inventory.api.replacement.PlaceholderReplacement
import de.fllip.inventory.api.result.InventoryClickResult
import de.fllip.inventory.api.section.bukkit.InventoryItemStack
import org.bukkit.Material
import org.bukkit.Sound

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 19:05
 */
class InventoryConfiguration : AbstractInventoryConfiguration() {

    override fun configure() {
        configureTitlePlaceholder(PlaceholderReplacement("name") { player, _ ->
            player.name
        })

        configureSection("skull", SectionConfigurator()
            .withEventHandler {
                it.player.sendMessage("§7Folgt mir auf Twitter: §b§l_Fllip")
                InventoryClickResult.DENY_GRABBING
            }
            .withPlaceholder(PlaceholderReplacement("currentPage") { _, inventory ->
                inventory.currentPage.toString()
            })
        )

        configureSection("groupTest", SectionConfigurator()
            .withGroupItems {
                val items = Lists.newArrayList<InventoryItemStack>()

                for (i: Int in 1..100) {
                    items.add(InventoryItemStack(Material.GLASS_BOTTLE)
                        .withDisplayName("§b§lLobby-$i")
                    )
                }

                items
            }
        )

        configureSection("paginationPrevious", SectionConfigurator()
            .withDynamicItem { inventory, player ->
                if (!inventory.hasPreviousPage()) {
                    return@withDynamicItem InventoryItemStack(Material.RED_STAINED_GLASS_PANE)
                        .withDisplayName("§cVorherige Seite")
                } else {
                    return@withDynamicItem InventoryItemStack(Material.LIME_STAINED_GLASS_PANE)
                        .withDisplayName("§aVorherige Seite")
                }
            }
            .withEventHandler {
                if (it.inventory.hasPreviousPage()) {
                    it.inventory.openPreviousPage()
                    it.player.playSound(it.player.location, Sound.BLOCK_BEEHIVE_ENTER, 1F, 1F)
                } else {
                    it.player.playSound(it.player.location, Sound.BLOCK_ANVIL_LAND, 1F, 10F)
                }

                InventoryClickResult.DENY_GRABBING
            }
        )

        configureSection("paginationNext", SectionConfigurator()
            .withDynamicItem { inventory, player ->
                if (!inventory.hasNextPage()) {
                    return@withDynamicItem InventoryItemStack(Material.RED_STAINED_GLASS_PANE)
                        .withDisplayName("§cNächste Seite")
                } else {
                    return@withDynamicItem InventoryItemStack(Material.LIME_STAINED_GLASS_PANE)
                        .withDisplayName("§aNächste Seite")
                }
            }
            .withEventHandler {
                if (it.inventory.hasNextPage()) {
                    it.inventory.openNextPage()
                    it.player.playSound(it.player.location, Sound.BLOCK_BEEHIVE_ENTER, 1F, 1F)
                } else {
                    it.player.playSound(it.player.location, Sound.BLOCK_ANVIL_LAND, 1F, 10F)
                }

                InventoryClickResult.DENY_GRABBING
            }
        )
    }

}