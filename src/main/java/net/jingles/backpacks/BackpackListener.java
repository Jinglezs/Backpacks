package net.jingles.backpacks;

import net.jingles.backpacks.persistence.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class BackpackListener implements Listener {

  private final BackpacksMain plugin;

  BackpackListener(BackpacksMain plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onBackpackOpen(PlayerInteractEvent event) {
    if (event.getItem() == null || isBackpack(event.getItem()) < 1) return;

    Backpack backpack = getBackpack(event.getItem());
    event.getPlayer().openInventory(backpack.getInventory());
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    if (!(event.getInventory().getHolder() instanceof Backpack)) return;

    Backpack backpack = (Backpack) event.getInventory().getHolder();
    backpack.saveInventory(event.getInventory().getStorageContents());
  }

  @EventHandler
  public void onBackpackEdit(InventoryClickEvent event) {
    if (event.getCurrentItem() == null) return;

    if (!(event.getInventory().getHolder() instanceof Backpack)) return;

    //Prevent illegal blocks from being added to the backpack due to storage constraints
    if (isBackpack(event.getCurrentItem()) != -1 || event.getCurrentItem().getType() == Material.SHULKER_BOX)
      event.setCancelled(true);

  }

  @EventHandler
  public void onPrepareItemCraft(PrepareItemCraftEvent event) {

    Recipe recipe = event.getRecipe();
    if (recipe == null) return;

    ItemStack result = event.getRecipe().getResult();
    if (isBackpack(result) != 0) return;

    BackpackType type = getTypeFromItem(result);
    if (type == BackpackType.LIGHTWEIGHT) return;

    List<ItemStack> matrix = Arrays.asList(event.getInventory().getMatrix());
    Backpack oldBackpack = getBackpack(matrix.get(4));

    //Checks to make sure that the correct backpack type is used in the recipe
    if (type == BackpackType.HEAVY) {

      if (oldBackpack.getType() != BackpackType.LIGHTWEIGHT)
        event.getInventory().setResult(null);

    } else if (type == BackpackType.COLOSSAL) {

      if (oldBackpack.getType() != BackpackType.HEAVY)
        event.getInventory().setResult(null);

    }

  }

  @EventHandler
  public void onAdvancedBackpackCraft(CraftItemEvent event) {

    ItemStack result = event.getInventory().getResult();
    if (result == null || isBackpack(result) == -1) return;

    List<ItemStack> matrix = Arrays.asList(event.getInventory().getMatrix());
    Backpack backpack = new Backpack(plugin, result, getTypeFromItem(result));

    if (isBackpack(matrix.get(4)) != 1) return;

    Backpack oldBackpack = getBackpack(matrix.get(4));
    backpack.saveInventory(oldBackpack.getInventory().getStorageContents());
  }

  private Backpack getBackpack(ItemStack itemstack) {
    return new Backpack(plugin, itemstack, null);
  }

  private int isBackpack(ItemStack item) {
    if (item == null || !item.hasItemMeta() || item.getType() != Material.MOJANG_BANNER_PATTERN) return -1;
    PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
    if (container.has(plugin.TYPE, PersistentDataType.STRING) &&
            !container.has(plugin.CONTENTS, PersistentDataTypes.ITEM_ARRAY)) return 0;
    else return 1;
  }

  private BackpackType getTypeFromItem(ItemStack item) {
    return BackpackType.valueOf(item.getItemMeta()
            .getPersistentDataContainer().get(plugin.TYPE, PersistentDataType.STRING));
  }

}
