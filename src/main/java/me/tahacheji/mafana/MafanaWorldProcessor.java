package me.tahacheji.mafana;

import org.bukkit.plugin.java.JavaPlugin;

public final class MafanaWorldProcessor extends JavaPlugin {

    private static MafanaWorldProcessor instance;

    @Override
    public void onEnable() {
        instance = this;

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MafanaWorldProcessor getInstance() {
        return instance;
    }
}
