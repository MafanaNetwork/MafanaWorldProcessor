package me.tahacheji.mafana.manager;

import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class LocalGameMap implements GameMap {
    private final File sourceWorldFolder;
    private File activeWorldFolder;

    private World bukkitWorld;
    private boolean isLoaded;
    private final UUID uuid;

    public LocalGameMap(File worldFolder, String worldName, boolean loadOnInit) {
        this.sourceWorldFolder = new File(worldFolder, worldName);
        if (loadOnInit) load();
        uuid = UUID.randomUUID();
    }

    @Override
    public boolean load() {
        if(isLoaded()) return true;
        activeWorldFolder = new File(Bukkit.getWorldContainer().getParentFile(),
                sourceWorldFolder.getName() + "_active_game_map_" +
                        System.currentTimeMillis());
        try {
            new FileUtil().copyFolder(sourceWorldFolder, activeWorldFolder);
            isLoaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.bukkitWorld = Bukkit.createWorld(new WorldCreator(activeWorldFolder.getName()));

        if(bukkitWorld != null) {
            this.bukkitWorld.setAutoSave(false);

        }
        bukkitWorld.setAutoSave(false);
        MafanaWorldProcessor.getInstance().getActiveMaps().add(this);
        return isLoaded();
    }

    public boolean loadMap() {
        if(isLoaded()) return true;
        activeWorldFolder = new File(Bukkit.getWorldContainer().getParentFile(),
                sourceWorldFolder.getName());
        try {
            new FileUtil().copyFolder(sourceWorldFolder, activeWorldFolder);
            isLoaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.bukkitWorld = Bukkit.createWorld(new WorldCreator(activeWorldFolder.getName()));

        if(bukkitWorld != null) {
            this.bukkitWorld.setAutoSave(false);

        }
        bukkitWorld.setAutoSave(false);
        MafanaWorldProcessor.getInstance().getActiveMaps().add(this);
        return isLoaded();
    }

    @Override
    public void unload() {
        if(bukkitWorld != null) Bukkit.unloadWorld(bukkitWorld, false);
        isLoaded = false;
        bukkitWorld = null;
        activeWorldFolder = null;
    }

    @Override
    public void saveMap() {
        try {
            if(bukkitWorld != null) Bukkit.unloadWorld(bukkitWorld, true);
            new FileUtil().copyFolder(activeWorldFolder, new File(sourceWorldFolder.getParentFile().getPath(), sourceWorldFolder.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        unload();
    }


    @Override
    public boolean restoreFromSource() {
        unload();
        return load();
    }

    public File getActiveWorldFolder() {
        return activeWorldFolder;
    }

    public File getSourceWorldFolder() {
        return sourceWorldFolder;
    }

    public UUID getUuid() {
        return uuid;
    }

    public World getBukkitWorld() {
        return bukkitWorld;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public String getName() {
        return sourceWorldFolder.getName();
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public LocalGameMap getLocalGameMap() {
        return this;
    }

    @Override
    public World getWorld() {
        return bukkitWorld;
    }
}

