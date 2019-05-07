package net.jingles.backpacks;

import net.jingles.backpacks.persistence.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Backpack implements InventoryHolder {

  private final BackpacksMain plugin;
  private ItemStack itemStack;
  //private final UUID id;
  private BackpackType type;

  /* Creates a brand new backpack item if type is not null. Otherwise it creates a backpack
    instance for a pre-existing backpack item. */
  Backpack(BackpacksMain plugin, ItemStack item, BackpackType type) {
    this.plugin = plugin;
    this.itemStack = item;

    if (type != null) {

      this.type = type;
      ItemMeta meta = item.getItemMeta();
      PersistentDataContainer container = meta.getPersistentDataContainer();

      container.set(plugin.CONTENTS, PersistentDataTypes.ITEM_ARRAY,
              new ItemStack[]{new ItemStack(Material.AIR, 1)});
      container.set(plugin.TYPE, PersistentDataType.STRING, type.name());
      item.setItemMeta(meta);

    } else {
      this.type = BackpackType.valueOf(item.getItemMeta()
              .getPersistentDataContainer().get(plugin.TYPE, PersistentDataType.STRING));
    }

  }

  ItemStack getItemStack() {
    return this.itemStack;
  }

  BackpackType getType() {
    return this.type;
  }

  void setType(BackpackType type) {
    ItemMeta meta = getItemStack().getItemMeta();

    if (meta.getDisplayName().equalsIgnoreCase(getType().getName()))
      meta.setDisplayName(type.getName());

    meta.getPersistentDataContainer().set(plugin.TYPE, PersistentDataType.STRING, type.name());
    getItemStack().setItemMeta(meta);

    this.type = type;
  }

  @Override
  public Inventory getInventory() {
    Inventory inventory = Bukkit.createInventory(this, type.getSize(), type.getName());
    inventory.setStorageContents(getItemStack().getItemMeta()
            .getPersistentDataContainer().get(plugin.CONTENTS, PersistentDataTypes.ITEM_ARRAY));
    return inventory;
  }

  void saveInventory(ItemStack[] items) {
    ItemMeta meta = getItemStack().getItemMeta();
    meta.getPersistentDataContainer()
            .set(plugin.CONTENTS, PersistentDataTypes.ITEM_ARRAY, items);
    getItemStack().setItemMeta(meta);
  }

}
