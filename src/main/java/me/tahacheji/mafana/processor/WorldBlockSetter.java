package me.tahacheji.mafana.processor;

import me.tahacheji.mafana.MafanaWorldProcessor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WorldBlockSetter {

    public CompletableFuture<World> setWorldBlocksAsyncWithDelay(String s, List<WorldBlock> worldBlocks, int batchSize, long delayBetweenBatches) {
        CompletableFuture<World> future = new CompletableFuture<>();
        WorldCreator worldCreator = new WorldCreator(s); // Replace with your desired world name
        worldCreator.generator(new VoidWorldGenerator());
        World voidWorld = worldCreator.createWorld();
        int currentIndex = 0;
        processBatch(voidWorld, worldBlocks, currentIndex, batchSize, delayBetweenBatches, future);

        return future;
    }

    private void processBatch(World voidWorld, List<WorldBlock> worldBlocks, int currentIndex, int batchSize, long delayBetweenBatches, CompletableFuture<World> future) {
        int totalBlocks = worldBlocks.size();
        int endIndex = Math.min(currentIndex + batchSize, totalBlocks);
        List<WorldBlock> batch = worldBlocks.subList(currentIndex, endIndex);

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (WorldBlock worldBlock : batch) {
                        Block block = voidWorld.getBlockAt(worldBlock.getX(), worldBlock.getY(), worldBlock.getZ());
                        System.out.println(worldBlock.getMaterial().name());
                        block.setType(worldBlock.getMaterial());
                    }
                    if (endIndex < totalBlocks) {
                        processBatch(voidWorld, worldBlocks, endIndex, batchSize, delayBetweenBatches, future);
                    } else {
                        future.complete(voidWorld);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                }
            }
        }.runTaskLater(MafanaWorldProcessor.getInstance(), delayBetweenBatches);
    }
}
