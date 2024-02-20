package me.tahacheji.mafana.processor;

public class IgnoreLocation {

    private final String world;
    private final int x;
    private final int y;
    private final int z;

    public IgnoreLocation(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public String getWorld() {
        return world;
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
