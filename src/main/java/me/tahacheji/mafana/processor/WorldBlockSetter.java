package me.tahacheji.mafana.processor;

import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WorldBlockSetter {
    private final World world;
    private final Executor executor;

    public WorldBlockSetter(World world) {
        this.world = world;
        this.executor = Executors.newCachedThreadPool(); // Adjust the executor as needed
    }

    public CompletableFuture<Void> setWorldBlocksAsync(List<WorldBlock> worldBlocks, int batchSize) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        int totalBlocks = worldBlocks.size();
        int currentIndex = 0;

        // Create a separate CompletableFuture for each batch of blocks
        while (currentIndex < totalBlocks) {
            int endIndex = Math.min(currentIndex + batchSize, totalBlocks);
            List<WorldBlock> batch = worldBlocks.subList(currentIndex, endIndex);

            CompletableFuture<Void> batchFuture = CompletableFuture.runAsync(() -> {
                try {
                    for (WorldBlock worldBlock : batch) {
                        Block block = world.getBlockAt(worldBlock.getX(), worldBlock.getY(), worldBlock.getZ());
                        block.setType(worldBlock.getMaterial());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executor);

            currentIndex += batchSize;

            // Combine the batchFuture with the overall future
            future = future.thenCombineAsync(batchFuture, (result1, result2) -> null, executor);
        }

        return future;
    }
}
