package me.tahacheji.mafana.manager;

import org.bukkit.World;

import java.util.UUID;

public interface GameMap {
    boolean load();
    void unload();
    boolean restoreFromSource();
    void saveMap();

    boolean isLoaded();
    World getWorld();
    String getName();
    UUID getUUID();
    LocalGameMap getLocalGameMap();
}
