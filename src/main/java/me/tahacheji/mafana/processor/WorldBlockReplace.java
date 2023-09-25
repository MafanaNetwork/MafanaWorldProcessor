package me.tahacheji.mafana.processor;

import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.processor.Cube;
import me.tahacheji.mafana.processor.CubeDivider;
import me.tahacheji.mafana.processor.WorldBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorldBlockReplace {

    private Location location1;
    private Location location2;
    private List<WorldBlock> originalBlocks = new ArrayList<>();

    public WorldBlockReplace(Location location1, Location location2) {
        this.location1 = location1;
        this.location2 = location2;
    }

    public CompletableFuture<List<WorldBlock>> processCubeAsync(Material originalBlock, Material replacementBlock, int divisions, long delayBetweenCubesTicks) {
        CompletableFuture<List<WorldBlock>> future = new CompletableFuture<>();
        CubeDivider divider = new CubeDivider(location1, location2);
        List<Cube> smallerCubes = divider.divide(divisions);

        List<WorldBlock> allBlocks = new ArrayList<>();

        for (Cube cube : smallerCubes) {
            List<WorldBlock> cubeOriginalBlocks = getWorldBlocksInCube(originalBlock, allBlocks, cube);
            originalBlocks.addAll(cubeOriginalBlocks);
        }

        processCubesRecursively(originalBlock, replacementBlock, smallerCubes, allBlocks, 0, delayBetweenCubesTicks, future);

        return future;
    }

    public CompletableFuture<List<WorldBlock>> processCubeAsyncWithPercentage(Material originalBlock, Material replacementBlock, int divisions, double percentage, long delayBetweenCubesTicks) {
        CompletableFuture<List<WorldBlock>> future = new CompletableFuture<>();
        CubeDivider divider = new CubeDivider(location1, location2);
        List<Cube> smallerCubes = divider.divide(divisions);

        List<WorldBlock> allBlocks = new ArrayList<>();
        for (Cube cube : smallerCubes) {
            List<WorldBlock> cubeOriginalBlocks = getWorldBlocksInCube(originalBlock, allBlocks, cube);
            originalBlocks.addAll(cubeOriginalBlocks);
        }

        processCubesRecursivelyWithPercentage(originalBlock, replacementBlock, smallerCubes, allBlocks, 0, percentage, delayBetweenCubesTicks, future);

        return future;
    }

    private void processCubesRecursively(Material originalBlock, Material replacementBlock, List<Cube> cubes, List<WorldBlock> allBlocks, int currentIndex, long delayBetweenCubesTicks, CompletableFuture<List<WorldBlock>> future) {
        if (currentIndex >= cubes.size()) {
            future.complete(allBlocks);
            return;
        }

        Cube cube = cubes.get(currentIndex);

        Bukkit.getScheduler().runTaskLater(MafanaWorldProcessor.getInstance(), () -> {
            List<WorldBlock> cubeBlocks = getWorldBlocksInCube(originalBlock, allBlocks, cube);

            allBlocks.addAll(cubeBlocks);

            // Replace blocks in the cube
            replaceBlocksInCube(cube, replacementBlock);

            processCubesRecursively(originalBlock, replacementBlock, cubes, allBlocks, currentIndex + 1, delayBetweenCubesTicks, future);
        }, delayBetweenCubesTicks);
    }

    private void processCubesRecursivelyWithPercentage(Material originalBlock, Material replacementBlock, List<Cube> cubes, List<WorldBlock> allBlocks, int currentIndex, double percentage, long delayBetweenCubesTicks, CompletableFuture<List<WorldBlock>> future) {
        if (currentIndex >= cubes.size()) {
            future.complete(allBlocks);
            return;
        }

        Cube cube = cubes.get(currentIndex);

        Bukkit.getScheduler().runTaskLater(MafanaWorldProcessor.getInstance(), () -> {
            List<WorldBlock> cubeBlocks = getWorldBlocksInCube(originalBlock, allBlocks, cube);

            allBlocks.addAll(cubeBlocks);

            // Calculate the number of blocks to replace based on the percentage
            int numBlocksToReplace = (int) (cubeBlocks.size() * percentage);

            // Randomly select blocks to replace while keeping the percentage
            List<WorldBlock> replacedBlocks = replaceRandomBlocksInCube(cubeBlocks, numBlocksToReplace, replacementBlock);

            processCubesRecursivelyWithPercentage(originalBlock, replacementBlock, cubes, allBlocks, currentIndex + 1, percentage, delayBetweenCubesTicks, future);
        }, delayBetweenCubesTicks);
    }

    private List<WorldBlock> getWorldBlocksInCube(Material originalBlock, List<WorldBlock> worldBlockList, Cube cube) {
        List<WorldBlock> worldBlocks = new ArrayList<>();

        for (Location location : cube.getLocations()) {
            Material material = getMaterialAtLocation(location);
            if (material == originalBlock) {
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

    private void replaceBlocksInCube(Cube cube, Material replacementBlock) {
        for (Location location : cube.getLocations()) {
            location.getBlock().setType(replacementBlock);
        }
    }

    private List<WorldBlock> replaceRandomBlocksInCube(List<WorldBlock> worldBlocks, int numBlocksToReplace, Material replacementBlock) {
        List<WorldBlock> replacedBlocks = new ArrayList<>();

        if (numBlocksToReplace <= 0) {
            return replacedBlocks;
        }

        List<WorldBlock> eligibleBlocks = new ArrayList<>(worldBlocks);
        int numEligibleBlocks = eligibleBlocks.size();

        while (numBlocksToReplace > 0 && numEligibleBlocks > 0) {
            int randomIndex = (int) (Math.random() * numEligibleBlocks);
            WorldBlock worldBlock = eligibleBlocks.get(randomIndex);
            Location location = new Location(location1.getWorld(), worldBlock.getX(), worldBlock.getY(), worldBlock.getZ());

            location.getBlock().setType(replacementBlock);
            worldBlocks.remove(worldBlock);
            eligibleBlocks.remove(randomIndex);

            replacedBlocks.add(worldBlock);

            numBlocksToReplace--;
            numEligibleBlocks--;
        }

        return replacedBlocks;
    }

    public List<WorldBlock> getOriginalBlocks() {
        return originalBlocks;
    }

    public Location getLocation1() {
        return location1;
    }

    public Location getLocation2() {
        return location2;
    }
}
