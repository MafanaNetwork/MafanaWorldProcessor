package me.tahacheji.mafana.processor;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Build {

    private List<WorldBlock> blockList = new ArrayList<>();
    private String ID;
    private boolean under;

    public Build(String ID, boolean under) {
        this.ID = ID;
        this.under = under;
    }

    public Build(List<WorldBlock> x) {
        this.blockList = x;
    }

    public List<WorldBlock> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<WorldBlock> blockList) {
        this.blockList = blockList;
    }


    public void setUnder(boolean under) {
        this.under = under;
    }

    public boolean isUnder() {
        return under;
    }

    public BuildCube getBuildCube() {
        int highestX = blockList.stream().mapToInt(WorldBlock::getX).max().orElse(0);
        int highestY = blockList.stream().mapToInt(WorldBlock::getY).max().orElse(0);
        int highestZ = blockList.stream().mapToInt(WorldBlock::getZ).max().orElse(0);

        int lowestX = blockList.stream().mapToInt(WorldBlock::getX).min().orElse(0);
        int lowestY = blockList.stream().mapToInt(WorldBlock::getY).min().orElse(0);
        int lowestZ = blockList.stream().mapToInt(WorldBlock::getZ).min().orElse(0);

        return new BuildCube(highestX, highestY, highestZ, lowestX, lowestY, lowestZ);
    }

    public Location getBottomMidPoint() {
        int avgX = (int) blockList.stream().mapToInt(WorldBlock::getX).average().orElse(0);
        int minY = blockList.stream().mapToInt(WorldBlock::getY).min().orElse(0);
        int avgZ = (int) blockList.stream().mapToInt(WorldBlock::getZ).average().orElse(0);
        return new Location(Bukkit.getWorld(blockList.get(0).getWorld()), avgX, minY, avgZ);
    }

    public String getID() {
        return ID;
    }
}
