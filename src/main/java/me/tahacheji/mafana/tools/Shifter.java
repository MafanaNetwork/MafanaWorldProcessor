package me.tahacheji.mafana.tools;

import me.tahacheji.mafana.MafanaNetwork;
import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.data.PlayerEditor;
import me.tahacheji.mafana.itemData.GameItem;
import me.tahacheji.mafana.processor.WorldBlockTransform;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Shifter extends GameItem {

    private final int translateX;
    private final int translateY;
    private final int translateZ;

    private final double rotateX;
    private final double rotateY;
    private final double rotateZ;

    private final int batchSize;
    private final long delayBetweenBatches;

    public Shifter(int tX, int tY, int tZ, double rX, double rY, double rZ, int bS, long dBB) {
        super(ChatColor.GREEN + "Shifter", Material.RABBIT_HIDE, true,
                ChatColor.GOLD + "Translate X: " + ChatColor.WHITE + tX,
                ChatColor.GOLD + "Translate Y: " + ChatColor.WHITE + tY,
                ChatColor.GOLD + "Translate Z: " + ChatColor.WHITE + tZ,
                "",
                ChatColor.GOLD + "Rotate X: " + ChatColor.WHITE + rX,
                ChatColor.GOLD + "Rotate Y: " + ChatColor.WHITE + rY,
                ChatColor.GOLD + "Rotate Z: " + ChatColor.WHITE + rZ,
                "",
                ChatColor.GOLD + "Batch Size: " + ChatColor.WHITE + bS,
                ChatColor.GOLD + "DelayBetweenBatches: " + ChatColor.WHITE + dBB,
                "",
                ChatColor.GRAY + "Right Click To Translate.",
                ChatColor.GRAY + "Left Click To Rotate.");

        this.translateX = tX;
        this.translateY = tY;
        this.translateZ = tZ;

        this.rotateX = rX;
        this.rotateY = rY;
        this.rotateZ = rZ;

        this.batchSize = bS;
        this.delayBetweenBatches = dBB;
        MafanaNetwork.getInstance().getGameItems().add(this);
    }

    @Override
    public boolean rightClickAirAction(Player var1, ItemStack var2) {
        PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(var1);
        if (playerEditor != null) {
            WorldBlockTransform worldBlockTransform = playerEditor.getWorldBlockTransform();
            if (worldBlockTransform != null) {
                if (!worldBlockTransform.getWorldBlocks().isEmpty()) {
                    worldBlockTransform.setTranslateX(translateX);
                    worldBlockTransform.setTranslateY(translateY);
                    worldBlockTransform.setTranslateZ(translateZ);

                    worldBlockTransform.setBatchSize(batchSize);
                    worldBlockTransform.setDelayBetweenBatches(delayBetweenBatches);

                    CompletableFuture<Void> moveFuture = worldBlockTransform.translate(var1, "world");
                    moveFuture.thenRun(() -> {
                        Bukkit.getScheduler().runTask(MafanaWorldProcessor.getInstance(), () -> {
                            var1.sendMessage(ChatColor.GREEN + "Blocks moved successfully!");
                        });
                    }).exceptionally(ex -> {

                        Bukkit.getScheduler().runTask(MafanaWorldProcessor.getInstance(), () -> {
                            var1.sendMessage(ChatColor.RED + "Failed to move blocks: " + ex.getMessage());
                        });
                        return null;
                    });
                }
            }
        }
        return true;
    }

    @Override
    public boolean leftClickAirAction(Player var1, ItemStack var2) {
        PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(var1);
        if (playerEditor != null) {
            WorldBlockTransform worldBlockTransform = playerEditor.getWorldBlockTransform();
            if (worldBlockTransform != null) {
                if (!worldBlockTransform.getWorldBlocks().isEmpty()) {
                    worldBlockTransform.setRotateX(rotateX);
                    worldBlockTransform.setRotateY(rotateY);
                    worldBlockTransform.setRotateZ(rotateZ);

                    worldBlockTransform.setBatchSize(batchSize);
                    worldBlockTransform.setDelayBetweenBatches(delayBetweenBatches);

                    CompletableFuture<Void> moveFuture = worldBlockTransform.rotate(var1, "world");
                    moveFuture.thenRun(() -> {
                        Bukkit.getScheduler().runTask(MafanaWorldProcessor.getInstance(), () -> {
                            var1.sendMessage(ChatColor.GREEN + "Blocks moved successfully!");
                        });
                    }).exceptionally(ex -> {

                        Bukkit.getScheduler().runTask(MafanaWorldProcessor.getInstance(), () -> {
                            var1.sendMessage(ChatColor.RED + "Failed to move blocks: " + ex.getMessage());
                        });
                        return null;
                    });
                }
            }
        }
        return true;
    }



    @Override
    public boolean breakBlockAction(Player var1, BlockBreakEvent var2, Block var3, ItemStack var4) {
        var2.setCancelled(true);
        return true;
    }

    public int getTranslateX() {
        return translateX;
    }

    public int getTranslateY() {
        return translateY;
    }

    public int getTranslateZ() {
        return translateZ;
    }

    public double getRotateX() {
        return rotateX;
    }

    public double getRotateY() {
        return rotateY;
    }

    public double getRotateZ() {
        return rotateZ;
    }
}
