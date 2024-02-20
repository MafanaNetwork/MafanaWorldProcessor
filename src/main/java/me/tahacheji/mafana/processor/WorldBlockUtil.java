package me.tahacheji.mafana.processor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;

import java.util.List;


public class WorldBlockUtil {

    public List<WorldBlock> decompressJsonToWorldBlocks(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<WorldBlock>>() {}.getType());
    }

    public String compressWorldBlocksToJson(List<WorldBlock> worldBlocks) {
        Gson gson = new Gson();
        return gson.toJson(worldBlocks);
    }

    public IgnoreLocation decompressJsonToLocations(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<IgnoreLocation>() {}.getType());
    }

    public String compressLocationsToJson(IgnoreLocation locationList) {
        Gson gson = new Gson();
        return gson.toJson(locationList);
    }

}
