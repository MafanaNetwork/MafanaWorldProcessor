package me.tahacheji.mafana.processor;

import me.tahacheji.mafana.MafanaWorldProcessor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WorldBlockSetter {
    private final World world;

    public WorldBlockSetter(World world) {
        this.world = world;
    }

    public CompletableFuture<World> setWorldBlocksAsyncWithDelay(List<WorldBlock> worldBlocks, int batchSize, long delayBetweenBatches) {
        CompletableFuture<World> future = new CompletableFuture<>();
        int currentIndex = 0;

        processBatch(worldBlocks, currentIndex, batchSize, delayBetweenBatches, future);

        return future;
    }

    private void processBatch(List<WorldBlock> worldBlocks, int currentIndex, int batchSize, long delayBetweenBatches, CompletableFuture<World> future) {
        int totalBlocks = worldBlocks.size();
        int endIndex = Math.min(currentIndex + batchSize, totalBlocks);
        List<WorldBlock> batch = worldBlocks.subList(currentIndex, endIndex);

        Bukkit.getScheduler().runTaskLater(MafanaWorldProcessor.getInstance(), () -> {
            try {
                for (WorldBlock worldBlock : batch) {
                    Block block = world.getBlockAt(worldBlock.getX(), worldBlock.getY(), worldBlock.getZ());
                    block.setType(worldBlock.getMaterial());
                }
                if (endIndex < totalBlocks) {
                    processBatch(worldBlocks, endIndex, batchSize, delayBetweenBatches, future);
                } else {
                    future.complete(world);
                }
            } catch (Exception e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        }, delayBetweenBatches);
    }
}
