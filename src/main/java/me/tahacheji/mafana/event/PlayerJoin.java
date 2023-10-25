package me.tahacheji.mafana.event;

import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.processor.BlockManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for(BlockManager blockManager : MafanaWorldProcessor.getInstance().getPlayerWorldBlockData().getAllPlayerBlockManager(event.getPlayer())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    blockManager.showBlocks();
                }
            }.runTaskLater(MafanaWorldProcessor.getInstance(), 20L * 2);
        }
    }
}
