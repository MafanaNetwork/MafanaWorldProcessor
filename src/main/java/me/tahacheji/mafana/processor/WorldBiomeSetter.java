package me.tahacheji.mafana.processor;

import me.tahacheji.mafana.MafanaWorldProcessor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorldBiomeSetter {
    private World world;
    private Location x;
    private Location y;
    private int divisions;
    private long delayBetweenCubesTicks;
    private Biome targetBiome;

    public WorldBiomeSetter(World world, Location x, Location y, int divisions, long delayBetweenCubesTicks, Biome targetBiome) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.divisions = divisions;
        this.delayBetweenCubesTicks = delayBetweenCubesTicks;
        this.targetBiome = targetBiome;
    }

    public CompletableFuture<Void> setBiomeAsync() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        CubeDivider divider = new CubeDivider(x, y);
        List<Cube> smallerCubes = divider.divide(divisions);

        setBiomeInCubesRecursively(smallerCubes, 0, delayBetweenCubesTicks, future);

        return future;
    }

    private void setBiomeInCubesRecursively(List<Cube> cubes, int currentIndex, long delayBetweenCubesTicks, CompletableFuture<Void> future) {
        if (currentIndex >= cubes.size()) {
            future.complete(null);
            return;
        }

        Cube cube = cubes.get(currentIndex);

        new BukkitRunnable() {
            @Override
            public void run() {
                setBiomeInCube(cube);
                setBiomeInCubesRecursively(cubes, currentIndex + 1, delayBetweenCubesTicks, future);
            }
        }.runTaskLater(MafanaWorldProcessor.getInstance(), delayBetweenCubesTicks);
    }

    private void setBiomeInCube(Cube cube) {
        for (Location location : cube.getLocations()) {
            if(MafanaWorldProcessor.getInstance().getIgnoreLocations().contains(location)) {
                continue;
            }
            Block block = world.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());

            if(targetBiome.name().contains("ocean") && block.getType() == Material.WATER) {
                block.setBiome(targetBiome);
            } else {
                block.setBiome(targetBiome);
            }
        }
    }
}

