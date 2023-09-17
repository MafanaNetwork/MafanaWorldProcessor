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
}







