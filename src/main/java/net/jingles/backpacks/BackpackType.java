package net.jingles.backpacks;

import org.bukkit.ChatColor;

public enum BackpackType {

  LIGHTWEIGHT(18, ChatColor.LIGHT_PURPLE + "Lightweight Backpack"),
  HEAVY(36, ChatColor.LIGHT_PURPLE + "Heavy Backpack"),
  COLOSSAL(54, ChatColor.LIGHT_PURPLE + "Hoeish Backpack");

  private int size;
  private String name;

  BackpackType(int size, String name) {
    this.size = size;
    this.name = name;
  }

  public int getSize() {
    return this.size;
  }

  public String getName() {
    return this.name;
  }

}
