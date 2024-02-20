package me.tahacheji.mafana;

import me.tahacheji.mafana.command.ToolCommands;
import me.tahacheji.mafana.command.WorldCommands;
import me.tahacheji.mafana.commandExecutor.CommandHandler;
import me.tahacheji.mafana.data.*;
import me.tahacheji.mafana.event.PlayerJoin;
import me.tahacheji.mafana.event.PlayerLoadChunk;
import me.tahacheji.mafana.processor.BlockManager;
import me.tahacheji.mafana.processor.Cube;
import me.tahacheji.mafana.processor.IgnoreLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public final class MafanaWorldProcessor extends JavaPlugin {

    private static MafanaWorldProcessor instance;
    private WorldBlockData worldBlockData;
    private IgnoreLocationDatabase ignoreLocationDatabase;
    private PlayerWorldBlockData playerWorldBlockData;
    private List<PlayerEditor> playerEditorList = new ArrayList<>();

    private List<UUID> coolDownItem = new ArrayList<>();

    private List<BlockManager> blockManagers = new ArrayList<>();
    private List<PlayerChunkManager> playerChunkManagerList = new ArrayList<>();

    private List<Location> ignoreLocations = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        worldBlockData = new WorldBlockData();
        ignoreLocationDatabase = new IgnoreLocationDatabase();
        playerWorldBlockData = new PlayerWorldBlockData();
        playerWorldBlockData.connect();
        worldBlockData.connect();
        ignoreLocationDatabase.connect();
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerLoadChunk(), this);
        CommandHandler.registerCommands(WorldCommands.class, this);
        CommandHandler.registerCommands(ToolCommands.class, this);
        CommandHandler.registerProcessors("me.tahacheji.mafana.processors", this);
        new BukkitRunnable() {
            @Override
            public void run() {
                CompletableFuture.supplyAsync(() -> {
                    try {
                        for(String s : ignoreLocationDatabase.getAllIDs().get()) {
                            if(ignoreLocationDatabase.getIgnoredLocation(s) != null) {
                                IgnoreLocation i1 = ignoreLocationDatabase.getIgnoredLocation(s).get().get(0);
                                IgnoreLocation i2 = ignoreLocationDatabase.getIgnoredLocation(s).get().get(0);

                                Location l1 = new Location(Bukkit.getWorld(i1.getWorld()), i1.getX(), i1.getY(), i1.getZ());
                                Location l2 = new Location(Bukkit.getWorld(i2.getWorld()), i2.getX(), i2.getY(), i2.getZ());

                                ignoreLocations.addAll(new Cube(l1, l2).getLocationsAsync().get());
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                });
            }
        }.runTaskAsynchronously(this);
    }

    @Override
    public void onDisable() {
        worldBlockData.disconnect();
        ignoreLocationDatabase.disconnect();
        playerWorldBlockData.disconnect();
    }

    public PlayerEditor getPlayerEditor(Player player) {
        for(PlayerEditor playerEditor : getPlayerEditorList()) {
            if(playerEditor.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return playerEditor;
            }
        }
        return null;
    }

    public void addToCoolDown(Player player, int seconds) {
        coolDownItem.add(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                coolDownItem.remove(player.getUniqueId());
            }
        }.runTaskLater(this, 20L * seconds);
    }

    public IgnoreLocationDatabase getIgnoreLocationDatabase() {
        return ignoreLocationDatabase;
    }

    public List<Location> getIgnoreLocations() {
        return ignoreLocations;
    }

    public List<BlockManager> getBlockManagers() {
        return blockManagers;
    }

    public List<PlayerChunkManager> getPlayerChunkManagerList() {
        return playerChunkManagerList;
    }

    public boolean isInCoolDown(Player player) {
        return coolDownItem.contains(player.getUniqueId());
    }

    public List<PlayerEditor> getPlayerEditorList() {
        return playerEditorList;
    }

    public WorldBlockData getWorldBlockData() {
        return worldBlockData;
    }

    public PlayerWorldBlockData getPlayerWorldBlockData() {
        return playerWorldBlockData;
    }

    public static MafanaWorldProcessor getInstance() {
        return instance;
    }
}
