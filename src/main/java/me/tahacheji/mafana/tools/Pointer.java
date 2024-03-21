package me.tahacheji.mafana.tools;

import me.tahacheji.mafana.MafanaNetwork;
import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.data.PlayerEditor;
import me.tahacheji.mafana.itemData.GameItem;
import me.tahacheji.mafana.util.GameItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class Pointer extends GameItem {

    public Pointer() {
        super("POINTER", ChatColor.GOLD + "Pointer", Material.STICK, true, ChatColor.GRAY + "Left Click To Set Location 1", ChatColor.GOLD + "L1: ", ChatColor.GRAY + "Right Click To Set Location 2", ChatColor.GOLD + "L2: ");
        MafanaNetwork.getInstance().getGameItems().add(this);
    }

    @Override
    public boolean rightClickBlockAction(Player var1, PlayerInteractEvent var2, Block var3, ItemStack var4) {
        PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(var1);
        if(!MafanaWorldProcessor.getInstance().isInCoolDown(var1)) {
            if (playerEditor != null) {
                if (var2.getClickedBlock() != null) {
                    Component cm = getMessage(var1, var3, var1.getItemInHand(), "L2", false);
                    var1.sendMessage(cm);
                    playerEditor.setPoint2(var2.getClickedBlock().getLocation());
                    MafanaWorldProcessor.getInstance().addToCoolDown(var1, 1);
                }
            }
            var2.setCancelled(true);
        }
        return true;
    }

    @Override
    public boolean shiftRightClickBlockAction(Player var1, PlayerInteractEvent var2, Block var3, ItemStack var4) {
        PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(var1);
        if(!MafanaWorldProcessor.getInstance().isInCoolDown(var1)) {
            if (playerEditor != null) {
                if (var2.getClickedBlock() != null) {
                    Component cm = getMessage(var1, var3, var1.getItemInHand(), "L2", true);
                    var1.sendMessage(cm);
                    playerEditor.setPoint2(var2.getClickedBlock().getLocation());
                    MafanaWorldProcessor.getInstance().addToCoolDown(var1, 1);
                }
            }
            var2.setCancelled(true);
        }
        return true;
    }

    @Override
    public boolean leftClickBlockAction(Player var1, PlayerInteractEvent var2, Block var3, ItemStack var4) {
        PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(var1);
        if(!MafanaWorldProcessor.getInstance().isInCoolDown(var1)) {
            if (playerEditor != null) {
                if (var2.getClickedBlock() != null) {
                    Component cm = getMessage(var1, var3, var1.getItemInHand(), "L1", false);
                    var1.sendMessage(cm);
                    playerEditor.setPoint1(var2.getClickedBlock().getLocation());
                    MafanaWorldProcessor.getInstance().addToCoolDown(var1, 1);
                }
            }
        }
        var2.setCancelled(true);
        return true;
    }

    @Override
    public boolean shiftLeftClickBlockAction(Player var1, PlayerInteractEvent var2, Block var3, ItemStack var4) {
        PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(var1);
        if(!MafanaWorldProcessor.getInstance().isInCoolDown(var1)) {
            if (playerEditor != null) {
                if (var2.getClickedBlock() != null) {
                    Component cm = getMessage(var1, var3, var1.getItemInHand(), "L1", true);
                    var1.sendMessage(cm);
                    playerEditor.setPoint1(var2.getClickedBlock().getLocation());
                    MafanaWorldProcessor.getInstance().addToCoolDown(var1, 1);
                }
            }
        }
        var2.setCancelled(true);
        return true;
    }

    @NotNull
    private Component getMessage(Player player, Block var3, ItemStack itemStack, String locationName, boolean code) {
        if(itemStack != null) {
            List<String> lore = itemStack.getLore();
            GameItem old = new GameItemUtil().getGameItem(itemStack);
            GameItem pointer = old.createNewInstance();
            MafanaNetwork.getInstance().getGameItems().remove(old);
            if(locationName.equalsIgnoreCase("L1")) {
                lore.set(1, ChatColor.GOLD + "L1: " + var3.getX() + ", " + var3.getY() + ", " + var3.getZ());
            } else if (locationName.equalsIgnoreCase("L2")) {
                lore.set(3, ChatColor.GOLD + "L2: " + var3.getX() + ", " + var3.getY() + ", " + var3.getZ());
            }
            ItemStack i = pointer.getItem();
            ItemMeta itemMeta = i.getItemMeta();
            itemMeta.setLore(lore);
            i.setItemMeta(itemMeta);
            player.setItemInHand(i);
        }
        String message;
        if(code) {
            message = "new Location(Bukkit.getWorld("+ player.getWorld().getName() + "), " + var3.getX() + ", " + var3.getY() + ", " +
                    var3.getZ() + ", (float) " + player.getLocation().getYaw() + ", (float) " + player.getLocation().getPitch() + ");";
        } else {
            message = ChatColor.WHITE + "" + var3.getLocation().getX() + "," + var3.getLocation().getY() + "," + var3.getLocation().getZ() + "," + player.getLocation().getYaw() + "," + player.getLocation().getPitch();
        }
        return Component.text(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "MWP" + ChatColor.DARK_GRAY + "] " + locationName + ": " + ChatColor.WHITE + message)
                .clickEvent(ClickEvent.copyToClipboard(message))
                .color(NamedTextColor.GOLD);
    }

    @Override
    public boolean breakBlockAction(Player var1, BlockBreakEvent var2, Block var3, ItemStack var4) {
        var2.setCancelled(true);
        return true;
    }
}
