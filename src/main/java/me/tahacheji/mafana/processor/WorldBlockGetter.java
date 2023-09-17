package me.tahacheji.mafana.processor;

import me.tahacheji.mafana.MafanaWorldProcessor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorldBlockGetter {
    private final World world;
    private final Location x;
    private final Location y;

    public WorldBlockGetter(World world, Location x, Location y) {
        this.world = world;
        this.x = x;
        this.y = y;
    }

    public CompletableFuture<List<WorldBlock>> processCubeAsync(int divisions) {
        CompletableFuture<List<WorldBlock>> future = new CompletableFuture<>();
        CubeDivider divider = new CubeDivider(x, y);
        List<Cube> smallerCubes = divider.divide(divisions);
        List<CompletableFuture<List<WorldBlock>>> cubeFutures = new ArrayList<>();

        for (Cube cube : smallerCubes) {
            List<Location> cubeLocations = cube.getLocations();
            CompletableFuture<List<WorldBlock>> cubeFuture = processCubeAsync(cubeLocations);
            cubeFutures.add(cubeFuture);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(cubeFutures.toArray(new CompletableFuture[0]));

        allOf.thenAccept(result -> {
            List<WorldBlock> allBlocks = new ArrayList<>();
            for (CompletableFuture<List<WorldBlock>> cubeFuture : cubeFutures) {
                try {
                    allBlocks.addAll(cubeFuture.get());
                } catch (Exception e) {
                    // Handle exceptions if needed
                }
            }
            future.complete(allBlocks);
        });

        return future;
    }

    private CompletableFuture<List<WorldBlock>> processCubeAsync(List<Location> locations) {
        CompletableFuture<List<WorldBlock>> future = new CompletableFuture<>();

        for (Location location : locations) {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    List<WorldBlock> worldBlocks = getWorldBlocksInCube(location, 1, 1, 1);
                    future.complete(worldBlocks);
                }
            }.runTask(MafanaWorldProcessor.getInstance());
        }

        return future;
    }

    private List<WorldBlock> getWorldBlocksInCube(Location corner, int width, int height, int depth) {
        List<WorldBlock> worldBlocks = new ArrayList<>();
        World world = corner.getWorld();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    Location location = new Location(world, corner.getX() + x, corner.getY() + y, corner.getZ() + z);
                    // Assuming you have a method to retrieve Material at a location
                    Material material = getMaterialAtLocation(location);
                    worldBlocks.add(new WorldBlock(material, location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                }
            }
        }

        return worldBlocks;
    }

    private Material getMaterialAtLocation(Location location) {
        return location.getBlock().getType();
    }

    public Location getY() {
        return y;
    }

    public Location getX() {
        return x;
    }

    public World getWorld() {
        return world;
    }
}
