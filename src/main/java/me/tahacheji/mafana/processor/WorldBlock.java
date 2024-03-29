package me.tahacheji.mafana.processor;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class WorldBlock {
    private Material material;
    private String world;
    private int x;
    private int y;
    private int z;

    public WorldBlock(Material material, int x, int y, int z) {
        this.material = material;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public WorldBlock(Material material, String world, int x, int y, int z) {
        this.material = material;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setWorld(String world) {
        this.world = world;
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
