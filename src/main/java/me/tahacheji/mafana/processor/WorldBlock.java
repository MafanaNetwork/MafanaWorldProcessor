package me.tahacheji.mafana.processor;

import org.bukkit.Material;

public class WorldBlock {
    private Material material;
    private int x;
    private int y;
    private int z;

    public WorldBlock(Material material, int x, int y, int z) {
        this.material = material;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Material getMaterial() {
        return material;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
