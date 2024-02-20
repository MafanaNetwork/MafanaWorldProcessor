package me.tahacheji.mafana.command;

import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.commandExecutor.Command;
import me.tahacheji.mafana.commandExecutor.paramter.Param;
import me.tahacheji.mafana.data.PlayerEditor;
import me.tahacheji.mafana.processor.*;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class WorldCommands {

    @Command(names = {"mwp edit", "mafanaworldprocessor edit"}, permission = "mafana.admin", playerOnly = true)
    public void setPlayerEditor(Player player) {
        if (MafanaWorldProcessor.getInstance().getPlayerEditor(player) != null) {
            player.sendMessage(ChatColor.GREEN + "You are already a editor.");
        } else {
            MafanaWorldProcessor.getInstance().getPlayerEditorList().add(new PlayerEditor(player));
            player.sendMessage(ChatColor.GREEN + "You are now set as editor.");
        }
    }

    @Command(names = {"mwp igl", "mafanaworldprocessor igl"}, permission = "mafana.admin", playerOnly = true)
    public void setIgnoreLocation(Player player, @Param(name = "ID") String id) {
        PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(player);
        if (playerEditor != null) {
            if(playerEditor.getPoint1() != null && playerEditor.getPoint2() != null) {
                Location p1 = playerEditor.getPoint1();
                Location p2 = playerEditor.getPoint2();
                MafanaWorldProcessor.getInstance().getIgnoreLocationDatabase().setIgnoredLocation(id, p1, p2);
            }
        }
    }

    @Command(names = {"mwp setBiome", "mafanaworldprocessor setBiome"}, permission = "mafana.admin")
    public void setBiome(Player player, @Param(name = "location1") Location location1, @Param(name = "location2") Location location2 , @Param(name = "divisions") int divisions, @Param(name = "delayBetweenCubesTicks") int delayBetweenCubesTicks, @Param(name = "biome") Biome biome ) {
        WorldBiomeSetter worldBiomeSetter = new WorldBiomeSetter(player.getWorld(), location1, location2, divisions, delayBetweenCubesTicks, biome);
        CompletableFuture<Void> w = worldBiomeSetter.setBiomeAsync();
        w.thenAccept(e -> {
           player.sendMessage(ChatColor.GREEN + "Updated Biome");
        });
    }


    @Command(names = {"mwp build", "mafanaworldprocessor build"}, permission = "mafana.admin", playerOnly = true)
    public void build(Player player, @Param(name = "upperBuildID") Build upper, @Param(name = "underBuildID") Build under, @Param(name = "location") Location location, @Param(name = "rotateX") double rotateX, @Param(name = "rotateY") double rotateY, @Param(name = "rotateZ") double rotateZ, @Param(name = "ticks") int x, @Param(name = "batch") int batch, @Param(name = "transparent") boolean transparent, @Param(name = "packet") boolean packet) {
        try {
            CompletableFuture.supplyAsync(() -> {
                try {
                    List<WorldBlock> upperBuild = new ArrayList<>(MafanaWorldProcessor.getInstance().getWorldBlockData().getBlocks(upper.getID()).get());
                    List<WorldBlock> underBuild = new ArrayList<>(MafanaWorldProcessor.getInstance().getWorldBlockData().getBlocks(under.getID()).get());
                    Location l = new Location(Bukkit.getWorld("world"), location.getX(), location.getY(), location.getZ());
                    WorldBlockSetter worldBlockSetter = new WorldBlockSetter(upperBuild, underBuild, upper.getID(), under.getID(), l, x, batch);
                    worldBlockSetter.setRotateX(rotateX);
                    worldBlockSetter.setRotateY(rotateY);
                    worldBlockSetter.setRotateZ(rotateZ);
                    worldBlockSetter.setPacket(packet);
                    worldBlockSetter.setTransparent(transparent);
                    worldBlockSetter.setWorld("world");
                    CompletableFuture<HashMap<Build, Build>> future = worldBlockSetter.placeBlockAtLocation();
                    future.thenAcceptAsync(buildHashMap -> {
                        player.sendMessage("Build completed successfully!");
                    }).exceptionally(ex -> {
                        player.sendMessage("An error occurred while building: " + ex.getMessage());
                        ex.printStackTrace();
                        return null;
                    });
                    PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(player);
                    if (playerEditor != null) {
                        playerEditor.addWorldBlockSetter(worldBlockSetter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("An unexpected error occurred: " + e.getMessage());
                }
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage("An unexpected error occurred: " + e.getMessage());
        }
    }


    @Command(names = {"mwp undoBuild", "mafanaworldprocessor undoBuild"}, permission = "mafana.admin", playerOnly = true)
    public void undoBuild(Player player, @Param(name = "ticks") int x, @Param(name = "batch") int batch) {
        PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(player);
        if (playerEditor != null) {
            WorldBlockSetter mostRecentBuild = playerEditor.getMostRecentWorldBlockSetter();
            if (mostRecentBuild != null) {
                CompletableFuture<List<WorldBlock>> undoFuture = mostRecentBuild.undoBuild(x, batch);

                undoFuture.thenAcceptAsync(undoneBuildsList -> {
                    player.sendMessage(ChatColor.GREEN + "Undid builds successfully!");
                });

                playerEditor.getWorldBlockSetters().remove(mostRecentBuild);
            } else {
                player.sendMessage(ChatColor.RED + "No recent builds to undo.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You are not an editor.");
        }
    }


    @Command(names = {"mwp undoPlace", "mafanaworldprocessor undoPlace"}, permission = "mafana.admin", playerOnly = true)
    public void undoPlace(Player player) {
        PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(player);
        if (playerEditor != null) {
            WorldBlockPlacer mostRecentBuild = playerEditor.getMostRecentWorldBlockPlacer();
            if (mostRecentBuild != null) {
                CompletableFuture<List<WorldBlock>> undoFuture = mostRecentBuild.undoPlacing();

                undoFuture.thenAcceptAsync(undoneBuildsList -> {
                    player.sendMessage(ChatColor.GREEN + "Undid builds successfully!");
                });

                playerEditor.getWorldBlockPlacers().remove(mostRecentBuild);
            } else {
                player.sendMessage(ChatColor.RED + "No recent builds to undo.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You are not an editor.");
        }

    }



    @Command(names = {"mwp undoReplace", "mafanaworldprocessor undoReplace"}, permission = "mafana.admin", playerOnly = true)
    public void undoReplace(Player player) {
        PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(player);
        if (playerEditor != null) {
            WorldBlockReplace mostRecentBuild = playerEditor.getMostRecentWorldBlockReplace();
            if (mostRecentBuild != null) {
                CompletableFuture<Void> undoFuture = mostRecentBuild.undoReplaceBlocksAsync();

                undoFuture.thenAcceptAsync(undoneBuildsList -> {
                    player.sendMessage(ChatColor.GREEN + "Undid replacement successfully!");
                });

                playerEditor.getWorldBlockReplaces().remove(mostRecentBuild);
            } else {
                player.sendMessage(ChatColor.RED + "No recent replacement to undo.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You are not an editor.");
        }
    }



    @Command(names = {"mwp setBuild", "mafanaworldprocessor setBuild"}, permission = "mafana.admin", playerOnly = true)
    public void setBuild(Player player, @Param(name = "buildID") String id, @Param(name = "x1") int x1, @Param(name = "y1") int y1, @Param(name = "z1") int z1, @Param(name = "x2") int x2, @Param(name = "y2") int y2, @Param(name = "z2") int z2, @Param(name = "includedBlocks") String includedBlocks, @Param(name = "excludedBlocks") String excludedBlocks) {
        String[] x = excludedBlocks.split(",");
        List<Material> excluded = new ArrayList<>();
        for (String m : x) {
            Material material = Material.matchMaterial(m);
            if (material != null) {
                excluded.add(material);
            }
        }

        String[] z = includedBlocks.split(",");
        List<Material> included = new ArrayList<>();
        for (String m : z) {
            Material material = Material.matchMaterial(m);
            if (material != null) {
                included.add(material);
            }
        }
        player.sendMessage(ChatColor.GREEN + "Set build to ID: " + id);
        MafanaWorldProcessor.getInstance().getWorldBlockData().setBlocks(id, new Location(player.getWorld(), x1, y1, z1), new Location(player.getWorld(), x2, y2, z2), included, excluded);
    }

    @Command(names = {"mwp replaceBuild", "mafanaworldprocessor replace"}, permission = "mafana.admin")
    public void replaceBuilds(Player player, @Param(name = "location1") Location location1, @Param(name = "location2") Location location2, @Param(name = "targetMaterial") me.tahacheji.mafana.commandExecutor.bukkit.Material targetMaterial, @Param(name = "replacedBlock") me.tahacheji.mafana.commandExecutor.bukkit.Material replacedBlock, @Param(name = "percentage") double percentage, @Param(name = "divisions") int divisions, @Param(name = "delayBetweenCubesTicks") int delayBetweenCubesTicks, @Param(name = "packet") boolean packet) {
        location1.setWorld(player.getWorld());
        location2.setWorld(player.getWorld());
        WorldBlockReplace worldBlockReplace = new WorldBlockReplace(location1, location2, targetMaterial.getMaterials(), replacedBlock.getMaterials(), percentage, divisions, delayBetweenCubesTicks, packet);
        CompletableFuture<List<WorldBlock>> future = worldBlockReplace.processCubeAsyncWithPercentage();
        future.thenAccept(worldBlocks -> {
            player.sendMessage(ChatColor.GREEN + "Complete");
        }).exceptionally(e -> {
            player.sendMessage("Error: " + e.getMessage());
            return null;
        });
        PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(player);
        if (playerEditor != null) {
            playerEditor.addWorldBlockReplaces(worldBlockReplace);
        }
    }

    @Command(names = {"mwp placeBuild", "mafanaworldprocessor place"}, permission = "mafana.admin")
    public void placeBuilds(Player player, @Param(name = "location1") Location location1, @Param(name = "location2") Location location2, @Param(name = "canPlaceIn") me.tahacheji.mafana.commandExecutor.bukkit.Material canPlaceIn, @Param(name = "targetMaterial") me.tahacheji.mafana.commandExecutor.bukkit.Material targetMaterial, @Param(name = "targetX") Location targetLocation, @Param(name = "upperBuildID") Build upperBlocks, @Param(name = "underBuildID") Build underBlocks, @Param(name = "batchSize") int batchSize, @Param(name = "percentage") double percentage, @Param(name = "divisions") int divisions, @Param(name = "delayBetweenCubesTicks") int delayBetweenCubesTicks, @Param(name = "amountOfAir") int amountOfAir, @Param(name = "transparent") boolean transparent, @Param(name = "packet") boolean packet) {
        CompletableFuture.supplyAsync(() -> {
            try {
                World world = player.getWorld();
                List<TargetBlock> targetBlocks = new ArrayList<>();
                for (Material m : targetMaterial.getMaterials()) {
                    targetBlocks.add(new TargetBlock(m, (int) targetLocation.getX(), (int) targetLocation.getY(), (int) targetLocation.getZ()));
                }
                List<WorldBlock> upperBuild = new ArrayList<>(MafanaWorldProcessor.getInstance().getWorldBlockData().getBlocks(upperBlocks.getID()).get());
                List<WorldBlock> underBuild = new ArrayList<>(MafanaWorldProcessor.getInstance().getWorldBlockData().getBlocks(underBlocks.getID()).get());
                location1.setWorld(player.getWorld());
                location2.setWorld(player.getWorld());
                WorldBlockPlacer worldBlockPlacer = new WorldBlockPlacer(location1, location2, world, targetBlocks, canPlaceIn.getMaterials(), upperBuild, upperBlocks.getID(), underBuild, underBlocks.getID(), batchSize, divisions, percentage, delayBetweenCubesTicks, amountOfAir, transparent, packet);
                CompletableFuture<List<WorldBlockPlacer.PlacerBuild>> future = worldBlockPlacer.placeBlocks();
                future.thenAccept(placerBuilds -> {
                    player.sendMessage(ChatColor.GREEN + "Complete");
                }).exceptionally(e -> {
                    player.sendMessage("Error: " + e.getMessage());
                    return null;
                });
                PlayerEditor playerEditor = MafanaWorldProcessor.getInstance().getPlayerEditor(player);
                if (playerEditor != null) {
                    playerEditor.addWorldBlockPlacers(worldBlockPlacer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }


    @Command(names = {"mwp removeBuild", "mafanaworldprocessor removeBuild"}, permission = "mafana.admin")
    public void removeBuilds(Player player, @Param(name = "buildID") String buildID) {
        player.sendMessage(ChatColor.GREEN + "Removed build to ID: " + buildID);
        MafanaWorldProcessor.getInstance().getWorldBlockData().removeBuild(buildID);
    }


}
