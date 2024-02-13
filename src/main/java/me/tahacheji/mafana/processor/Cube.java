package me.tahacheji.mafana.processor;

import org.bukkit.Location;

import java.util.List;

public class Cube {
    private final Location corner1;
    private final Location corner2;

    public Cube(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public List<Location> getLocations() {
        return new LocationProcessor(corner1, corner2).getLocations();
    }

    public int getMinX() {
        return Math.min(corner1.getBlockX(), corner2.getBlockX());
    }

    public int getMinY() {
        return Math.min(corner1.getBlockY(), corner2.getBlockY());
    }

    public int getMinZ() {
        return Math.min(corner1.getBlockZ(), corner2.getBlockZ());
    }

    public int getMaxX() {
        return Math.max(corner1.getBlockX(), corner2.getBlockX());
    }

    public int getMaxY() {
        return Math.max(corner1.getBlockY(), corner2.getBlockY());
    }

    public int getMaxZ() {
        return Math.max(corner1.getBlockZ(), corner2.getBlockZ());
    }

    public int getSizeX() {
        return getMaxX() - getMinX() + 1;
    }

    public int getSizeY() {
        return getMaxY() - getMinY() + 1;
    }

    public int getSizeZ() {
        return getMaxZ() - getMinZ() + 1;
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

}







