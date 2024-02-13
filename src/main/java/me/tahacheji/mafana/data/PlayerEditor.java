package me.tahacheji.mafana.data;

import me.tahacheji.mafana.processor.WorldBlockPlacer;

import me.tahacheji.mafana.processor.WorldBlockReplace;
import me.tahacheji.mafana.processor.WorldBlockSetter;
import me.tahacheji.mafana.processor.WorldBlockTransform;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerEditor {

    private final Player player;

    private WorldBlockTransform worldBlockTransform = new WorldBlockTransform(0,0,0,0,0,0,0,0);

    private List<WorldBlockPlacer> worldBlockPlacers = new ArrayList<>();
    private List<WorldBlockReplace> worldBlockReplaces = new ArrayList<>();
    private List<WorldBlockSetter> worldBlockSetters = new ArrayList<>();

    private Location point1;
    private Location point2;

    public PlayerEditor(Player player) {
        this.player = player;
    }

    public void setWorldBlockPlacers(List<WorldBlockPlacer> worldBlockReplaces) {
        this.worldBlockPlacers = worldBlockReplaces;
    }

    public void addWorldBlockPlacers(WorldBlockPlacer worldBlockReplaces) {
        this.worldBlockPlacers.add(worldBlockReplaces);
    }

    public List<WorldBlockPlacer> getWorldBlockPlacers() {
        return worldBlockPlacers;
    }

    public WorldBlockPlacer getMostRecentWorldBlockPlacer() {
        if (!worldBlockPlacers.isEmpty()) {
            return worldBlockPlacers.get(worldBlockPlacers.size() - 1);
        }
        return null;
    }

    public void setWorldBlockReplaces(List<WorldBlockReplace> worldBlockReplaces) {
        this.worldBlockReplaces = worldBlockReplaces;
    }

    public void addWorldBlockReplaces(WorldBlockReplace worldBlockReplaces) {
        this.worldBlockReplaces.add(worldBlockReplaces);
    }

    public List<WorldBlockReplace> getWorldBlockReplaces() {
        return worldBlockReplaces;
    }

    public WorldBlockReplace getMostRecentWorldBlockReplace() {
        if (!worldBlockReplaces.isEmpty()) {
            return worldBlockReplaces.get(worldBlockReplaces.size() - 1);
        }
        return null;
    }

    public void setWorldBlockSetters(List<WorldBlockSetter> worldBlockSetters) {
        this.worldBlockSetters = worldBlockSetters;
    }
    public void addWorldBlockSetter(WorldBlockSetter worldBlockSetter) {
        this.worldBlockSetters.add(worldBlockSetter);
    }
    public List<WorldBlockSetter> getWorldBlockSetters() {
        return worldBlockSetters;
    }
    public WorldBlockSetter getMostRecentWorldBlockSetter() {
        if (!worldBlockSetters.isEmpty()) {
            return worldBlockSetters.get(worldBlockSetters.size() - 1);
        }
        return null;
    }


    public WorldBlockTransform getWorldBlockTransform() {
        return worldBlockTransform;
    }

    public void setWorldBlockTransform(WorldBlockTransform worldBlockTransform) {
        this.worldBlockTransform = worldBlockTransform;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getPoint1() {
        return point1;
    }

    public Location getPoint2() {
        return point2;
    }

    public void setPoint1(Location point1) {
        this.point1 = point1;
    }

    public void setPoint2(Location point2) {
        this.point2 = point2;
    }
}
