package me.tahacheji.mafana.processor;

import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.processor.WorldBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class WorldBlockGetter {
    private final World world;
    private final Location x;
    private final Location y;

    public WorldBlockGetter(World world, Location x, Location y) {
        this.world = world;
        this.x = x;
        this.y = y;
    }

    public CompletableFuture<List<WorldBlock>> processCubeAsync(int divisions, long delayBetweenCubesTicks, boolean air) {
        CompletableFuture<List<WorldBlock>> future = new CompletableFuture<>();
        CubeDivider divider = new CubeDivider(x, y);
        List<Cube> smallerCubes = divider.divide(divisions);

        List<WorldBlock> allBlocks = new ArrayList<>();

        processCubesRecursively(smallerCubes, allBlocks, 0, delayBetweenCubesTicks, future, air);

        return future;
    }

    private void processCubesRecursively(List<Cube> cubes, List<WorldBlock> allBlocks, int currentIndex, long delayBetweenCubesTicks, CompletableFuture<List<WorldBlock>> future, boolean air) {
        if (currentIndex >= cubes.size()) {
            future.complete(allBlocks);
            return;
        }

        Cube cube = cubes.get(currentIndex);

        Bukkit.getScheduler().runTaskLater(MafanaWorldProcessor.getInstance(), () -> {
            List<WorldBlock> cubeBlocks = getWorldBlocksInCube(allBlocks, cube, air);

            allBlocks.addAll(cubeBlocks);

            processCubesRecursively(cubes, allBlocks, currentIndex + 1, delayBetweenCubesTicks, future, air);
        }, delayBetweenCubesTicks);
    }

    private List<WorldBlock> getWorldBlocksInCube(List<WorldBlock> worldBlockList, Cube cube, boolean air) {
        List<WorldBlock> worldBlocks = new ArrayList<>();

        for (Location location : cube.getLocations()) {
            Material material = getMaterialAtLocation(location);
            if(!air && material == Material.AIR) {
                continue;
            }
            boolean m = false;
            for(WorldBlock worldBlock : worldBlockList) {
                if(worldBlock.getX() == location.getBlockX() && worldBlock.getY() == location.getBlockY() && worldBlock.getZ() == location.getBlockZ()) {
                    m = true;
                    break;
                }
            }
            if(!m) {
                worldBlocks.add(new WorldBlock(material, location.getBlockX(), location.getBlockY(), location.getBlockZ()));
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
