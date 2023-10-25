package me.tahacheji.mafana.data;

import me.TahaCheji.mysqlData.MySQL;
import me.TahaCheji.mysqlData.MysqlValue;
import me.TahaCheji.mysqlData.SQLGetter;
import me.tahacheji.mafana.processor.EncryptionUtil;
import me.tahacheji.mafana.processor.WorldBlock;
import me.tahacheji.mafana.processor.WorldBlockGetter;
import me.tahacheji.mafana.processor.WorldBlockUtil;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WorldBlockData extends MySQL {
    SQLGetter sqlGetter = new SQLGetter(this);
    public WorldBlockData() {
        super("localhost", "3306", "51190", "51190", "26c58bbe8e");
    }

    public void addBlocks(String id, Location x, Location y) {
        if(!sqlGetter.exists(new EncryptionUtil().stringToUUID(id))) {
            WorldBlockGetter worldProcessor = new WorldBlockGetter(null, x, y);
            CompletableFuture<List<WorldBlock>> future = worldProcessor.processCubeAsync(2, 20L);
            future.thenAccept(worldBlocks -> {
                UUID uuid = new EncryptionUtil().stringToUUID(id);
                sqlGetter.setString(new MysqlValue("NAME", uuid, id));
                sqlGetter.setString(new MysqlValue("BLOCKS", uuid, new WorldBlockUtil().compressWorldBlocksToJson(worldBlocks)));
            });

        }
    }

    public List<WorldBlock> getBlocks(String id) {
        UUID uuid = new EncryptionUtil().stringToUUID(id);
        if(sqlGetter.exists(uuid)) {
            return new WorldBlockUtil().decompressJsonToWorldBlocks(sqlGetter.getString(uuid, new MysqlValue("BLOCKS")));
        }
        return null;
    }

    public void setBlocks(String id, Location x, Location y) {
        UUID uuid = new EncryptionUtil().stringToUUID(id);
        if(sqlGetter.exists(uuid)) {
            WorldBlockGetter worldProcessor = new WorldBlockGetter(null, x, y);
            CompletableFuture<List<WorldBlock>> future = worldProcessor.processCubeAsync(2, 20L);
            future.thenAccept(worldBlocks -> {
                sqlGetter.setString(new MysqlValue("BLOCKS", uuid, new WorldBlockUtil().compressWorldBlocksToJson(worldBlocks)));
            });

        }
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
