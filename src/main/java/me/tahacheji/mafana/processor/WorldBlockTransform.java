package me.tahacheji.mafana.processor;

import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.data.PlayerEditor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorldBlockTransform {

    private List<WorldBlock> worldBlocks = new ArrayList<>();
    private int batchSize;
    private long delayBetweenBatches;

    private int translateX;
    private int translateY;
    private int translateZ;

    private double rotateX;
    private double rotateY;
    private double rotateZ;


    public WorldBlockTransform(int batchSize, long delayBetweenBatches, int translateX, int translateY, int translateZ, double rotateX, double rotateY, double rotateZ) {
        this.batchSize = batchSize;
        this.delayBetweenBatches = delayBetweenBatches;
        this.translateX = translateX;
        this.translateY = translateY;
        this.translateZ = translateZ;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
    }

    public WorldBlockTransform(List<WorldBlock> worldBlocks, int batchSize, long delayBetweenBatches, int translateX, int translateY, int translateZ, double rotateX, double rotateY, double rotateZ) {
        this.worldBlocks = worldBlocks;
        this.batchSize = batchSize;
        this.delayBetweenBatches = delayBetweenBatches;
        this.translateX = translateX;
        this.translateY = translateY;
        this.translateZ = translateZ;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        this.rotateZ = rotateZ;
    }

    public CompletableFuture<Void> translate(Player player, String world) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        for (WorldBlock worldBlock : worldBlocks) {
            if(new Location(Bukkit.getWorld(worldBlock.getWorld()), worldBlock.getX(), worldBlock.getY(), worldBlock.getZ()).getBlock().getType() == worldBlock.getMaterial()) {
                new Location(Bukkit.getWorld(worldBlock.getWorld()), worldBlock.getX(), worldBlock.getY(), worldBlock.getZ()).getBlock().setType(Material.AIR);
            }
        }
        WorldBlockSetter w = new WorldBlockSetter(worldBlocks, new ArrayList<>(), "x", "x",
                new Build(worldBlocks).getBottomMidPoint().add(translateX, translateY, translateZ), batchSize, delayBetweenBatches);
        w.setTransparent(true);
        w.setPacket(false);
        w.setRotateX(0);
        w.setRotateY(0);
        w.setRotateZ(0);
        w.setWorld(world);

        CompletableFuture<HashMap<Build, Build>> d = w.placeBlockAtLocation();
        d.thenAcceptAsync(x -> {
            worldBlocks.clear();
            for (Build z : x.keySet()) {
                worldBlocks.addAll(z.getBlockList());
            }
            future.complete(null);
        });
        PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(player);
        if (playerEditor != null) {
            playerEditor.addWorldBlockSetter(w);
        }

        return future;
    }


    public CompletableFuture<Void> rotate(Player player, String world) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        for (WorldBlock worldBlock : worldBlocks) {
            if(new Location(Bukkit.getWorld(worldBlock.getWorld()), worldBlock.getX(), worldBlock.getY(), worldBlock.getZ()).getBlock().getType() == worldBlock.getMaterial()) {
                new Location(Bukkit.getWorld(worldBlock.getWorld()), worldBlock.getX(), worldBlock.getY(), worldBlock.getZ()).getBlock().setType(Material.AIR);
            }
        }
        WorldBlockSetter w = new WorldBlockSetter(worldBlocks, new ArrayList<>(), "x", "x",
                new Build(worldBlocks).getBottomMidPoint(), batchSize, delayBetweenBatches);
        w.setTransparent(true);
        w.setPacket(false);
        w.setRotateX(rotateX);
        w.setRotateY(rotateY);
        w.setRotateZ(rotateZ);
        w.setWorld(world);

        CompletableFuture<HashMap<Build, Build>> d = w.placeBlockAtLocation();
        d.thenAcceptAsync(x -> {
            worldBlocks.clear();
            for (Build z : x.keySet()) {
                worldBlocks.addAll(z.getBlockList());
            }
            future.complete(null);
        });
        PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(player);
        if (playerEditor != null) {
            playerEditor.addWorldBlockSetter(w);
        }

        return future;
    }



    public void setWorldBlocks(List<WorldBlock> worldBlocks) {
        this.worldBlocks = worldBlocks;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setDelayBetweenBatches(long delayBetweenBatches) {
        this.delayBetweenBatches = delayBetweenBatches;
    }

    public void setTranslateX(int translateX) {
        this.translateX = translateX;
    }

    public void setTranslateY(int translateY) {
        this.translateY = translateY;
    }

    public void setTranslateZ(int translateZ) {
        this.translateZ = translateZ;
    }

    public void setRotateX(double rotateX) {
        this.rotateX = rotateX;
    }

    public void setRotateY(double rotateY) {
        this.rotateY = rotateY;
    }

    public void setRotateZ(double rotateZ) {
        this.rotateZ = rotateZ;
    }

    public List<WorldBlock> getWorldBlocks() {
        return worldBlocks;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public long getDelayBetweenBatches() {
        return delayBetweenBatches;
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
