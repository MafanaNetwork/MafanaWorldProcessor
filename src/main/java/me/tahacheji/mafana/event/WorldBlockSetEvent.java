package me.tahacheji.mafana.event;

import me.tahacheji.mafana.processor.WorldBlockSetter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldBlockSetEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final WorldBlockSetter worldBlockSetter;
    private boolean cancelled = false;

    public WorldBlockSetEvent(WorldBlockSetter worldBlockSetter) {
        this.worldBlockSetter = worldBlockSetter;
    }

    public WorldBlockSetter getWorldBlockSetter() {
        return worldBlockSetter;
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
