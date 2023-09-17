package me.tahacheji.mafana.processor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class WorldBlockUtil {

    public List<WorldBlock> decompressJsonToWorldBlocks(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<WorldBlock>>() {}.getType());
    }

    public String compressWorldBlocksToJson(List<WorldBlock> worldBlocks) {
        Gson gson = new Gson();
        return gson.toJson(worldBlocks);
    }
}
