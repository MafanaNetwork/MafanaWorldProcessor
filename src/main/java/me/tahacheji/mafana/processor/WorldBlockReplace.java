package me.tahacheji.mafana.processor;

import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.event.WorldBlockReplaceEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorldBlockReplace {

    private Location x;
    private Location y;
    private List<Material> targetedBlock;
    private List<Material> replaceBlock;
    private double percentage;
    private int divisions;
    private long delayBetweenCubesTicks;
    private List<WorldBlock> originalBlocks = new ArrayList<>();

    private boolean packet;

    BlockManager blockManager = new BlockManager();

    public WorldBlockReplace(Location x, Location y, List<Material> targetedBlock, List<Material> replaceBlock, double percentage, int divisions, long delayBetweenCubesTicks, boolean packet) {
        this.x = x;
        this.y = y;
        this.targetedBlock = targetedBlock;
        this.replaceBlock = replaceBlock;
        this.percentage = percentage;
        this.divisions = divisions;
        this.delayBetweenCubesTicks = delayBetweenCubesTicks;
        this.packet = packet;
    }

    public CompletableFuture<List<WorldBlock>> processCubeAsyncWithPercentage() {
        WorldBlockReplaceEvent event = new WorldBlockReplaceEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            CompletableFuture<List<WorldBlock>> future = new CompletableFuture<>();
            CubeDivider divider = new CubeDivider(x, y);
            List<Cube> smallerCubes = divider.divide(divisions);

            List<WorldBlock> allBlocks = new ArrayList<>();
            for (Cube cube : smallerCubes) {
                List<WorldBlock> cubeOriginalBlocks = getWorldBlocksInCube(targetedBlock, allBlocks, cube);
                originalBlocks.addAll(cubeOriginalBlocks);
            }

            processCubesRecursivelyWithPercentage(targetedBlock, replaceBlock, smallerCubes, allBlocks, 0, percentage, delayBetweenCubesTicks, future);

            return future;
        } else {
            return null;
        }
    }

    public CompletableFuture<Void> undoReplaceBlocksAsync() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        CubeDivider divider = new CubeDivider(x, y);
        List<Cube> smallerCubes = divider.divide(divisions);

        undoReplaceCubesRecursively(smallerCubes, 0, delayBetweenCubesTicks, future);

        return future;
    }

    private void undoReplaceCubesRecursively(List<Cube> cubes, int currentIndex, long delayBetweenCubesTicks, CompletableFuture<Void> future) {
        if (currentIndex >= cubes.size()) {
            future.complete(null);
            return;
        }

        Cube cube = cubes.get(currentIndex);

        Bukkit.getScheduler().runTaskLater(MafanaWorldProcessor.getInstance(), () -> {
            restoreOriginalBlocksInCube(cube);

            undoReplaceCubesRecursively(cubes, currentIndex + 1, delayBetweenCubesTicks, future);
        }, delayBetweenCubesTicks);
    }

    private void restoreOriginalBlocksInCube(Cube cube) {
        for (Location location : cube.getLocations()) {
            for (WorldBlock originalBlock : originalBlocks) {
                if (originalBlock.getX() == location.getBlockX()
                        && originalBlock.getY() == location.getBlockY()
                        && originalBlock.getZ() == location.getBlockZ()) {
                    if(!packet) {
                        location.getBlock().setType(originalBlock.getMaterial());
                    } else {
                        MafanaWorldProcessor.getInstance().getBlockManagers().remove(blockManager);
                    }
                    break;
                }
            }
        }
    }

    private void processCubesRecursivelyWithPercentage(List<Material> targetedBlock, List<Material> replaceBlock, List<Cube> cubes, List<WorldBlock> allBlocks, int currentIndex, double percentage, long delayBetweenCubesTicks, CompletableFuture<List<WorldBlock>> future) {
        if (currentIndex >= cubes.size()) {
            future.complete(allBlocks);
            return;
        }

        Cube cube = cubes.get(currentIndex);

        Bukkit.getScheduler().runTaskLater(MafanaWorldProcessor.getInstance(), () -> {
            List<WorldBlock> cubeBlocks = getWorldBlocksInCube(targetedBlock, allBlocks, cube);

            allBlocks.addAll(cubeBlocks);

            // Calculate the number of blocks to replace based on the percentage
            int numBlocksToReplace = (int) (cubeBlocks.size() * percentage);

            // Randomly select blocks to replace while keeping the percentage
            List<WorldBlock> replacedBlocks = replaceRandomBlocksInCube(cubeBlocks, numBlocksToReplace, replaceBlock);

            processCubesRecursivelyWithPercentage(targetedBlock, replaceBlock, cubes, allBlocks, currentIndex + 1, percentage, delayBetweenCubesTicks, future);
        }, delayBetweenCubesTicks);
    }

    private List<WorldBlock> getWorldBlocksInCube(List<Material> targetedBlock, List<WorldBlock> worldBlockList, Cube cube) {
        List<WorldBlock> worldBlocks = new ArrayList<>();

        for (Location location : cube.getLocations()) {
            if(MafanaWorldProcessor.getInstance().getIgnoreLocations().contains(location)) {
                continue;
            }
            Material material = getMaterialAtLocation(location);
            if (targetedBlock.contains(material)) {
                boolean exists = worldBlockList.stream()
                        .anyMatch(worldBlock -> worldBlock.getX() == location.getBlockX()
                                && worldBlock.getY() == location.getBlockY()
                                && worldBlock.getZ() == location.getBlockZ());

                if (!exists) {
                    worldBlocks.add(new WorldBlock(material, location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                }
            }
        }

        return worldBlocks;
    }

    private Material getMaterialAtLocation(Location location) {
        return location.getBlock().getType();
    }

    private List<WorldBlock> replaceRandomBlocksInCube(List<WorldBlock> worldBlocks, int numBlocksToReplace, List<Material> replaceBlock) {
        List<WorldBlock> replacedBlocks = new ArrayList<>();

        if (numBlocksToReplace <= 0) {
            return replacedBlocks;
        }

        List<WorldBlock> eligibleBlocks = new ArrayList<>(worldBlocks);
        int numEligibleBlocks = eligibleBlocks.size();

        while (numBlocksToReplace > 0 && numEligibleBlocks > 0) {
            int randomIndex = (int) (Math.random() * numEligibleBlocks);
            WorldBlock worldBlock = eligibleBlocks.get(randomIndex);
            Location location = new Location(x.getWorld(), worldBlock.getX(), worldBlock.getY(), worldBlock.getZ());

            Material originalMaterial = location.getBlock().getType();
            if (targetedBlock.contains(originalMaterial)) {
                Material replacementMaterial = getRandomMaterial(replaceBlock);
                if(!packet) {
                    location.getBlock().setType(replacementMaterial);
                }
                worldBlock.setMaterial(replacementMaterial);

                eligibleBlocks.remove(randomIndex);

                replacedBlocks.add(worldBlock);

                numBlocksToReplace--;
            }

            numEligibleBlocks--;
        }
        if(packet) {
            blockManager.getBlockList().addAll(replacedBlocks);
            MafanaWorldProcessor.getInstance().getBlockManagers().add(blockManager);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(blockManager.isBlockInChunk(player.getChunk())) {
                        blockManager.showBlocks(player);
                    }
                }
        }
        return replacedBlocks;
    }


    private Material getRandomMaterial(List<Material> materials) {
        int randomIndex = (int) (Math.random() * materials.size());
        return materials.get(randomIndex);
    }

    public BlockManager getBlockManager() {
        return blockManager;
    }

    public Location getX() {
        return x;
    }

    public Location getY() {
        return y;
    }

    public List<Material> getTargetedBlock() {
        return targetedBlock;
    }

    public List<Material> getReplaceBlock() {
        return replaceBlock;
    }

    public double getPercentage() {
        return percentage;
    }

    public int getDivisions() {
        return divisions;
    }

    public long getDelayBetweenCubesTicks() {
        return delayBetweenCubesTicks;
    }

    public List<WorldBlock> getOriginalBlocks() {
        return originalBlocks;
    }

    public void setX(Location x) {
        this.x = x;
    }

    public void setY(Location y) {
        this.y = y;
    }

    public void setTargetedBlock(List<Material> targetedBlock) {
        this.targetedBlock = targetedBlock;
    }

    public void setReplaceBlock(List<Material> replaceBlock) {
        this.replaceBlock = replaceBlock;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setDivisions(int divisions) {
        this.divisions = divisions;
    }

    public void setDelayBetweenCubesTicks(long delayBetweenCubesTicks) {
        this.delayBetweenCubesTicks = delayBetweenCubesTicks;
    }

    public void setOriginalBlocks(List<WorldBlock> originalBlocks) {
        this.originalBlocks = originalBlocks;
    }

    public void setPacket(boolean packet) {
        this.packet = packet;
    }

    public void setBlockManager(BlockManager blockManager) {
        this.blockManager = blockManager;
    }
}
