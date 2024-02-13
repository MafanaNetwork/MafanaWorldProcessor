package me.tahacheji.mafana.command;

import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.commandExecutor.Command;
import me.tahacheji.mafana.commandExecutor.bukkit.Material;
import me.tahacheji.mafana.commandExecutor.paramter.Param;
import me.tahacheji.mafana.data.PlayerEditor;
import me.tahacheji.mafana.processor.WorldBlock;
import me.tahacheji.mafana.processor.WorldBlockGetter;
import me.tahacheji.mafana.processor.WorldBlockTransform;
import me.tahacheji.mafana.tools.Pointer;
import me.tahacheji.mafana.tools.Shifter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ToolCommands {

    @Command(names = {"mwp tool pointer", "mafanaworldprocessor tool pointer"}, permission = "mafana.admin", playerOnly = true)
    public void givePlayerPointer(Player player) {
        if(MafanaWorldProcessor.getInstance().getPlayerEditor(player) != null) {
            player.getInventory().addItem(new Pointer().getItem());
        }
    }

    @Command(names = {"mwp tool shifter", "mafanaworldprocessor tool shifter"}, permission = "mafana.admin", playerOnly = true)
    public void givePlayerShifter(Player player,
                                  @Param(name = "translateX") int tX, @Param(name = "translateY") int tY, @Param(name = "translateZ") int tZ,
                                  @Param(name = "rotateX") double rX, @Param(name = "rotateY") double rY, @Param(name = "rotateY") double rZ,
                                  @Param(name = "batchSize") int bS, @Param(name = "delayBetweenBatches") int dBB) {
        if(MafanaWorldProcessor.getInstance().getPlayerEditor(player) != null) {
            player.getInventory().addItem(new Shifter(tX, tY, tZ, rX, rY, rZ, bS, dBB).getItem());
        }
    }

    @Command(names = {"mwp clip", "mafanaworldprocessor clip"}, permission = "mafana.admin", playerOnly = true)
    public void setWorldBlock(Player player, @Param(name = "include", required = false) Material include, @Param(name = "exclude", required = false) Material exclude) {
        if(MafanaWorldProcessor.getInstance().getPlayerEditor(player) != null) {
            PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(player);
            if(playerEditor.getPoint1() != null && playerEditor.getPoint2() != null) {
                List<org.bukkit.Material> i = new ArrayList<>();
                List<org.bukkit.Material> e = new ArrayList<>();
                if(include != null) {
                    i.addAll(include.getMaterials());
                }
                if(exclude != null) {
                    e.addAll(exclude.getMaterials());
                }
                WorldBlockGetter worldProcessor = new WorldBlockGetter(null, playerEditor.getPoint1(), playerEditor.getPoint2(), 2, 5L, i, e);

                CompletableFuture<List<List<WorldBlock>>> future = worldProcessor.processCubeAsync();
                future.thenAccept(cubeBlocksList -> {
                    List<WorldBlock> worldBlocks = new ArrayList<>();
                    for(List<WorldBlock> l : cubeBlocksList) {
                        worldBlocks.addAll(l);
                    }
                    player.sendMessage(ChatColor.GREEN + "Clipped!");
                    playerEditor.getWorldBlockTransform().setWorldBlocks(worldBlocks);
                });
            }
        }
    }
}
