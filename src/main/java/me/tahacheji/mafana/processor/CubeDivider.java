package me.tahacheji.mafana.processor;

import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

public class CubeDivider {
    private final Location corner1;
    private final Location corner2;

    public CubeDivider(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public List<Cube> divide(int divisions) {
        if (divisions <= 0) {
            throw new IllegalArgumentException("Number of divisions must be greater than zero.");
        }

        List<Cube> smallerCubes = new ArrayList<>();
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        double stepX = (maxX - minX) / divisions;
        double stepY = (maxY - minY) / divisions;
        double stepZ = (maxZ - minZ) / divisions;

        for (int x = 0; x < divisions; x++) {
            for (int y = 0; y < divisions; y++) {
                for (int z = 0; z < divisions; z++) {
                    Location smallerCorner1 = new Location(corner1.getWorld(), minX + x * stepX, minY + y * stepY, minZ + z * stepZ);
                    Location smallerCorner2 = new Location(corner1.getWorld(), minX + (x + 1) * stepX, minY + (y + 1) * stepY, minZ + (z + 1) * stepZ);
                    smallerCubes.add(new Cube(smallerCorner1, smallerCorner2));
                }
            }
        }

        return smallerCubes;
    }
}
