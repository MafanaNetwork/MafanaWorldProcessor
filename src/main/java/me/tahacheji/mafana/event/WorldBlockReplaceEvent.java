package me.tahacheji.mafana.event;

import me.tahacheji.mafana.processor.WorldBlockReplace;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldBlockReplaceEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final WorldBlockReplace worldBlockReplace;
    private boolean cancelled = false;

    public WorldBlockReplaceEvent(WorldBlockReplace worldBlockReplace) {
        this.worldBlockReplace = worldBlockReplace;
    }

    public WorldBlockReplace getWorldBlockReplace() {
        return worldBlockReplace;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
