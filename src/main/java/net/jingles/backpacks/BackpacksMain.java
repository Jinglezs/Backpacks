package net.jingles.backpacks;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class BackpacksMain extends JavaPlugin {

  public final NamespacedKey CONTENTS = new NamespacedKey(this, "contents");
  public final NamespacedKey TYPE = new NamespacedKey(this, "type");
  public final NamespacedKey ID = new NamespacedKey(this, "uuid");

  private final Set<Backpack> cached = new HashSet<>();

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new BackpackListener(this), this);

    //Add backpack recipes
    NamespacedKey lightKey = new NamespacedKey(this, "lightweight_backpack");
    ShapedRecipe lightweight = new ShapedRecipe(lightKey, new Backpack(this, BackpackType.LIGHTWEIGHT).getItemStack());
    lightweight.shape("LLL", "LCL", "LLL");
    lightweight.setIngredient('L', Material.LEATHER);
    lightweight.setIngredient('C', Material.CHEST);

    NamespacedKey heavyKey = new NamespacedKey(this, "heavy_backpack");
    ShapedRecipe heavy = new ShapedRecipe(heavyKey, new Backpack(this, BackpackType.HEAVY).getItemStack());
    heavy.shape("GGG", "GHG", "GGG");
    heavy.setIngredient('G', Material.GOLD_INGOT);
    heavy.setIngredient('H', Material.MOJANG_BANNER_PATTERN); //backpack material

    NamespacedKey colossalKey = new NamespacedKey(this, "colossal_backpack");
    ShapedRecipe colossal = new ShapedRecipe(colossalKey, new Backpack(this, BackpackType.COLOSSAL).getItemStack());
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

  public Set<Backpack> getCachedBackpacks() {
    return this.cached;
  }

}
