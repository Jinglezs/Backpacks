package net.jingles.backpacks;

import net.jingles.backpacks.persistence.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BackpackListener implements Listener {

  private final BackpacksMain plugin;

  BackpackListener(BackpacksMain plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onBackpackOpen(PlayerInteractEvent event) {
    if (event.getItem() == null || !isBackpack(event.getItem())) return;

    Backpack backpack = getBackpack(event.getItem());
    event.getPlayer().openInventory(backpack.getInventory());
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    if (!(event.getInventory().getHolder() instanceof Backpack)) return;

    Backpack backpack = (Backpack) event.getInventory().getHolder();
    backpack.saveInventory(event.getInventory());
  }

  @EventHandler
  public void onBackpackEdit(InventoryClickEvent event) {
    if (!(event.getInventory().getHolder() instanceof Backpack)) return;
    if (event.getCurrentItem() == null) return;

    //Prevent illegal blocks from being added to the backpack due to storage constraints
    if (isBackpack(event.getCurrentItem()) || event.getCurrentItem().getType() == Material.SHULKER_BOX)
      event.setCancelled(true);
  }

  @EventHandler
  public void onPrepareItemCraft(PrepareItemCraftEvent event) {

    Recipe recipe = event.getRecipe();
    if (recipe == null) return;

    ItemStack result = event.getRecipe().getResult();
    if (!isBackpack(result)) return;

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
    if (!isBackpack(event.getCurrentItem())) return;

    Backpack backpack = getBackpack(event.getInventory().getResult());
    if (backpack.getType() == BackpackType.LIGHTWEIGHT) return;

    List<ItemStack> matrix = Arrays.asList(event.getInventory().getMatrix());
    Backpack oldBackpack = getBackpack(matrix.get(4));

    //Transfers the old backpack contents to the new backpack
    backpack.saveInventory(oldBackpack.getInventory());
    //The old backpack will be consumed when the new one is crafted, so it can be removed from the cache.
    plugin.getCachedBackpacks().remove(oldBackpack);
  }

  @EventHandler
  public void removeBackpackCacheOnDisconnect(PlayerQuitEvent event) {
    List<Backpack> backpacks = Stream.of(event.getPlayer().getInventory().getStorageContents())
            .filter(this::isBackpack).map(this::getBackpack)
            .collect(Collectors.toList());

    plugin.getCachedBackpacks().removeAll(backpacks);
  }

  private Backpack getBackpack(ItemStack itemStack) {
    return plugin.getCachedBackpacks().stream()
            .filter(pack -> pack.getUUID().equals(getUUIDFromItem(itemStack)))
            .findAny().orElseGet(() -> new Backpack(plugin, itemStack));
  }

  private boolean isBackpack(ItemStack item) {
    return item != null && item.hasItemMeta() &&
            item.getItemMeta().getPersistentDataContainer().has(plugin.TYPE, PersistentDataType.STRING);
  }

  private BackpackType getTypeFromItem(ItemStack item) {
    return BackpackType.valueOf(item.getItemMeta()
            .getPersistentDataContainer().get(plugin.TYPE, PersistentDataType.STRING));
  }

  private UUID getUUIDFromItem(ItemStack item) {
    return item.getItemMeta().getPersistentDataContainer().get(plugin.ID, PersistentDataTypes.UUID);
  }

}
