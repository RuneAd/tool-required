package com.unmoon;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.Menu;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.PostMenuSort;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.cluescrolls.clues.item.AnyRequirementCollection;

import net.runelite.api.Item;
import javax.inject.Inject;

import static net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirements.any;
import static net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirements.item;

@Slf4j
@PluginDescriptor(
	name = "Tool Required"
)
public class ToolRequiredPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ToolRequiredConfig config;

	@Provides
	ToolRequiredConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ToolRequiredConfig.class);
	}

	@Getter
	private Item[] equippedItems = new Item[0];

	@Getter
	private Item[] inventoryItems = new Item[0];

	@Getter
	private Item[] playerItems;

	private static final AnyRequirementCollection ANY_AXE = any("Any Axe",
			item(ItemID.IRON_AXE),
			item(ItemID.BRONZE_AXE),
			item(ItemID.STEEL_AXE),
			item(ItemID.MITHRIL_AXE),
			item(ItemID.ADAMANT_AXE),
			item(ItemID.RUNE_AXE),
			item(ItemID.BLACK_AXE),
			item(ItemID.DRAGON_AXE),
			item(ItemID.BLESSED_AXE),
			item(ItemID.INFERNAL_AXE),
			item(ItemID.INFERNAL_AXE_UNCHARGED),
			item(ItemID._3RD_AGE_AXE),
			item(ItemID.GILDED_AXE),
			item(ItemID.CRYSTAL_AXE),
			item(ItemID.CRYSTAL_AXE_INACTIVE),
			item(ItemID.CORRUPTED_AXE),
			item(ItemID.CRYSTAL_AXE_23862),
			item(ItemID.INFERNAL_AXE_OR),
			item(ItemID.TRAILBLAZER_AXE),
			item(ItemID.INFERNAL_AXE_UNCHARGED_25371),
			item(ItemID.DRAGON_AXE_OR),
			item(ItemID.BRONZE_FELLING_AXE),
			item(ItemID.IRON_FELLING_AXE),
			item(ItemID.STEEL_FELLING_AXE),
			item(ItemID.BLACK_FELLING_AXE),
			item(ItemID.MITHRIL_FELLING_AXE),
			item(ItemID.ADAMANT_FELLING_AXE),
			item(ItemID.RUNE_FELLING_AXE),
			item(ItemID.DRAGON_FELLING_AXE),
			item(ItemID.CRYSTAL_FELLING_AXE),
			item(ItemID.CRYSTAL_FELLING_AXE_INACTIVE),
			item(ItemID._3RD_AGE_FELLING_AXE)
	);

	private static final AnyRequirementCollection ANY_PICKAXE = any("Any Pickaxe",
			item(ItemID.BRONZE_PICKAXE),
			item(ItemID.IRON_PICKAXE),
			item(ItemID.STEEL_PICKAXE),
			item(ItemID.ADAMANT_PICKAXE),
			item(ItemID.MITHRIL_PICKAXE),
			item(ItemID.RUNE_PICKAXE),
			item(ItemID.RUNE_PICKAXE_NZ),
			item(ItemID.MITHRIL_PICKAXE_NZ),
			item(ItemID.IRON_PICKAXE_NZ),
			item(ItemID.DRAGON_PICKAXE),
			item(ItemID.BLACK_PICKAXE),
			item(ItemID.DRAGON_PICKAXE_12797),
			item(ItemID.INFERNAL_PICKAXE),
			item(ItemID.INFERNAL_PICKAXE_UNCHARGED),
			item(ItemID._3RD_AGE_PICKAXE),
			item(ItemID.GILDED_PICKAXE),
			item(ItemID.DRAGON_PICKAXE_OR),
			item(ItemID.CRYSTAL_PICKAXE),
			item(ItemID.CRYSTAL_PICKAXE_INACTIVE),
			item(ItemID.CORRUPTED_PICKAXE),
			item(ItemID.CRYSTAL_PICKAXE_23863),
			item(ItemID.INFERNAL_PICKAXE_OR),
			item(ItemID.TRAILBLAZER_PICKAXE),
			item(ItemID.INFERNAL_PICKAXE_UNCHARGED_25369),
			item(ItemID.DRAGON_PICKAXE_OR_25376)
	);

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged event)
	{
		final ItemContainer itemContainer = event.getItemContainer();
		if (event.getContainerId() == InventoryID.EQUIPMENT.getId())
		{
			equippedItems = itemContainer.getItems();
			playerItems = new Item[equippedItems.length + inventoryItems.length];
			System.arraycopy(equippedItems, 0, playerItems, 0, equippedItems.length);
			System.arraycopy(inventoryItems, 0, playerItems, equippedItems.length, inventoryItems.length);
		}
		else if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
			inventoryItems = itemContainer.getItems();
			playerItems = new Item[equippedItems.length + inventoryItems.length];
			System.arraycopy(equippedItems, 0, playerItems, 0, equippedItems.length);
			System.arraycopy(inventoryItems, 0, playerItems, equippedItems.length, inventoryItems.length);
		}
	}

	@Subscribe
	public void onPostMenuSort(PostMenuSort postMenuSort)
	{
		if (client.isMenuOpen()) {return;}

		Menu root = client.getMenu();
		MenuEntry[] entries = root.getMenuEntries();

		for (MenuEntry entry : entries) {
			if (config.chopDown() && (entry.getOption().equals("Chop down") || entry.getOption().equals("Chop-down")) && !ANY_AXE.fulfilledBy(playerItems)) {
				root.removeMenuEntry(entry);
			}
			else if (config.mine() && entry.getOption().equals("Mine") && !ANY_PICKAXE.fulfilledBy(playerItems)) {
				root.removeMenuEntry(entry);
			}
		}
	}
}
