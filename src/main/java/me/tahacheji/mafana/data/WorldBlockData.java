package me.tahacheji.mafana.data;

import me.TahaCheji.mysqlData.MySQL;
import me.TahaCheji.mysqlData.MysqlValue;
import me.TahaCheji.mysqlData.SQLGetter;
import me.tahacheji.mafana.processor.WorldBlock;
import me.tahacheji.mafana.processor.WorldBlockGetter;
import me.tahacheji.mafana.processor.WorldBlockUtil;
import me.tahacheji.mafana.util.EncryptionUtil;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WorldBlockData extends MySQL {
    SQLGetter sqlGetter = new SQLGetter(this);
    public WorldBlockData() {
        super("162.254.145.231", "3306", "51252", "51252", "346a1ef0fc");
    }

    public List<WorldBlock> getBlocks(String id) {
        UUID uuid = new EncryptionUtil().stringToUUID(id);
        if(sqlGetter.exists(uuid)) {
            return new WorldBlockUtil().decompressJsonToWorldBlocks(sqlGetter.getString(uuid, new MysqlValue("BLOCKS")));
        }
        return new ArrayList<>();
    }

    public void setBlocks(String id, Location x, Location y, List<Material> target, List<Material> exclude) {
        UUID uuid = new EncryptionUtil().stringToUUID(id);
        WorldBlockGetter worldProcessor = new WorldBlockGetter(null, x, y, 2, 10L, target, exclude);

        CompletableFuture<List<List<WorldBlock>>> future = worldProcessor.processCubeAsync();
        future.thenAccept(cubeBlocksList -> {
            if (sqlGetter.exists(uuid)) {
                List<WorldBlock> worldBlocks = new ArrayList<>();
                for(List<WorldBlock> l : cubeBlocksList) {
                    worldBlocks.addAll(l);
                }
                sqlGetter.setString(new MysqlValue("BLOCKS", uuid, new WorldBlockUtil().compressWorldBlocksToJson(worldBlocks)));
            } else {
                List<WorldBlock> worldBlocks = new ArrayList<>();
                for(List<WorldBlock> l : cubeBlocksList) {
                    worldBlocks.addAll(l);
                }
                sqlGetter.setString(new MysqlValue("NAME", uuid, id));
                sqlGetter.setString(new MysqlValue("BLOCKS", uuid, new WorldBlockUtil().compressWorldBlocksToJson(worldBlocks)));
            }
        });
    }

    public List<String> getAllNames() {
        try {
            return sqlGetter.getAllString(new MysqlValue("NAME"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public String getBuildFromName(String s) {
        for(String x : getAllNames()) {
            if(x.equalsIgnoreCase(s)) {
                return x;
            }
        }
        return "";
    }

    public void removeBuild(String id) {
        sqlGetter.removeString(id, new MysqlValue("NAME"));
    }

    @Override
    public void connect() {
        super.connect();
        if (this.isConnected()) sqlGetter.createTable("world_blocks",
                new MysqlValue("NAME", ""),
                new MysqlValue("BLOCKS", ""));
    }

    @Override
    public SQLGetter getSqlGetter() {
        return sqlGetter;
    }
}
