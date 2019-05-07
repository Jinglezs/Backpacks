package net.jingles.backpacks.persistence;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public interface PersistentDataTypes {

  PersistentDataType<byte[], ItemStack[]> ITEM_ARRAY = new ItemArrayDataType();

}
