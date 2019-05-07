package net.jingles.backpacks;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class BackpacksMain extends JavaPlugin {

  public final NamespacedKey CONTENTS = new NamespacedKey(this, "contents");
  public final NamespacedKey TYPE = new NamespacedKey(this, "type");

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new BackpackListener(this), this);

    //Add backpack recipes
    NamespacedKey lightKey = new NamespacedKey(this, "lightweight_backpack");
    ShapedRecipe lightweight = new ShapedRecipe(lightKey, backpackItem(BackpackType.LIGHTWEIGHT));
    lightweight.shape("LLL", "LCL", "LLL");
    lightweight.setIngredient('L', Material.LEATHER);
    lightweight.setIngredient('C', Material.CHEST);

    NamespacedKey heavyKey = new NamespacedKey(this, "heavy_backpack");
    ShapedRecipe heavy = new ShapedRecipe(heavyKey, backpackItem(BackpackType.HEAVY));
    heavy.shape("GGG", "GHG", "GGG");
    heavy.setIngredient('G', Material.GOLD_INGOT);
    heavy.setIngredient('H', Material.MOJANG_BANNER_PATTERN); //backpack material

    NamespacedKey colossalKey = new NamespacedKey(this, "colossal_backpack");
    ShapedRecipe colossal = new ShapedRecipe(colossalKey, backpackItem(BackpackType.COLOSSAL));
    colossal.shape("EEE", "EHE", "EEE");
    colossal.setIngredient('E', Material.EMERALD);
    colossal.setIngredient('H', Material.MOJANG_BANNER_PATTERN);

    try {
      getServer().addRecipe(lightweight);
      getServer().addRecipe(heavy);
      getServer().addRecipe(colossal);
    } catch (IllegalStateException e) { //Thrown when a duplicate recipe is added
      getServer().getConsoleSender().sendMessage("Backpack recipes already loaded... skipping!");
    }

  }

  public ItemStack backpackItem(BackpackType type) {
    ItemStack backpack = new ItemStack(Material.MOJANG_BANNER_PATTERN, 1);
    ItemMeta meta = backpack.getItemMeta();
    meta.setDisplayName(type.getName());
    meta.getPersistentDataContainer().set(TYPE, PersistentDataType.STRING, type.name());
    backpack.setItemMeta(meta);

    return backpack;
  }

}
