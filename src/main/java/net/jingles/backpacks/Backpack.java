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

import java.util.UUID;

public class Backpack implements InventoryHolder {

  private final BackpacksMain plugin;
  private final ItemStack itemStack;
  private final BackpackType type;
  private final UUID id;

  //Used to create a backpack instance from an existing backpack item
  Backpack(BackpacksMain plugin, ItemStack item) {
    this.plugin = plugin;
    this.itemStack = item;
    this.type = BackpackType.valueOf(item.getItemMeta()
            .getPersistentDataContainer().get(plugin.TYPE, PersistentDataType.STRING));
    this.id = item.getItemMeta().getPersistentDataContainer().get(plugin.ID, PersistentDataTypes.UUID);

    plugin.getCachedBackpacks().add(this);
  }

  //Used when creating an empty Backpack item
  Backpack(BackpacksMain plugin, BackpackType type) {
    this.plugin = plugin;
    this.type = type;
    this.id = UUID.randomUUID();

    ItemStack backpack = new ItemStack(Material.MOJANG_BANNER_PATTERN, 1);
    ItemMeta meta = backpack.getItemMeta();
    meta.setDisplayName(type.getName());

    PersistentDataContainer container = meta.getPersistentDataContainer();

    container.set(plugin.CONTENTS, PersistentDataTypes.ITEM_ARRAY,
            new ItemStack[]{new ItemStack(Material.AIR, 1)});
    container.set(plugin.TYPE, PersistentDataType.STRING, type.name());
    container.set(plugin.ID, PersistentDataTypes.UUID, id);
    backpack.setItemMeta(meta);

    this.itemStack = backpack;
    plugin.getCachedBackpacks().add(this);
  }

  ItemStack getItemStack() {
    return this.itemStack;
  }

  BackpackType getType() {
    return this.type;
  }

  UUID getUUID() {
    return this.id;
  }

  @Override
  public Inventory getInventory() {
    Inventory inventory = Bukkit.createInventory(this, type.getSize(), type.getName());
    inventory.setStorageContents(getItemStack().getItemMeta()
            .getPersistentDataContainer().get(plugin.CONTENTS, PersistentDataTypes.ITEM_ARRAY));
    return inventory;
  }

  void saveInventory(Inventory inventory) {
    ItemMeta meta = getItemStack().getItemMeta();
    meta.getPersistentDataContainer()
            .set(plugin.CONTENTS, PersistentDataTypes.ITEM_ARRAY, inventory.getStorageContents());
    getItemStack().setItemMeta(meta);
  }

}
