package me.tahacheji.mafana.processor;

import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.util.MathUtil;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class WorldBlockPlacer {

    private Location x;
    private Location y;
    private World world;
    private List<TargetBlock> targetBlock;
    private List<Material> canPlaceIn;
    private List<WorldBlock> upperBuild;
    private String upperID;
    private List<WorldBlock> underBuild;
    private String underID;
    private int batchSize;
    private int divisions;
    private double percentage;
    private long delayBetweenCubesTicks;
    private int amountOfAir;
    boolean transparent;
    boolean packet;

    public List<WorldBlockSetter> worldBlockSetters = new ArrayList<>();

    public WorldBlockPlacer(Location x, Location y, World world, List<TargetBlock> targetBlock, List<Material> canPlaceIn, List<WorldBlock> upperBuild, String upperID, List<WorldBlock> underBuild, String underID, int batchSize, int divisions, double percentage, long delayBetweenCubesTicks, int amountOfAir, boolean transparent, boolean packet) {
        this.x = x;
        this.y = y;
        this.world = world;
        this.targetBlock = targetBlock;
        this.canPlaceIn = canPlaceIn;
        this.upperBuild = upperBuild;
        this.upperID = upperID;
        this.underBuild = underBuild;
        this.underID = underID;
        this.batchSize = batchSize;
        this.divisions = divisions;
        this.percentage = percentage;
        this.delayBetweenCubesTicks = delayBetweenCubesTicks;
        this.amountOfAir = amountOfAir;
        this.transparent = transparent;
        this.packet = packet;
    }

    public CompletableFuture<List<PlacerBuild>> placeBlocks() {
        CompletableFuture<List<PlacerBuild>> future = new CompletableFuture<>();
        List<Cube> cubes = new CubeDivider(x, y).divide(divisions);
        List<PlacerBuild> buildsList = new ArrayList<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (Cube cube : cubes) {
                        if (hasEnoughCanPlaceIn(cube) && hasTargetBlock(cube)) {
                            Bukkit.getScheduler().runTask(MafanaWorldProcessor.getInstance(), () -> {
                                CompletableFuture<List<PlacerBuild>> buildsMapFuture = placeBuildsInCube(cube);
                                buildsMapFuture.thenAccept(buildsList::addAll);
                            });
                        }
                    }
                    future.complete(buildsList);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        }.runTaskAsynchronously(MafanaWorldProcessor.getInstance());
        return future;
    }

    public CompletableFuture<List<PlacerBuild>> placeBuildsInCube(Cube cube) {
        CompletableFuture<List<PlacerBuild>> future = new CompletableFuture<>();
        List<Location> cubeLocations = new ArrayList<>(cube.getLocations());
        Random random = new Random();
        List<PlacerBuild> buildsMap = new ArrayList<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    while (!cubeLocations.isEmpty()) {
                        int randomIndex = random.nextInt(cubeLocations.size());
                        Location targetLocation = cubeLocations.get(randomIndex);
                        if(MafanaWorldProcessor.getInstance().getIgnoreLocations().contains(targetLocation)) {
                            continue;
                        }
                        for (TargetBlock t : targetBlock) {
                            if (targetLocation.add(t.getX(), t.getY(), t.getZ()).getBlock().getType() == t.getMaterial()) {
                                Location l = targetLocation.clone().add(t.getX(), t.getY(), t.getZ());
                                if (canPlaceInLocation(t, l)) {
                                    double randomValue = random.nextDouble() * 100;
                                    if (randomValue <= percentage) {
                                        WorldBlockSetter worldBlockSetter = new WorldBlockSetter(upperBuild, underBuild, upperID, underID, l, batchSize, delayBetweenCubesTicks);
                                        worldBlockSetter.setRotateX(0);
                                        worldBlockSetter.setRotateY(getRotation(t));
                                        worldBlockSetter.setRotateZ(0);
                                        worldBlockSetter.setWorld(world.getName());
                                        worldBlockSetter.setTransparent(transparent);
                                        worldBlockSetter.setPacket(packet);
                                        CompletableFuture<HashMap<Build, Build>> wbs = worldBlockSetter.placeBlockAtLocation();
                                        wbs.thenAcceptAsync(x -> {
                                            Build upper = new Build(upperID, false);
                                            for (Build z : x.keySet()) {
                                                upper.setBlockList(z.getBlockList());
                                            }
                                            Build under = new Build(underID, false);
                                            for (Build z : x.values()) {
                                                under.setBlockList(z.getBlockList());
                                            }
                                            PlacerBuild placerBuild = new PlacerBuild(upper, under);
                                            buildsMap.add(placerBuild);
                                            worldBlockSetters.add(worldBlockSetter);
                                            future.complete(buildsMap);
                                        });
                                    }
                                    break;
                                }
                            }
                        }
                        cubeLocations.remove(randomIndex);
                    }
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        }.runTask(MafanaWorldProcessor.getInstance());
        return future;
    }

    public boolean canPlaceInLocation(TargetBlock t, Location location) {
        if (!getUpperBuild().isEmpty()) {
            int avgX = (int) getUpperBuild().stream().mapToInt(WorldBlock::getX).average().orElse(0);
            int minY = getUpperBuild().stream().mapToInt(WorldBlock::getY).min().orElse(0);
            int avgZ = (int) getUpperBuild().stream().mapToInt(WorldBlock::getZ).average().orElse(0);

            for (WorldBlock worldBlock : getUpperBuild()) {
                int offsetX = worldBlock.getX() - avgX;
                int offsetY = worldBlock.getY() - minY;
                int offsetZ = worldBlock.getZ() - avgZ;

                int[] rotatedCords = new MathUtil().applyRotations(0, getRotation(t), 0, offsetX, offsetY, offsetZ);

                int targetX = location.getBlockX() + rotatedCords[0];
                int targetY = location.getBlockY() + rotatedCords[1];
                int targetZ = location.getBlockZ() + rotatedCords[2];

                Location newLocation = new Location(world, targetX, targetY, targetZ);
                if (!canPlaceIn.contains(newLocation.getBlock().getType())) {
                    return false;
                }
            }
        }
        return true;
    }

    public CompletableFuture<List<WorldBlock>> undoPlacing() {
        CompletableFuture<List<WorldBlock>> future = new CompletableFuture<>();
        List<WorldBlock> u = new ArrayList<>();
        for (WorldBlockSetter worldBlockSetter : worldBlockSetters) {
            CompletableFuture<List<WorldBlock>> undoFuture = worldBlockSetter.undoBuild(batchSize, (int) delayBetweenCubesTicks);
            undoFuture.thenAcceptAsync(u::addAll);
        }
        future.complete(u);
        return future;
    }

    public boolean hasEnoughCanPlaceIn(Cube cube) {
        int z = upperBuild.size() + underBuild.size();
        for (Location location : cube.getLocations()) {
            Material x = location.getBlock().getType();
            if (canPlaceIn.contains(x)) {
                z--;
            }
        }
        return z <= 0;
    }

    public boolean hasTargetBlock(Cube cube) {
        for (Location location : cube.getLocations()) {
            Material x = location.getBlock().getType();
            for (TargetBlock t : targetBlock) {
                if (t.getMaterial() == x) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getRotation(TargetBlock targetBlock) {
        if(targetBlock.getX() > 0) {
            return 180;
        } else if(targetBlock.getX() < 0) {
            return 0;
        }
        if(targetBlock.getZ() > 0) {
            return 90;
        } else if(targetBlock.getZ() < 0) {
            return 270;
        }
        return 0;
    }

    public Location getX() {
        return x;
    }

    public Location getY() {
        return y;
    }

    public World getWorld() {
        return world;
    }

    public List<TargetBlock> getTargetBlock() {
        return targetBlock;
    }

    public List<Material> getCanPlaceIn() {
        return canPlaceIn;
    }

    public List<WorldBlock> getUpperBuild() {
        return upperBuild;
    }

    public String getUpperID() {
        return upperID;
    }

    public List<WorldBlock> getUnderBuild() {
        return underBuild;
    }

    public String getUnderID() {
        return underID;
    }

    public int getDivisions() {
        return divisions;
    }

    public double getPercentage() {
        return percentage;
    }

    public long getDelayBetweenCubesTicks() {
        return delayBetweenCubesTicks;
    }

    public int getAmountOfAir() {
        return amountOfAir;
    }

    public List<WorldBlockSetter> getWorldBlockSetters() {
        return worldBlockSetters;
    }

    public void setX(Location x) {
        this.x = x;
    }

    public void setY(Location y) {
        this.y = y;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setTargetBlock(List<TargetBlock> targetBlock) {
        this.targetBlock = targetBlock;
    }

    public void setCanPlaceIn(List<Material> canPlaceIn) {
        this.canPlaceIn = canPlaceIn;
    }

    public void setUpperBuild(List<WorldBlock> upperBuild) {
        this.upperBuild = upperBuild;
    }

    public void setUpperID(String upperID) {
        this.upperID = upperID;
    }

    public void setUnderBuild(List<WorldBlock> underBuild) {
        this.underBuild = underBuild;
    }

    public void setUnderID(String underID) {
        this.underID = underID;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setDivisions(int divisions) {
        this.divisions = divisions;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setDelayBetweenCubesTicks(long delayBetweenCubesTicks) {
        this.delayBetweenCubesTicks = delayBetweenCubesTicks;
    }

    public void setAmountOfAir(int amountOfAir) {
        this.amountOfAir = amountOfAir;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public void setPacket(boolean packet) {
        this.packet = packet;
    }

    public void setWorldBlockSetters(List<WorldBlockSetter> worldBlockSetters) {
        this.worldBlockSetters = worldBlockSetters;
    }

    public class PlacerBuild {
        public Build upper;
        public Build under;

        public PlacerBuild(Build upper, Build under) {
            this.upper = upper;
            this.under = under;
        }

        public Build getUnder() {
            return under;
        }

        public Build getUpper() {
            return upper;
        }

        public void setUnder(Build under) {
            this.under = under;
        }

        public void setUpper(Build upper) {
            this.upper = upper;
        }
    }
}
