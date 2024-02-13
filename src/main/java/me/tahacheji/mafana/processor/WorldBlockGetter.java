package me.tahacheji.mafana.processor;

import me.tahacheji.mafana.MafanaWorldProcessor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorldBlockGetter {
    private World world;
    private Location x;
    private Location y;
    private int divisions;
    private long delayBetweenCubesTicks;
    private List<Material> target;
    private List<Material> exclude;

    public WorldBlockGetter(World world, Location x, Location y, int divisions, long delayBetweenCubesTicks, List<Material> target, List<Material> exclude) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.divisions = divisions;
        this.delayBetweenCubesTicks = delayBetweenCubesTicks;
        this.target = target;
        this.exclude = exclude;
    }

    public CompletableFuture<List<List<WorldBlock>>> processCubeAsync() {
        CompletableFuture<List<List<WorldBlock>>> future = new CompletableFuture<>();
        CubeDivider divider = new CubeDivider(x, y);
        List<Cube> smallerCubes = divider.divide(divisions);

        List<List<WorldBlock>> allBlocks = new ArrayList<>();

        processCubesRecursively(smallerCubes, allBlocks, 0, delayBetweenCubesTicks, future);

        return future;
    }

    private void processCubesRecursively(List<Cube> cubes, List<List<WorldBlock>> allBlocks, int currentIndex, long delayBetweenCubesTicks, CompletableFuture<List<List<WorldBlock>>> future) {
        if (currentIndex >= cubes.size()) {
            future.complete(allBlocks);
            return;
        }

        Cube cube = cubes.get(currentIndex);

        Bukkit.getScheduler().runTaskLaterAsynchronously(MafanaWorldProcessor.getInstance(), () -> {
            List<WorldBlock> cubeBlocks = getWorldBlocksInCube(cube);

            allBlocks.add(cubeBlocks);

            processCubesRecursively(cubes, allBlocks, currentIndex + 1, delayBetweenCubesTicks, future);
        }, delayBetweenCubesTicks);
    }

    private List<WorldBlock> getWorldBlocksInCube(Cube cube) {
        List<WorldBlock> worldBlocks = new ArrayList<>();

        for (Location location : cube.getLocations()) {
            Material material = getMaterialAtLocation(location);

            boolean includeMaterial = (target.isEmpty() || target.contains(material)) && (exclude.isEmpty() || !exclude.contains(material));

            if (includeMaterial) {
                boolean exists = worldBlocks.stream()
                        .anyMatch(worldBlock -> worldBlock.getX() == location.getBlockX()
                                && worldBlock.getY() == location.getBlockY()
                                && worldBlock.getZ() == location.getBlockZ());

                if (!exists) {
                    worldBlocks.add(new WorldBlock(material, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                }
            }
        }

        return worldBlocks;
    }

    private Material getMaterialAtLocation(Location location) {
        return location.getBlock().getType();
    }

    public World getWorld() {
        return world;
    }

    public Location getX() {
        return x;
    }

    public Location getY() {
        return y;
    }

    public int getDivisions() {
        return divisions;
    }

    public long getDelayBetweenCubesTicks() {
        return delayBetweenCubesTicks;
    }

    public List<Material> getTarget() {
        return target;
    }

    public List<Material> getExclude() {
        return exclude;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setX(Location x) {
        this.x = x;
    }

    public void setY(Location y) {
        this.y = y;
    }

    public void setDivisions(int divisions) {
        this.divisions = divisions;
    }

    public void setDelayBetweenCubesTicks(long delayBetweenCubesTicks) {
        this.delayBetweenCubesTicks = delayBetweenCubesTicks;
    }

    public void setTarget(List<Material> target) {
        this.target = target;
    }

    public void setExclude(List<Material> exclude) {
        this.exclude = exclude;
    }
}


