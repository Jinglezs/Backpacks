package net.jingles.backpacks;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class BackpacksMain extends JavaPlugin {

  public static NamespacedKey CONTENTS;
  public static NamespacedKey TYPE;

  @Override
  public void onEnable() {

    CONTENTS = new NamespacedKey(this, "contents");
    TYPE = new NamespacedKey(this, "type");

    getServer().getPluginManager().registerEvents(new BackpackListener(this), this);
    createRecipes();
  }

  public ItemStack backpackItem(BackpackType type) {
    ItemStack backpack = new ItemStack(Material.MOJANG_BANNER_PATTERN, 1);
    ItemMeta meta = backpack.getItemMeta();
    meta.setDisplayName(type.getName());
    meta.getPersistentDataContainer().set(TYPE, PersistentDataType.STRING, type.name());
    backpack.setItemMeta(meta);

    return backpack;
  }

  private void createRecipes() {
    // Add backpack recipes
    NamespacedKey lightKey = new NamespacedKey(this, "lightweight_backpack");
    ShapedRecipe lightweight = new ShapedRecipe(lightKey, backpackItem(BackpackType.LIGHTWEIGHT));
    lightweight.shape("LLL", "LCL", "LLL");
    lightweight.setIngredient('L', Material.LEATHER);
    lightweight.setIngredient('C', Material.CHEST);

    NamespacedKey heavyKey = new NamespacedKey(this, "heavy_backpack");
    ShapedRecipe heavy = new ShapedRecipe(heavyKey, backpackItem(BackpackType.HEAVY));
    heavy.shape("GGG", "GHG", "GGG");
    heavy.setIngredient('G', Material.GOLD_INGOT);
    heavy.setIngredient('H', Material.MOJANG_BANNER_PATTERN); // backpack material

    NamespacedKey colossalKey = new NamespacedKey(this, "colossal_backpack");
    ShapedRecipe colossal = new ShapedRecipe(colossalKey, backpackItem(BackpackType.COLOSSAL));
    colossal.shape("EEE", "EHE", "EEE");
    colossal.setIngredient('E', Material.EMERALD);
    colossal.setIngredient('H', Material.MOJANG_BANNER_PATTERN); // backpack material

    NamespacedKey resourceKey = new NamespacedKey(this, "resource_backpack");
    ShapedRecipe resource = new ShapedRecipe(resourceKey, backpackItem(BackpackType.RESOURCE));
    resource.shape("LPL", "LHL", "LSL");
    resource.setIngredient('L', Material.LEATHER);
    resource.setIngredient('P', Material.IRON_PICKAXE);
    resource.setIngredient('S', Material.IRON_SHOVEL);
    resource.setIngredient('H', Material.MOJANG_BANNER_PATTERN); // backpack material

    try {
      getServer().addRecipe(lightweight);
      getServer().addRecipe(heavy);
      getServer().addRecipe(colossal);
      getServer().addRecipe(resource);
    } catch (IllegalStateException e) { // Thrown when a duplicate recipe is added
      getServer().getConsoleSender().sendMessage("Backpack recipes already loaded... skipping!");
    }

  }

}
