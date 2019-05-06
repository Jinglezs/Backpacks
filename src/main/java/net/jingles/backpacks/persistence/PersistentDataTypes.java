package net.jingles.backpacks.persistence;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public interface PersistentDataTypes {

  PersistentDataType<byte[], ItemStack[]> ITEM_ARRAY = new ItemArrayDataType();

  PersistentDataType<byte[], UUID> UUID = new UUIDDataType();

}
