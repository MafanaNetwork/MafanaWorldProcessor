package me.tahacheji.mafana.event;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.data.PlayerChunkManager;
import me.tahacheji.mafana.data.PlayerEditor;
import me.tahacheji.mafana.processor.BlockManager;
import me.tahacheji.mafana.processor.WorldBlock;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("mafana.builder")) {
            if (MafanaWorldProcessor.getInstance().getPlayerEditor(player) != null) {
                player.sendMessage(ChatColor.GREEN + "You are already a editor.");
            } else {
                MafanaWorldProcessor.getInstance().getPlayerEditorList().add(new PlayerEditor(player));
                player.sendMessage(ChatColor.GREEN + "You are now set as editor.");
            }
        }
    }

}
