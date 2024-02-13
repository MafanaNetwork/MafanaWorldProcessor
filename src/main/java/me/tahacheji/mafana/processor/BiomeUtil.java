package me.tahacheji.mafana.processor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;

import java.util.List;

public class BiomeUtil {

    public Biome getBiomeFromString(String biomeName) {
        for (Biome biome : Biome.values()) {
            if (biome.name().equalsIgnoreCase(biomeName)) {
                return biome;
            }
        }
        return null;
    }

    public void setBiomeForCube(Biome targetBiome, List<WorldBlock> cube) {
        for (WorldBlock worldBlock : cube) {
            Location location = new Location(Bukkit.getWorld(worldBlock.getWorld()), worldBlock.getX(), worldBlock.getY(), worldBlock.getZ());
            location.getBlock().setBiome(targetBiome);
        }
    }
}
