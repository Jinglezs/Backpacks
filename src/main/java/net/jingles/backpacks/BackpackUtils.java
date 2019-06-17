package net.jingles.backpacks;

import net.jingles.backpacks.persistence.PersistentDataTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BackpackUtils {

  /**
   * Gets a Backpack instance from the provided item stack.
   * NOTE: Must confirm the item is a backpack before using.
   * @param itemstack
   * @return
   */
  public static Backpack getBackpack(ItemStack itemstack) {
    return new Backpack(BackpacksMain.getPlugin(BackpacksMain.class), itemstack, null);
  }

  /**
   * Determines whether or not the given item is an eligible backpack.
   * @param item the item in question
   * @return -1 if the item is not a Backpack at all, 0 if it matches a backpack item, but
   * does not have an inventory saved to it, and 1 if it has a saved inventory.
   */
  public static int isBackpack(ItemStack item) {
    if (item == null || !item.hasItemMeta() || item.getType() != Material.MOJANG_BANNER_PATTERN) return -1;
    PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
    if (container.has(BackpacksMain.TYPE, PersistentDataType.STRING) &&
        !container.has(BackpacksMain.CONTENTS, PersistentDataTypes.ITEM_ARRAY)) return 0;
    else return 1;
  }

  /**
   * Gets the BackpackType from a Backpack item stack.
   * NOTE: You must check if the item is a backpack before using this method.
   * @param item the backpack item
   * @return the type of backpack
   */
  public static BackpackType getTypeFromItem(ItemStack item) {
    return BackpackType.valueOf(item.getItemMeta()
        .getPersistentDataContainer().get(BackpacksMain.TYPE, PersistentDataType.STRING));
  }

  /**
   * Determines whether or not an inventory contains a Backpack item.
   * @param inventory the inventory to search
   * @return true if contains a backpack
   */
  public static boolean hasBackpack(Inventory inventory) {
    return Stream.of(inventory.getContents())
        .anyMatch(item -> isBackpack(item) != -1);
  }

  /**
   * Gets all Backpacks of the Resource type that aren't full.
   * @param inventory the inventory to search through
   * @return a list of Resource Backpacks that items can be added to.
   */
  public static List<Backpack> getResourceBackpacks(Inventory inventory) {
    return Stream.of(inventory.getContents())
        .filter(item -> BackpackUtils.isBackpack(item) != -1)
        .map(BackpackUtils::getBackpack)
        .filter(backpack -> backpack.getType() == BackpackType.RESOURCE)
        .filter(backpack -> backpack.getInventory().firstEmpty() != -1)
        .collect(Collectors.toList());
  }

  /**
   * Attempts to add the given item to the provided backpacks or inventory.
   * @param item the item to add
   * @param inventory the inventory to search for backpacks and to add the item to if
   *                  it could not be added to the backpacks.
   * @param location the location to drop the item if the item could not be added anywhere.
   */
  public static void addToResourceBackpacks(ItemStack item, Inventory inventory, Location location) {

    List<Backpack> backpacks = getResourceBackpacks(inventory);

    for (Backpack backpack : backpacks) {
      Inventory backpackInv = backpack.getInventory();
      // Try to add the items
      Map<Integer, ItemStack> leftover = backpackInv.addItem(item);
      // Save the items they were successfully added.
      backpack.saveInventory(backpackInv.getContents());
      // If all items were successfully added, end method execution.
      if (leftover.isEmpty()) {
        location.getWorld().playSound(location, Sound.ENTITY_ITEM_PICKUP, 1, 1);
        return;
      }
      // Set the item amount to the leftover amount
      item.setAmount(leftover.values().stream().findFirst().get().getAmount());
    }

    // If there are still leftovers, try to add them to the player's inventory.
    Map<Integer, ItemStack> leftover = inventory.addItem(item);
    // Drop the leftovers that couldn't fit anywhere.
    if (!leftover.isEmpty()) {
      item.setAmount(leftover.values().stream().findFirst().get().getAmount());
      location.getWorld().dropItemNaturally(location, item);
    } else location.getWorld().playSound(location, Sound.ENTITY_ITEM_PICKUP, 1, 1);

  }

}
