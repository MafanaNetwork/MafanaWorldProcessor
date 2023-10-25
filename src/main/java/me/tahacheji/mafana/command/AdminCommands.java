package me.tahacheji.mafana.command;

import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.processor.BlockManager;
import me.tahacheji.mafana.processor.WorldBlock;
import me.tahacheji.mafana.processor.WorldBlockPlacer;
import me.tahacheji.mafana.processor.WorldBlockReplace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AdminCommands implements CommandExecutor {



    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("MWP")) {
            Player player = (Player) sender;
            if(!player.isOp()) {
                return true;
            }
            if(args[0].equalsIgnoreCase("show")) {
                MafanaWorldProcessor.getInstance().blockManager(player).showBlocks();
            }
            if(args[0].equalsIgnoreCase("remove")) {
                MafanaWorldProcessor.getInstance().blockManager(player).hideBlocks();
            }
            if(args[0].equalsIgnoreCase("saveBuildPlayer")) {
                String id = args[1];
                MafanaWorldProcessor.getInstance().getPlayerWorldBlockData().addPlayerWorldBlocks(player, id);
            }
            if(args[0].equalsIgnoreCase("showBuildPlayer")) {
                String id = args[1];
                MafanaWorldProcessor.getInstance().getPlayerWorldBlockData().getBlockManager(player, id).showBlocks();
            }
            if(args[0].equalsIgnoreCase("removeBuildPlayer")) {
                String id = args[1];
                MafanaWorldProcessor.getInstance().getPlayerWorldBlockData().removePlayerBuild(player, id);
            }
            if(args[0].equalsIgnoreCase("saveBuild")) {
                String id = args[1];
                int x1 = Integer.parseInt(args[2]);
                int y1 = Integer.parseInt(args[3]);
                int z1 = Integer.parseInt(args[4]);
                int x2 = Integer.parseInt(args[5]);
                int y2 = Integer.parseInt(args[6]);
                int z2 = Integer.parseInt(args[7]);
                MafanaWorldProcessor.getInstance().getWorldBlockData().addBlocks(id, new Location(player.getWorld(), x1, y1, z1), new Location(player.getWorld(), x2, y2, z2));
            }
            if(args[0].equalsIgnoreCase("setBuild")) {
                String id = args[1];
                int x1 = Integer.parseInt(args[2]);
                int y1 = Integer.parseInt(args[3]);
                int z1 = Integer.parseInt(args[4]);
                int x2 = Integer.parseInt(args[5]);
                int y2 = Integer.parseInt(args[6]);
                int z2 = Integer.parseInt(args[7]);
                MafanaWorldProcessor.getInstance().getWorldBlockData().setBlocks(id, new Location(player.getWorld(), x1, y1, z1), new Location(player.getWorld(), x2, y2, z2));
            }
            if (args.length >= 10 && args[0].equalsIgnoreCase("replace")) {
                Material original = Material.matchMaterial(args[1]);
                Material replacement = Material.matchMaterial(args[2]);

                if (original == null || replacement == null) {
                    sender.sendMessage("Invalid block names.");
                    return true;
                }
                double percentage = Double.parseDouble(args[3]);
                int x1 = Integer.parseInt(args[4]);
                int y1 = Integer.parseInt(args[5]);
                int z1 = Integer.parseInt(args[6]);
                int x2 = Integer.parseInt(args[7]);
                int y2 = Integer.parseInt(args[8]);
                int z2 = Integer.parseInt(args[9]);

                WorldBlockReplace worldBlockReplace = new WorldBlockReplace(new Location(player.getWorld(), x1, y1, z1), new Location(player.getWorld(), x2, y2, z2));
                CompletableFuture<List<WorldBlock>> replaceFuture = worldBlockReplace.processCubeAsyncWithPercentage(
                        original,
                        replacement,
                        2,
                        percentage,
                        40L
                );
                replaceFuture.thenAccept(blocks -> {
                    MafanaWorldProcessor.getInstance().getReplacedBlocks().addAll(worldBlockReplace.getOriginalBlocks());
                });
                return true;
            }
            if (args.length >= 10 && args[0].equalsIgnoreCase("place")) {
                Material original = Material.matchMaterial(args[1]);
                Material onTop = Material.matchMaterial(args[2]);

                if (original == null || onTop == null) {
                    sender.sendMessage("Invalid block names.");
                    return true;
                }
                double percentage = Double.parseDouble(args[3]);
                int x1 = Integer.parseInt(args[4]);
                int y1 = Integer.parseInt(args[5]);
                int z1 = Integer.parseInt(args[6]);
                int x2 = Integer.parseInt(args[7]);
                int y2 = Integer.parseInt(args[8]);
                int z2 = Integer.parseInt(args[9]);

                WorldBlockPlacer worldBlockPlacer = new WorldBlockPlacer(new Location(player.getWorld(), x1, y1, z1), new Location(player.getWorld(), x2, y2, z2));
                CompletableFuture<List<WorldBlock>> replaceFuture = worldBlockPlacer.placeBlocksAsync(
                        original,
                        onTop,
                        2,
                        percentage,
                        40L
                );
                replaceFuture.thenAccept(blocks -> {
                    MafanaWorldProcessor.getInstance().getPlacedBlocks().addAll(blocks);
                });
                return true;
            }
            if(args[0].equalsIgnoreCase("undoReplace")) {
                for(WorldBlock worldBlock : MafanaWorldProcessor.getInstance().getReplacedBlocks()) {
                    Location location = new Location(Bukkit.getWorld("world"), worldBlock.getX(), worldBlock.getY(), worldBlock.getZ());
                    location.getBlock().setType(worldBlock.getMaterial());
                }
                MafanaWorldProcessor.getInstance().getReplacedBlocks().clear();
            }
            if(args[0].equalsIgnoreCase("undoPlace")) {
                for(WorldBlock worldBlock : MafanaWorldProcessor.getInstance().getPlacedBlocks()) {
                    Location location = new Location(Bukkit.getWorld("world"), worldBlock.getX(), worldBlock.getY() + 1, worldBlock.getZ());
                    location.getBlock().setType(Material.AIR);
                }
                MafanaWorldProcessor.getInstance().getPlacedBlocks().clear();
            }
        }
        return false;
    }
}
