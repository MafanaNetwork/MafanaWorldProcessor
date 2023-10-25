package me.tahacheji.mafana.data;

import me.TahaCheji.mysqlData.MySQL;
import me.TahaCheji.mysqlData.MysqlValue;
import me.TahaCheji.mysqlData.SQLGetter;
import me.tahacheji.mafana.MafanaWorldProcessor;
import me.tahacheji.mafana.processor.BlockManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerWorldBlockData extends MySQL {
    SQLGetter sqlGetter = new SQLGetter(this);
    public PlayerWorldBlockData() {
        super("localhost", "3306", "51190", "51190", "26c58bbe8e");
    }

    public void addPlayerWorldBlocks(Player target, String id) {
        UUID uuid = UUID.randomUUID();
        sqlGetter.setString(new MysqlValue("WORLD_BLOCK_ID", uuid, id));
        sqlGetter.setString(new MysqlValue("PLAYER_UUID", uuid, target.getUniqueId().toString()));
        sqlGetter.setString(new MysqlValue("PACKET_UUID", uuid, uuid.toString()));
    }

    public BlockManager getBlockManager(Player target, String id) {
        try {
            List<String> idList = sqlGetter.getAllString(new MysqlValue("WORLD_BLOCK_ID"));
            List<String> playerID = sqlGetter.getAllString(new MysqlValue("PLAYER_UUID"));
            for(String i : idList) {
                if(i.equalsIgnoreCase(id)) {
                    for(String m : playerID) {
                        if(m.equalsIgnoreCase(target.getUniqueId().toString())) {
                            return new BlockManager(target, MafanaWorldProcessor.getInstance().getWorldBlockData().getBlocks(id));
                        }
                    }
                }
            }
        } catch (Exception ignore) {

        }
        return null;
    }

    public List<BlockManager> getAllPlayerBlockManager(Player player) {
        List<BlockManager> blockManagers = new ArrayList<>();
        for(BlockManager blockManager : getAllBlockManagers()) {
            if(blockManager.getTargetPlayer().getUniqueId().toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                blockManagers.add(blockManager);
            }
        }
        return blockManagers;
    }

    public List<BlockManager> getAllBlockManagers() {
        List<BlockManager> blockManagers = new ArrayList<>();
        try {
            List<UUID> uuids = sqlGetter.getAllUUID(new MysqlValue("UUID"));
            List<String> idList = sqlGetter.getAllString(new MysqlValue("WORLD_BLOCK_ID"));
            List<String> playerID = sqlGetter.getAllString(new MysqlValue("PLAYER_UUID"));
            for (int i = 0; i < uuids.size(); i++) {
                String x = idList.get(i);
                String p = playerID.get(i);
                if(Bukkit.getPlayer(UUID.fromString(p)) != null) {
                    blockManagers.add(new BlockManager(Bukkit.getPlayer(UUID.fromString(p)), MafanaWorldProcessor.getInstance().getWorldBlockData().getBlocks(x)));
                }
            }
        } catch (Exception ignore) {

        }
        return blockManagers;
    }
    public void removePlayerBuild(OfflinePlayer target, String id) {
        try {
            List<UUID> uuids = sqlGetter.getAllUUID(new MysqlValue("UUID"));
            List<String> idList = sqlGetter.getAllString(new MysqlValue("WORLD_BLOCK_ID"));
            List<String> playerID = sqlGetter.getAllString(new MysqlValue("PLAYER_UUID"));
            for (int i = 0; i < uuids.size(); i++) {
                String x = idList.get(i);
                String p = playerID.get(i);
                if(x.equalsIgnoreCase(id)) {
                    if(p.equalsIgnoreCase(target.getUniqueId().toString())) {
                        sqlGetter.removeString(uuids.get(i).toString(), new MysqlValue("PACKET_UUID"));
                    }
                }
            }
        } catch (Exception ignore) {

        }
    }

    @Override
    public void connect() {
        super.connect();
        if (this.isConnected()) sqlGetter.createTable("player_packet_blocks",
                new MysqlValue("WORLD_BLOCK_ID", ""),
                new MysqlValue("PLAYER_UUID", ""),
                new MysqlValue("PACKET_UUID", ""));
    }

    @Override
    public SQLGetter getSqlGetter() {
        return sqlGetter;
    }
}
