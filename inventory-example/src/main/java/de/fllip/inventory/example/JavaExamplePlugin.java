package de.fllip.inventory.example;

import de.fllip.inventory.api.InventoryAPI;
import de.fllip.inventory.api.storage.DefaultStorageLoader;
import de.fllip.inventory.example.configuration.JavaInventoryConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 02.12.2020
 * Time: 19:11
 */
public class JavaExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        InventoryAPI.init(this, DefaultStorageLoader.class);

        InventoryAPI.getInstance().getInventoryCreator().createInventory(
                "inv",
                new JavaInventoryConfiguration()
        );
    }
}
