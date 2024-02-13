package me.tahacheji.mafana.tools;

import me.tahacheji.mafana.MafanaNetwork;
import me.tahacheji.mafana.itemData.GameItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Undor3000 extends GameItem {
    public Undor3000() {
        super(ChatColor.DARK_PURPLE + "Undor3000!", Material.SUGAR, true, ChatColor.GRAY + "Right click to undo latest!");
        MafanaNetwork.getInstance().getGameItems().add(this);
    }

    @Override
    public boolean rightClickAirAction(Player var1, ItemStack var2) {
        Bukkit.dispatchCommand(var1, "mwp undoBuild 1 1");
        Bukkit.dispatchCommand(var1, "mwp undoPlace");
        Bukkit.dispatchCommand(var1, "mwp undoReplace");
        return true;
    }
}
