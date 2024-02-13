package me.tahacheji.mafana.data;

import me.tahacheji.mafana.processor.BlockManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class PlayerChunkManager {


    private Player player;
    private Chunk chunk;
    private boolean loaded;

    public PlayerChunkManager(Player player, Chunk chunk) {
        this.player = player;
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
