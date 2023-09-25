package me.tahacheji.mafana;

import me.tahacheji.mafana.command.AdminCommands;
import me.tahacheji.mafana.processor.WorldBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class MafanaWorldProcessor extends JavaPlugin {

    private static MafanaWorldProcessor instance;
    private List<WorldBlock> replacedBlocks = new ArrayList<>();
    private List<WorldBlock> placedBlocks = new ArrayList<>();
    @Override
    public void onEnable() {
        instance = this;
        getCommand("MWP").setExecutor(new AdminCommands());
    }

    @Override
    public void onDisable() {
        for(WorldBlock worldBlock : replacedBlocks) {
            Location location = new Location(Bukkit.getWorld("world"), worldBlock.getX(), worldBlock.getY(), worldBlock.getZ());
            location.getBlock().setType(worldBlock.getMaterial());
        }
        for(WorldBlock worldBlock : placedBlocks) {
            Location location = new Location(Bukkit.getWorld("world"), worldBlock.getX(), worldBlock.getY() + 1, worldBlock.getZ());
            location.getBlock().setType(Material.AIR);
        }
    }

    public List<WorldBlock> getReplacedBlocks() {
        return replacedBlocks;
    }

    public List<WorldBlock> getPlacedBlocks() {
        return placedBlocks;
    }

    public static MafanaWorldProcessor getInstance() {
        return instance;
    }
}
