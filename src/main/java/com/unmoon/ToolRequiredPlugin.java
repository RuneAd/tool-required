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

import java.util.Arrays;
import java.util.List;

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

	private static final AnyRequirementCollection ANY_AXE = createAnyRequirement("Any Axe",
			Arrays.asList(
				ItemID.IRON_AXE,
				ItemID.BRONZE_AXE,
				ItemID.STEEL_AXE,
				ItemID.MITHRIL_AXE,
				ItemID.ADAMANT_AXE,
				ItemID.RUNE_AXE,
				ItemID.BLACK_AXE,
				ItemID.DRAGON_AXE,
				ItemID.BLESSED_AXE,
				ItemID.INFERNAL_AXE,
				ItemID.INFERNAL_AXE_UNCHARGED,
				ItemID._3RD_AGE_AXE,
				ItemID.GILDED_AXE,
				ItemID.CRYSTAL_AXE,
				ItemID.CRYSTAL_AXE_INACTIVE,
				ItemID.CORRUPTED_AXE,
				ItemID.CRYSTAL_AXE_23862,
				ItemID.INFERNAL_AXE_OR,
				ItemID.TRAILBLAZER_AXE,
				ItemID.INFERNAL_AXE_UNCHARGED_25371,
				ItemID.DRAGON_AXE_OR,
				ItemID.BRONZE_FELLING_AXE,
				ItemID.IRON_FELLING_AXE,
				ItemID.STEEL_FELLING_AXE,
				ItemID.BLACK_FELLING_AXE,
				ItemID.MITHRIL_FELLING_AXE,
				ItemID.ADAMANT_FELLING_AXE,
				ItemID.RUNE_FELLING_AXE,
				ItemID.DRAGON_FELLING_AXE,
				ItemID.CRYSTAL_FELLING_AXE,
				ItemID.CRYSTAL_FELLING_AXE_INACTIVE,
				ItemID._3RD_AGE_FELLING_AXE
			)
	);

	private static final AnyRequirementCollection ANY_PICKAXE = createAnyRequirement("Any Pickaxe",
			Arrays.asList(
				ItemID.BRONZE_PICKAXE,
				ItemID.IRON_PICKAXE,
				ItemID.STEEL_PICKAXE,
				ItemID.ADAMANT_PICKAXE,
				ItemID.MITHRIL_PICKAXE,
				ItemID.RUNE_PICKAXE,
				ItemID.RUNE_PICKAXE_NZ,
				ItemID.MITHRIL_PICKAXE_NZ,
				ItemID.IRON_PICKAXE_NZ,
				ItemID.DRAGON_PICKAXE,
				ItemID.BLACK_PICKAXE,
				ItemID.DRAGON_PICKAXE_12797,
				ItemID.INFERNAL_PICKAXE,
				ItemID.INFERNAL_PICKAXE_UNCHARGED,
				ItemID._3RD_AGE_PICKAXE,
				ItemID.GILDED_PICKAXE,
				ItemID.DRAGON_PICKAXE_OR,
				ItemID.CRYSTAL_PICKAXE,
				ItemID.CRYSTAL_PICKAXE_INACTIVE,
				ItemID.CORRUPTED_PICKAXE,
				ItemID.CRYSTAL_PICKAXE_23863,
				ItemID.INFERNAL_PICKAXE_OR,
				ItemID.TRAILBLAZER_PICKAXE,
				ItemID.INFERNAL_PICKAXE_UNCHARGED_25369,
				ItemID.DRAGON_PICKAXE_OR_25376
			)
	);

	private static AnyRequirementCollection createAnyRequirement(String name, List<Integer> itemIds)
	{
		return any(name, itemIds.stream().map(ItemRequirements::item).toArray(Item[]::new));
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged event)
	{
		final ItemContainer itemContainer = event.getItemContainer();
		if (event.getContainerId() == InventoryID.EQUIPMENT.getId())
		{
			equippedItems = itemContainer.getItems();
			updatePlayerItems();
		}
		else if (event.getContainerId() == InventoryID.INVENTORY.getId())
		{
			inventoryItems = itemContainer.getItems();
			updatePlayerItems();
		}
	}

	private void updatePlayerItems()
	{
		playerItems = new Item[equippedItems.length + inventoryItems.length];
		System.arraycopy(equippedItems, 0, playerItems, 0, equippedItems.length);
		System.arraycopy(inventoryItems, 0, playerItems, equippedItems.length, inventoryItems.length);
	}

	@Subscribe
	public void onPostMenuSort(PostMenuSort postMenuSort)
	{
		if (client.isMenuOpen())
		{
			return;
		}

		Menu root = client.getMenu();
		MenuEntry[] entries = root.getMenuEntries();

		for (MenuEntry entry : entries)
		{
			if (config.chopDown() && (entry.getOption().equals("Chop down") || entry.getOption().equals("Chop-down")) && !ANY_AXE.fulfilledBy(playerItems))
			{
				root.removeMenuEntry(entry);
			}
			else if (config.mine() && entry.getOption().equals("Mine") && !ANY_PICKAXE.fulfilledBy(playerItems))
			{
				root.removeMenuEntry(entry);
			}
		}
	}
}
