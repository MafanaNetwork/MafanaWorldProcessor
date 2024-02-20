package me.tahacheji.mafana.data;

import me.tahacheji.mafana.processor.IgnoreLocation;
import me.tahacheji.mafana.processor.WorldBlockUtil;
import me.tahacheji.mafana.util.EncryptionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class IgnoreLocationDatabase extends MySQL {

    SQLGetter sqlGetter = new SQLGetter(this);

    public IgnoreLocationDatabase() {
        super("162.254.145.231", "3306", "51252", "51252", "346a1ef0fc");
    }

    public CompletableFuture<Void> setIgnoredLocation(String id, Location p1, Location p2) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = new EncryptionUtil().stringToUUID(id);
            IgnoreLocation i1 = new IgnoreLocation(p1.getWorld().getName(), (int) p1.getX(), (int) p1.getY(), (int) p1.getZ());
            IgnoreLocation i2 = new IgnoreLocation(p2.getWorld().getName(), (int) p2.getX(), (int) p2.getY(), (int) p2.getZ());
            try {
                if (!sqlGetter.existsAsync(uuid).get()) {
                    sqlGetter.setStringAsync(new DatabaseValue("ID", new EncryptionUtil().stringToUUID(id), id));
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            sqlGetter.setStringAsync(new DatabaseValue("P1", new EncryptionUtil().stringToUUID(id), new WorldBlockUtil().compressLocationsToJson(i1)));
            sqlGetter.setStringAsync(new DatabaseValue("P2", new EncryptionUtil().stringToUUID(id), new WorldBlockUtil().compressLocationsToJson(i2)));
            return null;
        });
    }

    public CompletableFuture<List<IgnoreLocation>> getIgnoredLocation(String id) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid = new EncryptionUtil().stringToUUID(id);
            List<IgnoreLocation> ignoreLocations = new ArrayList<>();
            try {
                ignoreLocations.add(new WorldBlockUtil().decompressJsonToLocations(sqlGetter.getStringAsync(uuid, new DatabaseValue("P1")).get()));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            try {
                ignoreLocations.add(new WorldBlockUtil().decompressJsonToLocations(sqlGetter.getStringAsync(uuid, new DatabaseValue("P2")).get()));
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            return ignoreLocations;
        });
    }

    public CompletableFuture<List<String>> getAllIDs() {
        try {
            return sqlGetter.getAllStringAsync(new DatabaseValue("ID"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CompletableFuture<>();
    }

    @Override
    public void connect() {
        super.connect();
        if (this.isConnected()) sqlGetter.createTable("ignore_locations",
                new DatabaseValue("ID", ""),
                new DatabaseValue("P1", ""),
                new DatabaseValue("P2", ""));
    }

    @Override
    public SQLGetter getSqlGetter() {
        return sqlGetter;
    }
}
