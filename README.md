
# inventory-api
A file based inventory api for minecraft.

[![Release](https://jitpack.io/v/FllipEis/inventory-api.svg)](https://jitpack.io/#FllipEis/inventory-api)


### 🛠  Development

**Gradle:**

	

	repositories {  
	  maven { url 'https://jitpack.io' }  
	}

    dependencies {  
	  compile('com.github.FllipEis.inventory-api:inventory-api:VERSION')  
	}

**Maven:**

	

	
	<repositories>
	  <repository>
	    <id>jitpack.io</id>
		<url>https://jitpack.io</url>
	  </repository>
	</repositories>

	<dependency>
	   <groupId>com.github.FllipEis.inventory-api</groupId>
	   <artifactId>inventory-api</artifactId>
	   <version>VERSION</version>
	</dependency>
You can find the latest version [here](https://jitpack.io/#FllipEis/inventory-api).

**Initialization:**

To initialize the api you have to call the init method:

    InventoryAPI.init(javaPlugin, DefaultStorageLoader.class);
    
**Register a inventory:**

For every inventory file you have to create an inventory in your plugin.

    InventoryAPI.getInstance().getInventoryCreator().createInventory("inventoryName", new YourCustomInventoryConfiguration());

**Inventory configuration:**

Every inventory needs an inventory configuration. To create an inventory configuration you have to create a class, which extends the `AbstractInventoryConfiguration` class. 

    public class YourCustomInventoryConfiguration extends AbstractInventoryConfiguration {  
	  @Override  
	  public void configure() {  
	    configureSection("sectionIdentifier", new SectionConfigurator());  
	  }  
	}

**Section configurator:**

With the `SectionConfigurator` you can add some configurations to your section. 
You have a few options:

 - EventHandler:

	    new SectionConfigurator()  
		  .withEventHandler(inventoryClickEventResult -> {  
		    //your code  
		    return InventoryClickResult.DENY_GRABBING;  
		  });
	  
 - Placeholder:

	    new SectionConfigurator()  
		  .withPlaceholder(new PlaceholderReplacement("placeholder", (player, inventory) -> {  
		    return "some replacement";  
		  }));


 - Dynamic item:
	
	If you don't want to add the item information, for example the display name, in the inventory file you can use a dynamic section and apply all information in the code.
	 
		 new SectionConfigurator()  
		  .withDynamicItem((inventory, player) -> {  
		    return new InventoryItemStack(Material.OAK_BUTTON);  
		  });

 - Group items:
 
	The group section is very useful, if you want to add pagination to your inventory or if you want to set a lot of items.
	
		new SectionConfigurator()  
		  .withGroupItems(player -> {  
		    return Lists.newArrayList(  
		      new InventoryItemStack(Material.GLASS_BOTTLE),  
		      new InventoryItemStack(Material.GLASS_BOTTLE),  
		      new InventoryItemStack(Material.GLASS_BOTTLE),  
		      new InventoryItemStack(Material.GLASS_BOTTLE)  
		    );  
		  });

**Inventory file:**

Your inventories are based on your inventory files. A simple file looks like this:

    {  
	  "title": "Hello {name}",  
	  "type": "GENERIC_9X6",  
	  "pagination": {  
	    "enabled": true,  
	    "groupIdentifier": "groupExample"  
	  },  
	  "sections": [  
		 {  
		   "type": "PLACEHOLDER",  
		   "material": "BLACK_STAINED_GLASS_PANE",  
		   "slotRange": true,  
		   "slots": [0, 8, 46, 52]  
		 },  
		 {  
		   "type": "STATIC",  
		   "material": "GLASS_BOTTLE",  
		   "displayName": "§b§lGlass bottle",  
		   "amount": 1,  
		   "loreLines": ["§7Page: §b§l{currentPage}"],  
		   "slots": [4],  
		   "identifier": "bottle",  
		   "extras": {}  
		 },  
		 {  
		   "type": "GROUP",  
		   "slotRange": true,  
		   "slots": [20, 24, 29, 33],  
		   "identifier": "groupExample"  
		  },  
		  {  
		    "type": "DYNAMIC",  
		    "slots": [45],  
		    "identifier": "paginationPrevious"  
		  },  
		  {  
		    "type": "DYNAMIC",  
		    "slots": [53],  
		    "identifier": "paginationNext"  
		  }  
	   ]
	 }

To see the full example click [here](https://github.com/FllipEis/inventory-api/tree/master/inventory-example/src/main/java/de/fllip/inventory/example).

**Thanks for using my api.**
