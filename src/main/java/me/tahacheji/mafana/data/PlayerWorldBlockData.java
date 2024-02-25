package me.tahacheji.mafana.data;

import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.processor.BlockManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PlayerWorldBlockData extends MySQL {
    SQLGetter sqlGetter = new SQLGetter(this);

    public PlayerWorldBlockData() {
        super("162.254.145.231", "3306", "51252", "51252", "346a1ef0fc");
    }

    public CompletableFuture<Void> addPlayerWorldBlocks(Player target, String id) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = UUID.randomUUID();
            sqlGetter.setStringAsync(new DatabaseValue("WORLD_BLOCK_ID", uuid, id));
            sqlGetter.setStringAsync(new DatabaseValue("PLAYER_UUID", uuid, target.getUniqueId().toString()));
            sqlGetter.setStringAsync(new DatabaseValue("PACKET_UUID", uuid, uuid.toString()));
            return null;
        });
    }

    public CompletableFuture<BlockManager> getBlockManager(Player target, String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> idList = sqlGetter.getAllStringAsync(new DatabaseValue("WORLD_BLOCK_ID")).join();
                List<String> playerID = sqlGetter.getAllStringAsync(new DatabaseValue("PLAYER_UUID")).join();
                for (String i : idList) {
                    if (i.equalsIgnoreCase(id)) {
                        for (String m : playerID) {
                            if (m.equalsIgnoreCase(target.getUniqueId().toString())) {
                                return new BlockManager(target, MafanaWorldProcessor.getInstance().getWorldBlockData().getBlocks(id).join());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }


    public CompletableFuture<List<BlockManager>> getAllPlayerBlockManager(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            List<BlockManager> blockManagers = new ArrayList<>();
            try {
                for (BlockManager blockManager : getAllBlockManagers().join()) {
                    if (blockManager.getTargetPlayer().getUniqueId().toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                        blockManagers.add(blockManager);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return blockManagers;
        });
    }


    public CompletableFuture<List<BlockManager>> getAllBlockManagers() {
        return CompletableFuture.supplyAsync(() -> {
            List<BlockManager> blockManagers = new ArrayList<>();
            try {
                List<UUID> uuids = sqlGetter.getAllUUIDAsync(new DatabaseValue("UUID")).join();
                List<String> idList = sqlGetter.getAllStringAsync(new DatabaseValue("WORLD_BLOCK_ID")).join();
                List<String> playerID = sqlGetter.getAllStringAsync(new DatabaseValue("PLAYER_UUID")).join();
                for (int i = 0; i < uuids.size(); i++) {
                    String x = idList.get(i);
                    String p = playerID.get(i);
                    if (Bukkit.getPlayer(UUID.fromString(p)) != null) {
                        blockManagers.add(new BlockManager(Bukkit.getPlayer(UUID.fromString(p)), MafanaWorldProcessor.getInstance().getWorldBlockData().getBlocks(x).join()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return blockManagers;
        });
    }


    public CompletableFuture<Void> removePlayerBuild(OfflinePlayer target, String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<UUID> uuids = sqlGetter.getAllUUIDAsync(new DatabaseValue("UUID")).join();
                List<String> idList = sqlGetter.getAllStringAsync(new DatabaseValue("WORLD_BLOCK_ID")).join();
                List<String> playerID = sqlGetter.getAllStringAsync(new DatabaseValue("PLAYER_UUID")).join();
                for (int i = 0; i < uuids.size(); i++) {
                    String x = idList.get(i);
                    String p = playerID.get(i);
                    if (x.equalsIgnoreCase(id) && p.equalsIgnoreCase(target.getUniqueId().toString())) {
                        sqlGetter.removeStringAsync(uuids.get(i).toString(), new DatabaseValue("PACKET_UUID"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }


    public void connect() {
        sqlGetter.createTable("player_packet_blocks",
                new DatabaseValue("WORLD_BLOCK_ID", ""),
                new DatabaseValue("PLAYER_UUID", ""),
                new DatabaseValue("PACKET_UUID", ""));
    }

    @Override
    public SQLGetter getSqlGetter() {
        return sqlGetter;
    }
}
