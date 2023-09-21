package me.tahacheji.mafana.processor;

import me.tahacheji.mafana.MafanaWorldProcessor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorldBlockPlacer {

    private Location location1;
    private Location location2;

    public WorldBlockPlacer(Location location1, Location location2) {
        this.location1 = location1;
        this.location2 = location2;
    }

    public CompletableFuture<List<WorldBlock>> placeBlocksAsync(Material blockToPlace, Material targetBlock, int divisions, double percentage, long delayBetweenCubesTicks) {
        CompletableFuture<List<WorldBlock>> future = new CompletableFuture<>();
        CubeDivider divider = new CubeDivider(location1, location2);
        List<Cube> smallerCubes = divider.divide(divisions);

        List<WorldBlock> allBlocks = new ArrayList<>();

        placeBlocksRecursively(blockToPlace, targetBlock, smallerCubes, allBlocks, 0, percentage, delayBetweenCubesTicks, future);

        return future;
    }

    public CompletableFuture<List<WorldBlock>> removeBlocksAsync(Material blockToRemove, int divisions, long delayBetweenCubesTicks) {
        CompletableFuture<List<WorldBlock>> future = new CompletableFuture<>();
        CubeDivider divider = new CubeDivider(location1, location2);
        List<Cube> smallerCubes = divider.divide(divisions);

        List<WorldBlock> allBlocks = new ArrayList<>();

        removeBlocksRecursively(blockToRemove, smallerCubes, allBlocks, 0, delayBetweenCubesTicks, future);

        return future;
    }

    private void removeBlocksRecursively(Material blockToRemove, List<Cube> cubes, List<WorldBlock> allBlocks, int currentIndex, long delayBetweenCubesTicks, CompletableFuture<List<WorldBlock>> future) {
        if (currentIndex >= cubes.size()) {
            future.complete(allBlocks);
            return;
        }

        Cube cube = cubes.get(currentIndex);

        Bukkit.getScheduler().runTaskLater(MafanaWorldProcessor.getInstance(), () -> {
            List<WorldBlock> cubeBlocks = getWorldBlocksOfTypeInCube(blockToRemove, allBlocks, cube);

            allBlocks.addAll(cubeBlocks);

            // Remove blocks in the cube
            removeBlocksInCube(cube, blockToRemove);

            removeBlocksRecursively(blockToRemove, cubes, allBlocks, currentIndex + 1, delayBetweenCubesTicks, future);
        }, delayBetweenCubesTicks);
    }

    private List<WorldBlock> getWorldBlocksOfTypeInCube(Material blockType, List<WorldBlock> worldBlockList, Cube cube) {
        List<WorldBlock> worldBlocks = new ArrayList<>();

        for (Location location : cube.getLocations()) {
            Material material = getMaterialAtLocation(location);
            if (material == blockType) {
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

    private void removeBlocksInCube(Cube cube, Material blockToRemove) {
        for (Location location : cube.getLocations()) {
            if (location.getBlock().getType() == blockToRemove) {
                location.getBlock().setType(Material.AIR);
            }
        }
    }

    private void placeBlocksRecursively(Material blockToPlace, Material targetBlock, List<Cube> cubes, List<WorldBlock> allBlocks, int currentIndex, double percentage, long delayBetweenCubesTicks, CompletableFuture<List<WorldBlock>> future) {
        if (currentIndex >= cubes.size()) {
            future.complete(allBlocks);
            return;
        }

        Cube cube = cubes.get(currentIndex);

        Bukkit.getScheduler().runTaskLater(MafanaWorldProcessor.getInstance(), () -> {
            List<WorldBlock> cubeBlocks = getWorldBlocksInCube(targetBlock, allBlocks, cube);

            allBlocks.addAll(cubeBlocks);

            // Calculate the number of blocks to place based on the percentage
            int numBlocksToPlace = (int) (cubeBlocks.size() * percentage);

            // Randomly select locations to place blocks while keeping the percentage
            placeRandomBlocksInCube(blockToPlace, cube, numBlocksToPlace);

            placeBlocksRecursively(blockToPlace, targetBlock, cubes, allBlocks, currentIndex + 1, percentage, delayBetweenCubesTicks, future);
        }, delayBetweenCubesTicks);
    }

    private List<WorldBlock> getWorldBlocksInCube(Material targetBlock, List<WorldBlock> worldBlockList, Cube cube) {
        List<WorldBlock> worldBlocks = new ArrayList<>();

        for (Location location : cube.getLocations()) {
            Material material = getMaterialAtLocation(location);
            if (material == targetBlock) {
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

    private void placeRandomBlocksInCube(Material blockToPlace, Cube cube, int numBlocksToPlace) {
        if (numBlocksToPlace <= 0) {
            return;
        }

        List<Location> eligibleLocations = new ArrayList<>(cube.getLocations());
        int numEligibleLocations = eligibleLocations.size();

        while (numBlocksToPlace > 0 && numEligibleLocations > 0) {
            int randomIndex = (int) (Math.random() * numEligibleLocations);
            Location targetLocation = eligibleLocations.get(randomIndex);

            // Check if the block above the targetLocation is air before placing
            Location locationToPlace = targetLocation.clone().add(0, 1, 0);
            Block blockAbove = locationToPlace.getBlock();
            if (blockAbove.getType() == Material.AIR) {
                // Place the blockToPlace one block above the targetLocation
                locationToPlace.getBlock().setType(blockToPlace);
                eligibleLocations.remove(randomIndex);
                numBlocksToPlace--;
            }

            numEligibleLocations--;
        }
    }
}
