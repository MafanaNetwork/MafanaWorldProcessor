package me.tahacheji.mafana.command;

import me.tahacheji.mafana.commandExecutor.Command;
import me.tahacheji.mafana.commandExecutor.paramter.Param;
import me.tahacheji.mafana.manager.GameMap;
import me.tahacheji.mafana.manager.LocalGameMap;
import me.tahacheji.mafana.util.FileUtil;
import me.tahacheji.mafana.util.VoidWorldGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class MapWorldCommands {

    @Command(names = {"mwp map create"}, permission = "mafana.admin", playerOnly = true)
    public void createMap(Player player, @Param(name = "source") String z, @Param(name = "world") String w, @Param(name = "loadOnInit") boolean t) {
        GameMap gameMap = new LocalGameMap(new File(z, "worlds"), w, t);
        gameMap.load();
        player.teleport(gameMap.getWorld().getSpawnLocation());
        player.sendMessage(ChatColor.GREEN + "Teleported to world: " + w);
    }

    @Command(names = {"mwp map void"}, permission = "mafana.admin", playerOnly = true)
    public void voidWorld(Player player, @Param(name = "world") String z) {
        World x = Bukkit.getWorld(z);
        if (x == null) {
            WorldCreator worldCreator = new WorldCreator(z);
            worldCreator.generator(new VoidWorldGenerator());
            x = worldCreator.createWorld();
        }
        player.teleport(x.getSpawnLocation());
        player.sendMessage(ChatColor.GREEN + "Teleported to world: " + z);
    }

    @Command(names = {"mwp map load"}, permission = "mafana.admin", playerOnly = true)
    public void loadMap(Player player, @Param(name = "source") String z, @Param(name = "world") String w, @Param(name = "loadOnInit") boolean t) {
        GameMap gameMap = new LocalGameMap(new File(z, "worlds"), w, t);
        gameMap.getLocalGameMap().loadMap();
        player.teleport(gameMap.getWorld().getSpawnLocation());
        player.sendMessage(ChatColor.GREEN + "Teleported to world: " + w);
    }

    @Command(names = {"mwp map save"}, permission = "mafana.admin", playerOnly = true)
    public void saveWorld(Player player, @Param(name = "source") String z, @Param(name = "world") String w) throws IOException {
        World world = player.getWorld();
        if (world != null) {
            world.save();
            File sourceWorldFolder = world.getWorldFolder();
            File destinationFolder = new File(z, w);

            if (destinationFolder.exists()) {
                new FileUtil().deleteWorldFolder(destinationFolder);
            }

            // Copy the world folder to the destination folder
            new FileUtil().copyFolder(sourceWorldFolder, destinationFolder);

            player.sendMessage(ChatColor.GREEN + "World copied to: " + destinationFolder.getAbsolutePath());
        } else {
            player.sendMessage(ChatColor.RED + "You are not in a world.");
        }
    }

}
