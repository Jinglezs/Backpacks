package net.jingles.backpacks.persistence;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ItemArrayDataType implements PersistentDataType<byte[], ItemStack[]> {

  @Override
  public Class<byte[]> getPrimitiveType() {
    return byte[].class;
  }

  @Override
  public Class<ItemStack[]> getComplexType() {
    return ItemStack[].class;
  }

  @Override
  public byte[] toPrimitive(ItemStack[] items, PersistentDataAdapterContext persistentDataAdapterContext) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

      // Write the size of the inventory
      dataOutput.writeInt(items.length);

      // Save every element in the list
      for (ItemStack item : items) {
        dataOutput.writeObject(item);
      }

      // Serialize that array
      dataOutput.close();
      return outputStream.toByteArray();

    } catch (Exception e) {
      throw new IllegalStateException("Unable to save item stacks.", e);
    }
  }

  @Override
  public ItemStack[] fromPrimitive(byte[] bytes, PersistentDataAdapterContext persistentDataAdapterContext) {
    try {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
      BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
      ItemStack[] items = new ItemStack[dataInput.readInt()];

      for (int i = 0; i < items.length; i++) {
        items[i] = (ItemStack) dataInput.readObject();
      }

      dataInput.close();
      return items;
    } catch (ClassNotFoundException | IOException e) {
      throw new IllegalStateException("Unable to deserialize item stacks.", e);
    }
  }
}
