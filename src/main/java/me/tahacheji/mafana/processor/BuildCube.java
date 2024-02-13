package me.tahacheji.mafana.processor;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;

public class BuildCube {

    int highestX;
    int highestY;
    int highestZ;

    int lowestX;
    int lowestY;
    int lowestZ;

    public BuildCube(int highestX, int highestY, int highestZ, int lowestX, int lowestY, int lowestZ) {
        this.highestX = highestX;
        this.highestY = highestY;
        this.highestZ = highestZ;
        this.lowestX = lowestX;
        this.lowestY = lowestY;
        this.lowestZ = lowestZ;
    }

    public boolean isTouching(int amount, TargetBlock targetBlock, World world) {
        for (int i = 0; i < amount; i++) {
            int maxTouchingX = highestX + i;
            int maxTouchingY = highestY + i;
            int maxTouchingZ = highestZ + i;

            int minTouchingX = lowestX - i;
            int minTouchingY = lowestY - i;
            int minTouchingZ = lowestZ - i;

            for (int x = minTouchingX; x <= maxTouchingX; x++) {
                for (int y = minTouchingY; y <= maxTouchingY; y++) {
                    for (int z = minTouchingZ; z <= maxTouchingZ; z++) {
                        Location location = new Location(world, x, y, z);
                        Material blockType = location.getBlock().getType();

                        if (blockType != Material.AIR && blockType != targetBlock.getMaterial()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public int getHighestX() {
        return highestX;
    }

    public int getHighestY() {
        return highestY;
    }

    public int getHighestZ() {
        return highestZ;
    }

    public int getLowestX() {
        return lowestX;
    }

    public int getLowestY() {
        return lowestY;
    }

    public int getLowestZ() {
        return lowestZ;
    }


}
