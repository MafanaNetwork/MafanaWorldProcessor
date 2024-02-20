package me.tahacheji.mafana.event;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.data.PlayerChunkManager;
import me.tahacheji.mafana.processor.BlockManager;
import me.tahacheji.mafana.processor.WorldBlock;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerLoadChunk implements Listener {

    private final Map<Player, Set<Chunk>> processedChunksMap;

    public PlayerLoadChunk() {
        this.processedChunksMap = new HashMap<>();
    }

    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent event) {
        Chunk loadedChunk = event.getChunk();
        Player player = event.getPlayer();
        Set<Chunk> processedChunks = processedChunksMap.computeIfAbsent(player, k -> new HashSet<>());
        if (!processedChunks.contains(loadedChunk)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (BlockManager blockManager : MafanaWorldProcessor.getInstance().getBlockManagers()) {
                        if (blockManager.isBlockInChunk(loadedChunk)) {
                            blockManager.showBlocks(player);
                        }
                    }
                    processedChunks.add(loadedChunk);
                }
            }.runTaskAsynchronously(MafanaWorldProcessor.getInstance());
        }
    }

    @EventHandler
    public void onChunkUnload(PlayerChunkUnloadEvent event) {
        Chunk unloadedChunk = event.getChunk();
        Player player = event.getPlayer();
        Set<Chunk> processedChunks = processedChunksMap.get(player);
        if (processedChunks != null) {
            processedChunks.remove(unloadedChunk);
        }

    }
}
