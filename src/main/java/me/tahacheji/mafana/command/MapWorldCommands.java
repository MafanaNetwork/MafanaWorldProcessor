package me.tahacheji.mafana.command;

import me.tahacheji.mafana.commandExecutor.Command;
import me.tahacheji.mafana.commandExecutor.paramter.Param;
import me.tahacheji.mafana.manager.GameMap;
import me.tahacheji.mafana.manager.LocalGameMap;
import me.tahacheji.mafana.util.VoidWorldGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;

public class MapWorldCommands {

    @Command(names = {"mwp map create"}, permission = "mafana.admin", playerOnly = true)
    public void createMap(Player player, @Param(name = "source") String z, @Param(name = "world") String w, @Param(name = "loadOnInit") boolean t) {
        GameMap gameMap = new LocalGameMap(new File(z, "worlds"), w, t);
        gameMap.load();
        player.teleport(gameMap.getWorld().getSpawnLocation());
        player.sendMessage(ChatColor.GREEN + "Teleported to world: " + z);
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

}
