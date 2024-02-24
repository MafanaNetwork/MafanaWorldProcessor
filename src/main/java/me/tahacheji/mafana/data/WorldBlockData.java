package me.tahacheji.mafana.data;

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
import java.util.concurrent.ExecutionException;

public class WorldBlockData extends MySQL {
    SQLGetter sqlGetter = new SQLGetter(this);

    public WorldBlockData() {
        super("162.254.145.231", "3306", "51252", "51252", "346a1ef0fc");
    }

    public CompletableFuture<List<WorldBlock>> getBlocks(String id) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = new EncryptionUtil().stringToUUID(id);
            if (sqlGetter.existsAsync(uuid).join()) {
                String blocksString = sqlGetter.getStringAsync(uuid, new DatabaseValue("BLOCKS")).join();
                return new WorldBlockUtil().decompressJsonToWorldBlocks(blocksString);
            }
            return new ArrayList<>();
        });
    }


    public CompletableFuture<Void> setBlocks(String id, Location x, Location y, List<Material> target, List<Material> exclude) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = new EncryptionUtil().stringToUUID(id);
            WorldBlockGetter worldProcessor = new WorldBlockGetter(null, x, y, 2, 10L, target, exclude);

            CompletableFuture<List<List<WorldBlock>>> future = worldProcessor.processCubeAsync();
            future.thenAccept(cubeBlocksList -> {
                if (sqlGetter.existsAsync(uuid).join()) {
                    List<WorldBlock> worldBlocks = new ArrayList<>();
                    for (List<WorldBlock> l : cubeBlocksList) {
                        worldBlocks.addAll(l);
                    }
                    sqlGetter.setStringAsync(new DatabaseValue("BLOCKS", uuid, new WorldBlockUtil().compressWorldBlocksToJson(worldBlocks))).join();
                } else {
                    List<WorldBlock> worldBlocks = new ArrayList<>();
                    for (List<WorldBlock> l : cubeBlocksList) {
                        worldBlocks.addAll(l);
                    }
                    sqlGetter.setStringAsync(new DatabaseValue("NAME", uuid, id)).join();
                    sqlGetter.setStringAsync(new DatabaseValue("BLOCKS", uuid, new WorldBlockUtil().compressWorldBlocksToJson(worldBlocks))).join();
                }

            });
            return null;
        });
    }


    public CompletableFuture<List<String>> getAllNames() {
        try {
            return sqlGetter.getAllStringAsync(new DatabaseValue("NAME"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CompletableFuture<>();
    }

    public List<String> getAllNamesSync() {
        try {
            return sqlGetter.getAllString(new DatabaseValue("NAME"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public CompletableFuture<String> getBuildFromName(String s) {
        return CompletableFuture.supplyAsync(() -> {
            for (String x : getAllNames().join()) {
                if (x.equalsIgnoreCase(s)) {
                    return x;
                }
            }
            return "";
        });
    }

    public String getBuildFromNameSync(String s) {
        for (String x : getAllNamesSync()) {
            if (x.equalsIgnoreCase(s)) {
                return x;
            }
        }
        return "";

    }

    public CompletableFuture<Void> removeBuild(String id) {
        return sqlGetter.removeStringAsync(id, new DatabaseValue("NAME"));
    }

    @Override
    public void connect() {
        super.connect();
        if (this.isConnected()) sqlGetter.createTable("world_blocks",
                new DatabaseValue("NAME", ""),
                new DatabaseValue("BLOCKS", ""));
    }

    @Override
    public SQLGetter getSqlGetter() {
        return sqlGetter;
    }
}
