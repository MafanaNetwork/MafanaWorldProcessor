package me.tahacheji.mafana;

import me.tahacheji.mafana.command.AdminCommands;
import me.tahacheji.mafana.data.PlayerWorldBlockData;
import me.tahacheji.mafana.data.WorldBlockData;
import me.tahacheji.mafana.event.PlayerJoin;
import me.tahacheji.mafana.processor.BlockManager;
import me.tahacheji.mafana.processor.WorldBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public final class MafanaWorldProcessor extends JavaPlugin {

    private static MafanaWorldProcessor instance;
    private List<WorldBlock> replacedBlocks = new ArrayList<>();
    private List<WorldBlock> placedBlocks = new ArrayList<>();
    private WorldBlockData worldBlockData;
    private PlayerWorldBlockData playerWorldBlockData;
    @Override
    public void onEnable() {
        instance = this;
        worldBlockData = new WorldBlockData();
        playerWorldBlockData = new PlayerWorldBlockData();
        playerWorldBlockData.connect();
        worldBlockData.connect();
        getCommand("MWP").setExecutor(new AdminCommands());
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
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

    public WorldBlockData getWorldBlockData() {
        return worldBlockData;
    }

    public BlockManager blockManager(Player player) {
        List<WorldBlock> worldBlocks = new ArrayList<>();
        worldBlocks.add(new WorldBlock(Material.OAK_PLANKS, 2389, 164, 3045));
        worldBlocks.add(new WorldBlock(Material.OAK_PLANKS, 2390, 164, 3045));
        worldBlocks.add(new WorldBlock(Material.OAK_PLANKS, 2391, 164, 3045));
        return new BlockManager(player, worldBlocks);
    }

    public PlayerWorldBlockData getPlayerWorldBlockData() {
        return playerWorldBlockData;
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
