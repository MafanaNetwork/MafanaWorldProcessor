package me.tahacheji.mafana.processor;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class LocationProcessor {
    private Location x;
    private Location y;

    public LocationProcessor(Location x, Location y) {
        this.x = x;
        this.y = y;
    }

    public World getWorld() {
        return x.getWorld();
    }

    public List<Location> getLocations() {
        World world = getWorld();
        double minX = Math.min(x.getX(), y.getX());
        double minY = Math.min(x.getY(), y.getY());
        double minZ = Math.min(x.getZ(), y.getZ());
        double maxX = Math.max(x.getX(), y.getX());
        double maxY = Math.max(x.getY(), y.getY());
        double maxZ = Math.max(x.getZ(), y.getZ());

        int xMin = (int) minX;
        int yMin = (int) minY;
        int zMin = (int) minZ;
        int xMax = (int) maxX;
        int yMax = (int) maxY;
        int zMax = (int) maxZ;

        List<Location> locations = new ArrayList<>();

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    locations.add(new Location(world, x, y, z));
                }
            }
        }

        return locations;
    }

    public CompletableFuture<List<Location>> getLocationsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            World world = getWorld();
            double minX = Math.min(x.getX(), y.getX());
            double minY = Math.min(x.getY(), y.getY());
            double minZ = Math.min(x.getZ(), y.getZ());
            double maxX = Math.max(x.getX(), y.getX());
            double maxY = Math.max(x.getY(), y.getY());
            double maxZ = Math.max(x.getZ(), y.getZ());

            int xMin = (int) minX;
            int yMin = (int) minY;
            int zMin = (int) minZ;
            int xMax = (int) maxX;
            int yMax = (int) maxY;
            int zMax = (int) maxZ;

            List<Location> locations = new ArrayList<>();

            for (int x = xMin; x <= xMax; x++) {
                for (int y = yMin; y <= yMax; y++) {
                    for (int z = zMin; z <= zMax; z++) {
                        locations.add(new Location(world, x, y, z));
                    }
                }
            }

            return locations;
        });
    }

}
