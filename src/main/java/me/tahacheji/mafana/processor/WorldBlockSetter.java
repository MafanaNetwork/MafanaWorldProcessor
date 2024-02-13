package me.tahacheji.mafana.processor;

import me.tahacheji.mafana.MafanaWorldProcessor;

import me.tahacheji.mafana.event.WorldBlockSetEvent;
import me.tahacheji.mafana.util.MathUtil;
import me.tahacheji.mafana.util.VoidWorldGenerator;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class WorldBlockSetter {

    private String world;
    private List<WorldBlock> worldBlocks;


    private int batchSize;
    private long delayBetweenBatches;


    private List<WorldBlock> upper;
    private List<WorldBlock> under;
    private String upperID;
    private String underID;
    private Location location;
    private double rotateX;
    private double rotateY;
    private double rotateZ;

    private List<WorldBlock> placedBlocks = new ArrayList<>();

    boolean transparent = false;
    boolean packet = false;

    public WorldBlockSetter(String world, List<WorldBlock> worldBlocks, int batchSize, long delayBetweenBatches) {
        this.world = world;
        this.worldBlocks = worldBlocks;
        this.batchSize = batchSize;
        this.delayBetweenBatches = delayBetweenBatches;
    }

    public WorldBlockSetter(List<WorldBlock> upper, List<WorldBlock> under, String upperID, String underID, Location location, int batchSize, long delayBetweenBatches) {
        this.upper = upper;
        this.under = under;
        this.upperID = upperID;
        this.underID = underID;
        this.location = location;
        this.batchSize = batchSize;
        this.delayBetweenBatches = delayBetweenBatches;
    }

    public CompletableFuture<HashMap<Build, Build>> placeBlockAtLocation() {
        WorldBlockSetEvent event = new WorldBlockSetEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            CompletableFuture<HashMap<Build, Build>> future = new CompletableFuture<>();
            HashMap<Build, Build> buildHashMap = new HashMap<>();

            CompletableFuture<Build> upperFuture = placeUpper();
            CompletableFuture<Build> underFuture = placeUnder();

            CompletableFuture<Void> displayBuilds = underFuture.thenComposeAsync(underFutureBuild -> {
                List<WorldBlock> list = new ArrayList<>();
                list.addAll(upperFuture.join().getBlockList());
                list.addAll(underFutureBuild.getBlockList());
                return displayBuilds(list);
            });

            CompletableFuture.allOf(upperFuture, underFuture, displayBuilds).thenAcceptAsync(action -> {
                buildHashMap.put(upperFuture.join(), underFuture.join());
                future.complete(buildHashMap);
            }).exceptionally(ex -> {
                future.completeExceptionally(ex);
                return null;
            });

            return future;
        } else {
            return null;
        }
    }

    public CompletableFuture<Build> placeUpper() {
        CompletableFuture<Build> future = new CompletableFuture<>();
        Build upperBuild = new Build(upperID, false);

        if (!getUpper().isEmpty()) {
            int avgX = (int) getUpper().stream().mapToInt(WorldBlock::getX).average().orElse(0);
            int minY = getUpper().stream().mapToInt(WorldBlock::getY).min().orElse(0);
            int avgZ = (int) getUpper().stream().mapToInt(WorldBlock::getZ).average().orElse(0);

            for (WorldBlock worldBlock : getUpper()) {
                int offsetX = worldBlock.getX() - avgX;
                int offsetY = worldBlock.getY() - minY;
                int offsetZ = worldBlock.getZ() - avgZ;

                int[] rotatedCords = new MathUtil().applyRotations(rotateX, rotateY, rotateZ, offsetX, offsetY, offsetZ);

                int targetX = location.getBlockX() + rotatedCords[0];
                int targetY = location.getBlockY() + rotatedCords[1];
                int targetZ = location.getBlockZ() + rotatedCords[2];

                Location newLocation = new Location(Bukkit.getWorld(world), targetX, targetY, targetZ);
                upperBuild.getBlockList().add(new WorldBlock(worldBlock.getMaterial(), world, targetX, targetY, targetZ));
                placedBlocks.add(new WorldBlock(newLocation.getBlock().getType(), world, targetX, targetY, targetZ));
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                future.complete(upperBuild);
            }
        }.runTaskLater(MafanaWorldProcessor.getInstance(), delayBetweenBatches);

        return future;
    }


    public CompletableFuture<Build> placeUnder() {
        CompletableFuture<Build> future = new CompletableFuture<>();
        Build underBuild = new Build(underID, true);

        if (!getUnder().isEmpty()) {
            int avgX = (int) getUpper().stream().mapToInt(WorldBlock::getX).average().orElse(0);
            int minY = getUnder().stream().mapToInt(WorldBlock::getY).min().orElse(0);
            int avgZ = (int) getUpper().stream().mapToInt(WorldBlock::getZ).average().orElse(0);

            for (WorldBlock worldBlock : getUnder()) {
                int offsetX = worldBlock.getX() - avgX;
                int offsetY = worldBlock.getY() - minY - 4;
                int offsetZ = worldBlock.getZ() - avgZ;

                int[] rotatedCords = new MathUtil().applyRotations(rotateX, rotateY, rotateZ, offsetX, offsetY, offsetZ);

                int targetX = location.getBlockX() + rotatedCords[0];
                int targetY = location.getBlockY() + rotatedCords[1];
                int targetZ = location.getBlockZ() + rotatedCords[2];

                Location newLocation = new Location(Bukkit.getWorld(world), targetX, targetY, targetZ);
                placedBlocks.add(new WorldBlock(newLocation.getBlock().getType(), world, targetX, targetY, targetZ));
                underBuild.getBlockList().add(new WorldBlock(worldBlock.getMaterial(), world, targetX, targetY, targetZ));
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                future.complete(underBuild);
            }
        }.runTaskLaterAsynchronously(MafanaWorldProcessor.getInstance(), delayBetweenBatches);

        return future;
    }



    public CompletableFuture<Void> displayBuilds(List<WorldBlock> worldBlocks) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        new BukkitRunnable() {
            int currentIndex = 0;

            @Override
            public void run() {
                try {
                    int endIndex = Math.min(currentIndex + batchSize, worldBlocks.size());
                    List<WorldBlock> batch = worldBlocks.subList(currentIndex, endIndex);

                    for (WorldBlock worldBlock : batch) {
                        Location location = new Location(Bukkit.getWorld(worldBlock.getWorld()), worldBlock.getX(), worldBlock.getY(), worldBlock.getZ());
                        if(location.getBlock().getType() == Material.AIR && transparent) {
                            if(!packet) {
                                location.getBlock().setType(worldBlock.getMaterial());
                            }
                        } else if (!transparent) {
                            if(!packet) {
                                location.getBlock().setType(worldBlock.getMaterial());
                            }
                        }
                    }
                    if(packet) {
                        MafanaWorldProcessor.getInstance().getBlockManagers().add(new BlockManager(batch));
                    }

                    currentIndex += batchSize;

                    if (currentIndex >= worldBlocks.size()) {
                        future.complete(null);
                        if(packet) {
                            for (BlockManager blockManager : MafanaWorldProcessor.getInstance().getBlockManagers()) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    blockManager.showBlocks(player);
                                }
                            }
                        }
                        cancel(); // Stop the task if all blocks have been displayed
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }
        }.runTaskTimer(MafanaWorldProcessor.getInstance(), 0L, delayBetweenBatches);

        return future;
    }


    public CompletableFuture<List<WorldBlock>> undoBuild(int batchSize, int delayBetweenBatches) {
        CompletableFuture<List<WorldBlock>> future = new CompletableFuture<>();

        new BukkitRunnable() {
            int currentIndex = 0;

            @Override
            public void run() {
                try {
                    int endIndex = Math.min(currentIndex + batchSize, placedBlocks.size());
                    List<WorldBlock> batch = placedBlocks.subList(currentIndex, endIndex);

                    for (WorldBlock worldBlock : batch) {
                        Location location = new Location(Bukkit.getWorld(worldBlock.getWorld()), worldBlock.getX(), worldBlock.getY(), worldBlock.getZ());
                        Block block = location.getBlock();
                        block.setType(worldBlock.getMaterial());
                    }

                    currentIndex += batchSize;

                    if (currentIndex >= placedBlocks.size()) {
                        future.complete(placedBlocks); // Return the updated list of blocks
                        cancel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }
        }.runTaskTimer(MafanaWorldProcessor.getInstance(), 0L, delayBetweenBatches);

        return future;
    }

    public CompletableFuture<World> setWorldBlocksAsyncWithDelay() {
        CompletableFuture<World> future = new CompletableFuture<>();
        World x = Bukkit.getWorld(world);
        if (x == null) {
            WorldCreator worldCreator = new WorldCreator(world);
            worldCreator.generator(new VoidWorldGenerator());
            x = worldCreator.createWorld();
        }
        int currentIndex = 0;
        processBatch(x, worldBlocks, currentIndex, batchSize, delayBetweenBatches, future);
        return future;
    }

    private void processBatch(World voidWorld, List<WorldBlock> worldBlocks, int currentIndex, int batchSize, long delayBetweenBatches, CompletableFuture<World> future) {
        int totalBlocks = worldBlocks.size();
        int endIndex = Math.min(currentIndex + batchSize, totalBlocks);
        List<WorldBlock> batch = worldBlocks.subList(currentIndex, endIndex);

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (WorldBlock worldBlock : batch) {
                        Block block = voidWorld.getBlockAt(worldBlock.getX(), worldBlock.getY(), worldBlock.getZ());
                        block.setType(worldBlock.getMaterial());
                    }
                    if (endIndex < totalBlocks) {
                        processBatch(voidWorld, worldBlocks, endIndex, batchSize, delayBetweenBatches, future);
                    } else {
                        future.complete(voidWorld);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }
        }.runTaskLaterAsynchronously(MafanaWorldProcessor.getInstance(), delayBetweenBatches);
    }

    public List<WorldBlock> getUpper() {
        return upper;
    }

    public List<WorldBlock> getUnder() {
        return under;
    }

    public String getUpperID() {
        return upperID;
    }

    public String getUnderID() {
        return underID;
    }

    public Location getLocation() {
        return location;
    }

    public List<WorldBlock> getPlacedBlocks() {
        return placedBlocks;
    }

    public void setWorld(String world) {
        this.world = world;
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

    public String getWorld() {
        return world;
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

    public void setRotateX(double rotateX) {
        this.rotateX = rotateX;
    }

    public void setRotateY(double rotateY) {
        this.rotateY = rotateY;
    }

    public void setRotateZ(double rotateZ) {
        this.rotateZ = rotateZ;
    }

    public void setUpper(List<WorldBlock> upper) {
        this.upper = upper;
    }

    public void setUpperID(String upperID) {
        this.upperID = upperID;
    }

    public void setUnder(List<WorldBlock> under) {
        this.under = under;
    }

    public void setUnderID(String underID) {
        this.underID = underID;
    }

    public void setPlacedBlocks(List<WorldBlock> placedBlocks) {
        this.placedBlocks = placedBlocks;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPacket(boolean packet) {
        this.packet = packet;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
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

    public boolean isPacket() {
        return packet;
    }

    public boolean isTransparent() {
        return transparent;
    }

}
