package me.tahacheji.mafana.processor;

import org.bukkit.Material;

public class TargetBlock {

    private Material material;
    private int x;
    private int y;
    private int z;

    public TargetBlock(Material material, int x, int y, int z) {
        this.material = material;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
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
