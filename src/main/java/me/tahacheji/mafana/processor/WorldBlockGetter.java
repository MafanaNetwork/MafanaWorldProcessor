package me.tahacheji.mafana.processor;

import me.tahacheji.mafana.processor.Cube;
import me.tahacheji.mafana.processor.CubeDivider;
import me.tahacheji.mafana.processor.WorldBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

        List<WorldBlock> allBlocks = new ArrayList<>();

        for (Cube cube : smallerCubes) {
            List<WorldBlock> cubeBlocks = getWorldBlocksInCube(cube);
            allBlocks.addAll(cubeBlocks);
        }

        future.complete(allBlocks);
        return future;
    }

    private List<WorldBlock> getWorldBlocksInCube(Cube cube) {
        List<WorldBlock> worldBlocks = new ArrayList<>();
        World world = x.getWorld();

        // Iterate through locations within the cube and retrieve materials
        for (Location location : cube.getLocations()) {
            Material material = getMaterialAtLocation(location);
            worldBlocks.add(new WorldBlock(material, location.getBlockX(), location.getBlockY(), location.getBlockZ()));
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
