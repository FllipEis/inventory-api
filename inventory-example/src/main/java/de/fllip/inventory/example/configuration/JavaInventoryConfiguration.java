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
